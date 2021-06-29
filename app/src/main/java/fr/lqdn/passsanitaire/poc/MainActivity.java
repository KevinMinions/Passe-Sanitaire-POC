package fr.lqdn.passsanitaire.poc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import fr.lqdn.passsanitaire.poc.ehc.ValueSets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.google.android.material.snackbar.Snackbar;

import se.digg.dgc.encoding.Base45;
import se.digg.dgc.encoding.DGCConstants;
import se.digg.dgc.encoding.Zlib;
import se.digg.dgc.payload.v1.DigitalCovidCertificate;
import se.digg.dgc.payload.v1.RecoveryEntry;
import se.digg.dgc.payload.v1.TestEntry;
import se.digg.dgc.payload.v1.VaccinationEntry;
import se.digg.dgc.signatures.cose.CoseSign1_Object;
import se.digg.dgc.signatures.cwt.Cwt;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String CEV_PREFIX = "DC04";
    public static final String CEV_TYPE_VIROLOGICAL = "B2";
    public static final String CEV_TYPE_VACCINATION = "L1";

    public static final String VIROLOGICAL_CSV_FILE = "virological_certificates.csv";
    public static final String VACCINATION_CSV_FILE = "vaccination_certificates.csv";

    private static final int VIROLOGICAL_EXPORT_REQUEST_CODE = 2;
    private static final int VACCINATION_EXPORT_REQUEST_CODE = 3;

    private static final String CSV_SEPARATOR = ",";

    private View    mLayout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.main_layout);

        findViewById(R.id.button_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });

        findViewById(R.id.button_export_virological).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestExportFile(VIROLOGICAL_CSV_FILE, VIROLOGICAL_EXPORT_REQUEST_CODE);
            }
        });

        findViewById(R.id.button_export_vaccination).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestExportFile(VACCINATION_CSV_FILE, VACCINATION_EXPORT_REQUEST_CODE);
            }
        });
    }

    private void startCamera() {
        IntentIntegrator integrator = new IntentIntegrator(this);

        integrator.setDesiredBarcodeFormats(IntentIntegrator.DATA_MATRIX, IntentIntegrator.QR_CODE);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setPrompt(getResources().getString(R.string.scan_certificate));
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch(requestCode) {
                case IntentIntegrator.REQUEST_CODE:
                    processScanResult(requestCode, resultCode, intent);
                    break;
                case VIROLOGICAL_EXPORT_REQUEST_CODE:
                    processExportResult(requestCode, resultCode, intent);
                    break;
                case VACCINATION_EXPORT_REQUEST_CODE:
                    processExportResult(requestCode, resultCode, intent);
                    break;
        }
    }

    public void processScanResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String data = result.getContents();
            if (data != null) {
                parseData(data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void parseData(String data) {
        Pattern pattern;
        Matcher matcher;

        /* Parse CEV or EDCC headers */
        pattern = Pattern.compile(
            "^(" + CEV_PREFIX + "|" + DGCConstants.DGC_V1_HEADER + ")"
        );
        matcher = pattern.matcher(data);

        if (matcher.find()) {
            if (matcher.group(1).equals(CEV_PREFIX)) {
                parseCev(data);
            } else {
                parseEdcc(data);
            }
        } else {
            Toast.makeText(context, R.string.invalid_certificate, Toast.LENGTH_SHORT).show();
            startCamera();
        }
    }

    private void parseEdcc(String input) {
        if (input.startsWith(DGCConstants.DGC_V1_HEADER)) input = input.substring(DGCConstants.DGC_V1_HEADER.length());

        final byte[] compressedSignedCwt = Base45.getDecoder().decode(input);

        try {
            final byte[] uncompressedSignedCwt = Zlib.decompress(compressedSignedCwt, false);
            final CoseSign1_Object coseObject = CoseSign1_Object.decode(uncompressedSignedCwt);

            final Cwt cwt = coseObject.getCwt();
            final DigitalCovidCertificate edcc = DigitalCovidCertificate.decode(cwt.getDgcV1());

            List<VaccinationEntry> vaccinationGroup = edcc.getV();
            List<TestEntry>        virologicalGroup = edcc.getT();
            List<RecoveryEntry>    recoveryGroup    = edcc.getR();

            if ((vaccinationGroup == null || vaccinationGroup.isEmpty()) && 
                (virologicalGroup == null || virologicalGroup.isEmpty()) && 
                (recoveryGroup    == null || recoveryGroup.isEmpty())) {
                throw new Exception("No data in certificate...");
            }

            String ca            = cwt.getIssuer();
            String signatureDate = calculateDate(cwt.getIssuedAt().atZone(ZoneOffset.UTC).toLocalDate());
            String firstNames    = edcc.getNam().getGn();
            String lastName      = edcc.getNam().getFn();
            String birthDate     = edcc.getDateOfBirth().toString();

            if (vaccinationGroup != null && !vaccinationGroup.isEmpty()) {
                VaccinationEntry vaccination = vaccinationGroup.get(0);

                String certId                    = vaccination.getCi();
                String country                   = vaccination.getCo();
                String disease                   = ValueSets.getValueFromKey(ValueSets.DISEASE_AGENT_TARGETED,        vaccination.getTg());
                String prophylaxis               = ValueSets.getValueFromKey(ValueSets.VACCINE_PROPHYLAXIS,           vaccination.getVp());;
                String vaccineName               = ValueSets.getValueFromKey(ValueSets.VACCINES_COVID19_NAMES,        vaccination.getMp());
                String vaccineManufacter         = ValueSets.getValueFromKey(ValueSets.VACCINES_COVID19_AUTH_HOLDERS, vaccination.getMa());
                String lastVaccinationStatusRank = String.valueOf(vaccination.getDn());
                String requiredVaccinationStatus = String.valueOf(vaccination.getSd());
                String lastVaccinationStatusDate = calculateDate(vaccination.getDt());
                String vaccinationStatus         = (requiredVaccinationStatus == lastVaccinationStatusRank) ? "TE" : "CO";

                saveVaccination(ca, certId, signatureDate, country, firstNames, lastName, birthDate, disease, prophylaxis,
                                vaccineName, vaccineManufacter, lastVaccinationStatusRank, requiredVaccinationStatus,
                                lastVaccinationStatusDate, vaccinationStatus);
            } else if (virologicalGroup != null && !virologicalGroup.isEmpty()) {
                TestEntry test = virologicalGroup.get(0);

                String certId       = test.getCi();
                String country      = test.getCo();
                String analysisCode = ValueSets.getValueFromKey(ValueSets.COVID19_LAB_TEST_TYPE, test.getTt());
                String resultCode   = ValueSets.getValueFromKey(ValueSets.COVID19_LAB_RESULT,    test.getTr());
                String dayAndHour   = calculateDate(test.getSc().atZone(ZoneOffset.UTC).toLocalDate());

                saveVirological(ca, certId, signatureDate, country, firstNames, lastName, birthDate, "U", analysisCode,
                                resultCode, dayAndHour);
            } else if (recoveryGroup != null && !recoveryGroup.isEmpty()) {
                Toast.makeText(context, "EU recovery certificates not supported yet.", Toast.LENGTH_SHORT).show();
                startCamera();
            } else {
                throw new Exception("Invalid data");
            }
        } catch (Exception e) {
            Log.w(TAG, e);
            Toast.makeText(context, R.string.invalid_certificate, Toast.LENGTH_SHORT).show();
            startCamera();
        }
    }

    private void parseCev(String cev) {
        Pattern pattern;
        Matcher matcher;

        /* Parse CEV header */
        pattern = Pattern.compile(
            "^DC"           + // CEV identifier
            "04"            + // CEV version (must be 04)
            "([A-Z0-9]{4})" + // CA identifier
            "([A-Z0-9]{4})" + // Cert ID
            "([A-F0-9]{4})" + // Creation date
            "([A-F0-9]{4})" + // Signature date
            "([A-Z0-9]{2})" + // Document type
            "([A-Z0-9]{2})" + // Perimeter
            "([A-Z]{2})"      // Country
        );
        matcher = pattern.matcher(cev);

        if (matcher.find()) {
            String ca            = matcher.group(1);
            String certId        = matcher.group(2);
            String signatureDate = calculateDateFromHexa(matcher.group(4));
            String documentType  = matcher.group(5);
            String perimeterId   = matcher.group(6);
            String country       = matcher.group(7);

            switch(documentType) {
                case CEV_TYPE_VIROLOGICAL:
                    parseCevVirological(cev, ca, certId, signatureDate, country);
                    break;
                case CEV_TYPE_VACCINATION:
                    parseCevVaccination(cev, ca, certId, signatureDate, country);
                    break;
                default:
                    Toast.makeText(context, R.string.invalid_certificate, Toast.LENGTH_SHORT).show();
                    startCamera();
            }
        } else {
            Toast.makeText(context, R.string.invalid_certificate, Toast.LENGTH_SHORT).show();
            startCamera();
        }
    }

    private void parseCevVaccination(String cev, String ca, String certId,
                                     String signatureDate, String country)
    {
        Pattern pattern;
        Matcher matcher;

        /* Parse a vaccination certificate */
        pattern = Pattern.compile(
            "^[A-Z0-9]{26}"                                       + // Header
            "L0([A-Z -./]{0,80})[" + (char)30 + (char)29 + "]"    + // Last name
            "L1([A-Z -./]{0,80})[" + (char)30 + (char)29 + "]"    + // First names
            "L2([0-9]{8})"                                        + // Birth date
            "L3([A-Z0-9 -./]{0,30})[" + (char)30 + (char)29 + "]" + // Disease
            "L4([A-Z0-9 -./]{5,15})[" + (char)30 + (char)29 + "]" + // Prophylactic agent
            "L5([A-Z0-9 -./]{5,35})[" + (char)30 + (char)29 + "]" + // Vaccine name
            "L6([A-Z0-9 -./]{5,35})[" + (char)30 + (char)29 + "]" + // Vaccine manufacter
            "L7([0-9])"                                           + // Last vaccine status rank
            "L8([0-9])"                                           + // Required vaccination status
            "L9([0-9]{8})"                                        + // Date of last vaccination status
            "LA([A-Z]{2})"                                          // Vaccination status
        );

        matcher = pattern.matcher(cev);

        if (matcher.find()) {
            String lastName                  = matcher.group(1);
            String firstNames                = matcher.group(2);
            String birthDate                 = calculateDateFromFormat(matcher.group(3), "ddMMyyyy");
            String disease                   = matcher.group(4);
            String prophylaxis               = matcher.group(5);
            String vaccineName               = matcher.group(6);
            String vaccineManufacter         = matcher.group(7);
            String lastVaccinationStatusRank = matcher.group(8);
            String requiredVaccinationStatus = matcher.group(9);
            String lastVaccinationStatusDate = calculateDateFromFormat(matcher.group(10), "ddMMyyyy");
            String vaccinationStatus         = matcher.group(11);

            saveVaccination(ca, certId, signatureDate, country, firstNames, lastName, birthDate, disease,
                            prophylaxis, vaccineName, vaccineManufacter, lastVaccinationStatusRank,
                            requiredVaccinationStatus, lastVaccinationStatusDate, vaccinationStatus);
        } else {
            Toast.makeText(context, R.string.invalid_vaccination_certificate, Toast.LENGTH_SHORT).show();
            startCamera();
        }
    }

    private void parseCevVirological(String cev, String ca, String certId, String signatureDate, String country) {
        Pattern pattern;
        Matcher matcher;

        /* Parse a virological certificate */
        pattern = Pattern.compile(
            "^[A-Z0-9]{26}"                                  + // Header
            "F0([A-Z /]{0,60})[" + (char)30 + (char)29 + "]" + // First names
            "F1([A-Z ]{0,38})[" + (char)30 + (char)29 + "]"  + // Last name
            "F2([0-9]{8})"                                   + // Birth date
            "F3([MFU])"                                      + // Gender (female, male, unknown)
            "F4([0-9-]{3,7})[" + (char)30 + (char)29 + "]"   + // Analysis code from on loinc.org
            "F5([PNIX])"                                     + // Result code (P positive, N negative, I unknown, X failed)
            "F6([0-9]{12})"                                    // Day and hour of test
        );

        matcher = pattern.matcher(cev);

        if (matcher.find()) {
            String firstNames   = matcher.group(1);
            String lastName     = matcher.group(2);
            String birthDate    = calculateDateFromFormat(matcher.group(3), "ddMMyyyy");
            String gender       = matcher.group(4);
            String analysisCode = matcher.group(5);
            String resultCode   = matcher.group(6);
            String dayAndHour   = calculateDateTimeFromFormat(matcher.group(7), "ddMMyyyyHHmm");

            saveVirological(ca, certId, signatureDate, country, firstNames, lastName, birthDate, gender,
                            analysisCode, resultCode, dayAndHour);
        } else {
            Toast.makeText(context, R.string.invalid_virological_certificate, Toast.LENGTH_SHORT).show();
            startCamera();
        }
    }

    private void saveVaccination(String ca, String certId, String signatureDate, String country, String firstNames,
                                 String lastName, String birthDate, String disease, String prophylaxis, String vaccineName,
                                 String vaccineManufacter, String lastVaccineStatusRank, String requiredVaccineStatus,
                                 String lastVaccineStatusDate, String vaccineStatus)
    {
        String fileContents = '"' + signatureDate + '"' + CSV_SEPARATOR +
                              '"' + firstNames + '"' + CSV_SEPARATOR +
                              '"' + lastName + '"' + CSV_SEPARATOR +
                              '"' + birthDate + '"' + CSV_SEPARATOR +
                              '"' + disease + '"' + CSV_SEPARATOR +
                              '"' + prophylaxis + '"' + CSV_SEPARATOR +
                              '"' + vaccineName + '"' + CSV_SEPARATOR +
                              '"' + vaccineManufacter + '"' + CSV_SEPARATOR +
                              '"' + lastVaccineStatusRank + '"' + CSV_SEPARATOR +
                              '"' + requiredVaccineStatus + '"' + CSV_SEPARATOR +
                              '"' + lastVaccineStatusDate + '"' + CSV_SEPARATOR +
                              '"' + vaccineStatus + '"' + "\n";

        save(VACCINATION_CSV_FILE, fileContents, firstNames, lastName);
    }

    private void saveVirological(String ca, String certId, String signatureDate, String country, String firstNames,
                                 String lastName, String birthDate, String gender, String analysisCode, String resultCode,
                                 String dayAndHour)
    {
        String fileContents = '"' + signatureDate + '"' + CSV_SEPARATOR +
                              '"' + firstNames + '"' + CSV_SEPARATOR +
                              '"' + lastName + '"' + CSV_SEPARATOR +
                              '"' + birthDate + '"' + CSV_SEPARATOR +
                              '"' + gender + '"' + CSV_SEPARATOR +
                              '"' + analysisCode + '"' + CSV_SEPARATOR +
                              '"' + resultCode + '"' + CSV_SEPARATOR +
                              '"' + dayAndHour + '"' + "\n";

        save(VIROLOGICAL_CSV_FILE, fileContents, firstNames, lastName);
    }

    private void save(String fileName, String fileContents, String firstNames, String lastName) {
        try (FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE|Context.MODE_APPEND)) {
            fileOutputStream.write(fileContents.getBytes());
            fileOutputStream.close();
            Toast.makeText(context, context.getString(R.string.certificate_for_added, firstNames, lastName), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.w(TAG, e);
            Toast.makeText(context, R.string.cannot_save_certificate, Toast.LENGTH_SHORT).show();
        }

        startCamera();
    }

    private static String calculateDateFromHexa(String date) {
        int days = Integer.parseInt(date, 16);
        LocalDate localDate = LocalDate.of(2000,01,01).plusDays(days);
        return localDate.getYear() + "-" +
               String.format("%02d", localDate.getMonthValue()) + "-" +
               String.format("%02d", localDate.getDayOfMonth());
    }

    private static String calculateDate(LocalDate localDate) {
        return localDate.getYear() + "-" +
               String.format("%02d", localDate.getMonthValue()) + "-" +
               String.format("%02d", localDate.getDayOfMonth());
    }

    private static String calculateDateFromFormat(String date, String format) {
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
            return calculateDate(localDate);
        } catch (Exception e) {
            Log.w(TAG, e);
            return date;
        }
    }

    private static String calculateDateTimeFromFormat(String date, String format) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
            return localDateTime.getYear() + "-" +
                   String.format("%02d", localDateTime.getMonthValue()) + "-" +
                   String.format("%02d", localDateTime.getDayOfMonth()) + " " +
                   String.format("%02d", localDateTime.getHour()) + ":" +
                   String.format("%02d", localDateTime.getMinute());
        } catch (Exception e) {
            Log.w(TAG, e);
            Log.i(TAG, "Format: " + format);
            return date;
        }
    }

    private void requestExportFile(String fileName, int requestCode) {
        // Check if the file permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, export file
            exportFile(fileName, requestCode);
        } else {
            // Permission is missing and must be requested.
            requestFilePermission(fileName, requestCode);
        }
    }

    private void requestFilePermission(String fileName, int requestCode) {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(mLayout, R.string.file_permission_is_required_to_export_data_to_internal_storage,
                          Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
            }).show();
        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {
        if (requestCode == VACCINATION_EXPORT_REQUEST_CODE ||
            requestCode == VIROLOGICAL_EXPORT_REQUEST_CODE)
        {
            // Request for file permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                String fileName = (requestCode == VACCINATION_EXPORT_REQUEST_CODE) ? VACCINATION_CSV_FILE : VIROLOGICAL_CSV_FILE;
                exportFile(fileName, requestCode);
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, R.string.file_permission_is_required_but_has_been_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void exportFile(String fileName, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        startActivityForResult(intent, requestCode);
    }

    public void processExportResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                Uri uri = null;
                if (intent != null) {
                    uri = intent.getData();
                    String fileName = (requestCode == VACCINATION_EXPORT_REQUEST_CODE) ? VACCINATION_CSV_FILE : VIROLOGICAL_CSV_FILE;
                    try (InputStream in = (FileInputStream) context.openFileInput(fileName)) {
                        try (OutputStream out = getApplicationContext().getContentResolver().openOutputStream(uri)) {
                            // Transfer bytes from in to out
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                        }
                    }
                    Snackbar.make(mLayout, R.string.data_saved_to_storage_successfully, Snackbar.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.w(TAG, e);
                Toast.makeText(context, R.string.cannot_export_file, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
