package fr.lqdn.passsanitaire.poc.ehc;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValueSets {
    public static final String VERSION = "1.3.0"; // https://github.com/ehn-dcc-development/ehn-dcc-valuesets/tree/release/1.3.0

    public static final List<Pair<String,String>> DISEASE_AGENT_TARGETED = new ArrayList<>(Arrays.asList(
        Pair.create("840539006", "COVID-19")
    ));

    public static final List<Pair<String,String>> COVID19_LAB_RESULT = new ArrayList<>(Arrays.asList(
        Pair.create("260373001", "Detected"),
        Pair.create("260415000", "Not detected")
    ));

    public static final List<Pair<String,String>> COVID19_LAB_TEST_MANUFACTURER_AND_NAME = new ArrayList<>(Arrays.asList(
        Pair.create("1097", "Quidel Corporation, Sofia SARS Antigen FIA"),
        Pair.create("1114", "Sugentech, Inc, SGTi-flex COVID-19 Ag"),
        Pair.create("1144", "Green Cross Medical Science Corp., GENEDIA W COVID-19 Ag"),
        Pair.create("1162", "Nal von minden GmbH, NADAL COVID-19 Ag Test"),
        Pair.create("1173", "CerTest Biotec, CerTest SARS-CoV-2 Card test"),
        Pair.create("1180", "MEDsan GmbH, MEDsan SARS-CoV-2 Antigen Rapid Test"),
        Pair.create("1190", "möLab, COVID-19 Rapid Antigen Test"),
        Pair.create("1199", "Oncosem Onkolojik Sistemler San. ve Tic. A.S., CAT"),
        Pair.create("1215", "Hangzhou Laihe Biotech Co., Ltd, LYHER Novel Coronavirus (COVID-19) Antigen Test Kit(Colloidal Gold)"),
        Pair.create("1218", "Siemens Healthineers, CLINITEST Rapid Covid-19 Antigen Test"),
        Pair.create("1223", "BIOSYNEX S.A., BIOSYNEX COVID-19 Ag BSS"),
        Pair.create("1225", "DDS DIAGNOSTIC, Test Rapid Covid-19 Antigen (tampon nazofaringian)"),
        Pair.create("1232", "Abbott Rapid Diagnostics, Panbio COVID-19 Ag Rapid Test"),
        Pair.create("1236", "BTNX Inc, Rapid Response COVID-19 Antigen Rapid Test"),
        Pair.create("1244", "GenBody, Inc, Genbody COVID-19 Ag Test"),
        Pair.create("1246", "VivaChek Biotech (Hangzhou) Co., Ltd, Vivadiag SARS CoV 2 Ag Rapid Test"),
        Pair.create("1253", "GenSure Biotech Inc, GenSure COVID-19 Antigen Rapid Kit (REF: P2004)"),
        Pair.create("1256", "Hangzhou AllTest Biotech Co., Ltd, COVID-19 and Influenza A+B Antigen Combo Rapid Test"),
        Pair.create("1263", "Humasis, Humasis COVID-19 Ag Test"),
        Pair.create("1266", "Labnovation Technologies Inc, SARS-CoV-2 Antigen Rapid Test Kit"),
        Pair.create("1267", "LumiQuick Diagnostics Inc, QuickProfile COVID-19 Antigen Test"),
        Pair.create("1268", "LumiraDX, LumiraDx SARS-CoV-2 Ag Test"),
        Pair.create("1271", "Precision Biosensor, Inc, Exdia COVID-19 Ag"),
        Pair.create("1278", "Xiamen Boson Biotech Co. Ltd, Rapid SARS-CoV-2 Antigen Test Card"),
        Pair.create("1295", "Zhejiang Anji Saianfu Biotech Co., Ltd, reOpenTest COVID-19 Antigen Rapid Test"),
        Pair.create("1296", "Zhejiang Anji Saianfu Biotech Co., Ltd, AndLucky COVID-19 Antigen Rapid Test"),
        Pair.create("1304", "AMEDA Labordiagnostik GmbH, AMP Rapid Test SARS-CoV-2 Ag"),
        Pair.create("1319", "SGA Medikal, V-Chek SARS-CoV-2 Ag Rapid Test Kit (Colloidal Gold)"),
        Pair.create("1331", "Beijing Lepu Medical Technology Co., Ltd, SARS-CoV-2 Antigen Rapid Test Kit"),
        Pair.create("1333", "Joinstar Biomedical Technology Co., Ltd, COVID-19 Rapid Antigen Test (Colloidal Gold)"),
        Pair.create("1341", "Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)"),
        Pair.create("1343", "Zhezhiang Orient Gene Biotech Co., Ltd, Coronavirus Ag Rapid Test Cassette (Swab)"),
        Pair.create("1360", "Guangdong Wesail Biotech Co., Ltd, COVID-19 Ag Test Kit"),
        Pair.create("1363", "Hangzhou Clongene Biotech Co., Ltd, Covid-19 Antigen Rapid Test Kit"),
        Pair.create("1365", "Hangzhou Clongene Biotech Co., Ltd, COVID-19/Influenza A+B Antigen Combo Rapid Test"),
        Pair.create("1375", "DIALAB GmbH, DIAQUICK COVID-19 Ag Cassette"),
        Pair.create("1392", "Hangzhou Testsea Biotechnology Co., Ltd, COVID-19 Antigen Test Cassette"),
        Pair.create("1420", "NanoEntek, FREND COVID-19 Ag"),
        Pair.create("1437", "Guangzhou Wondfo Biotech Co., Ltd, Wondfo 2019-nCoV Antigen Test (Lateral Flow Method)"),
        Pair.create("1443", "Vitrosens Biotechnology Co., Ltd, RapidFor SARS-CoV-2 Rapid Ag Test"),
        Pair.create("1456", "Xiamen Wiz Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test"),
        Pair.create("1466", "TODA PHARMA, TODA CORONADIAG Ag"),
        Pair.create("1468", "ACON Laboratories, Inc, Flowflex SARS-CoV-2 Antigen rapid test"),
        Pair.create("1481", "MP Biomedicals, Rapid SARS-CoV-2 Antigen Test Card"),
        Pair.create("1484", "Beijing Wantai Biological Pharmacy Enterprise Co., Ltd, Wantai SARS-CoV-2 Ag Rapid Test (FIA)"),
        Pair.create("1489", "Safecare Biotech (Hangzhou) Co. Ltd, COVID-19 Antigen Rapid Test Kit (Swab)"),
        Pair.create("1490", "Safecare Biotech (Hangzhou) Co. Ltd, Multi-Respiratory Virus Antigen Test Kit(Swab)  (Influenza A+B/ COVID-19)"),
        Pair.create("1574", "Shenzhen Zhenrui Biotechnology Co., Ltd, Zhenrui ®COVID-19 Antigen Test Cassette"),
        Pair.create("1604", "Roche (SD BIOSENSOR), SARS-CoV-2 Antigen Rapid Test"),
        Pair.create("1606", "RapiGEN Inc, BIOCREDIT COVID-19 Ag - SARS-CoV 2 Antigen test"),
        Pair.create("1654", "Asan Pharmaceutical CO., LTD, Asan Easy Test COVID-19 Ag"),
        Pair.create("1736", "Anhui Deep Blue Medical Technology Co., Ltd, COVID-19 (SARS-CoV-2) Antigen Test Kit(Colloidal Gold)"),
        Pair.create("1747", "Guangdong Hecin Scientific, Inc., 2019-nCoV Antigen Test Kit (colloidal gold method)"),
        Pair.create("1763", "Xiamen AmonMed Biotechnology Co., Ltd, COVID-19 Antigen Rapid Test Kit (Colloidal Gold)"),
        Pair.create("1764", "JOYSBIO (Tianjin) Biotechnology Co., Ltd, SARS-CoV-2 Antigen Rapid Test Kit (Colloidal Gold)"),
        Pair.create("1767", "Healgen Scientific, Coronavirus Ag Rapid Test Cassette"),
        Pair.create("1769", "Shenzhen Watmind Medical Co., Ltd, SARS-CoV-2 Ag Diagnostic Test Kit (Colloidal Gold)"),
        Pair.create("1815", "Anhui Deep Blue Medical Technology Co., Ltd, COVID-19 (SARS-CoV-2) Antigen Test Kit (Colloidal Gold) - Nasal Swab"),
        Pair.create("1822", "Anbio (Xiamen) Biotechnology Co., Ltd, Rapid COVID-19 Antigen Test(Colloidal Gold)"),
        Pair.create("1833", "AAZ-LMB, COVID-VIRO"),
        Pair.create("1844", "Hangzhou Immuno Biotech Co.,Ltd, Immunobio SARS-CoV-2 Antigen ANTERIOR NASAL Rapid Test Kit (minimal invasive)"),
        Pair.create("1870", "Beijing Hotgen Biotech Co., Ltd, Novel Coronavirus 2019-nCoV Antigen Test (Colloidal Gold)"),
        Pair.create("1884", "Xiamen Wiz Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Colloidal Gold)"),
        Pair.create("1906", "Azure Biotech Inc, COVID-19 Antigen Rapid Test Device"),
        Pair.create("1919", "Core Technology Co., Ltd, Coretests COVID-19 Ag Test"),
        Pair.create("1934", "Tody Laboratories Int., Coronavirus (SARS-CoV 2) Antigen - Oral Fluid"),
        Pair.create("2010", "Atlas Link Technology Co., Ltd., NOVA Test® SARS-CoV-2 Antigen Rapid Test Kit (Colloidal Gold Immunochromatography)"),
        Pair.create("2017", "Shenzhen Ultra-Diagnostics Biotec.Co.,Ltd, SARS-CoV-2 Antigen Test Kit"),
        Pair.create("308", "PCL Inc, PCL COVID19 Ag Rapid FIA"),
        Pair.create("344", "SD BIOSENSOR Inc, STANDARD F COVID-19 Ag FIA"),
        Pair.create("345", "SD BIOSENSOR Inc, STANDARD Q COVID-19 Ag Test"),
        Pair.create("768", "ArcDia International Ltd, mariPOC SARS-CoV-2")
    ));

    public static final List<Pair<String,String>> COVID19_LAB_TEST_TYPE = new ArrayList<>(Arrays.asList(
        Pair.create("LP217198-3", "Rapid immunoassay"),
        Pair.create("LP6464-4", "Nucleic acid amplification with probe detection")
    ));

    public static final List<Pair<String,String>> VACCINES_COVID19_AUTH_HOLDERS = new ArrayList<>(Arrays.asList(
        Pair.create("Bharat-Biotech", "Bharat Biotech"),
        Pair.create("Gamaleya-Research-Institute", "Gamaleya Research Institute"),
        Pair.create("ORG-100001417", "Janssen-Cilag International"),
        Pair.create("ORG-100001699", "AstraZeneca AB"),
        Pair.create("ORG-100006270", "Curevac AG"),
        Pair.create("ORG-100010771", "Sinopharm Weiqida Europe Pharmaceutical s.r.o. - Prague location"),
        Pair.create("ORG-100013793", "CanSino Biologics"),
        Pair.create("ORG-100020693", "China Sinopharm International Corp. - Beijing location"),
        Pair.create("ORG-100024420", "Sinopharm Zhijun (Shenzhen) Pharmaceutical Co. Ltd. - Shenzhen location"),
        Pair.create("ORG-100030215", "Biontech Manufacturing GmbH"),
        Pair.create("ORG-100031184", "Moderna Biotech Spain S.L."),
        Pair.create("ORG-100032020", "Novavax CZ AS"),
        Pair.create("Sinovac-Biotech", "Sinovac Biotech"),
        Pair.create("Vector-Institute", "Vector Institute")
    ));

    public static final List<Pair<String,String>> VACCINES_COVID19_NAMES = new ArrayList<>(Arrays.asList(
        Pair.create("BBIBP-CorV", "BBIBP-CorV"),
        Pair.create("CVnCoV", "CVnCoV"),
        Pair.create("Convidecia", "Convidecia"),
        Pair.create("CoronaVac", "CoronaVac"),
        Pair.create("Covaxin", "Covaxin (also known as BBV152 A, B, C)"),
        Pair.create("EU/1/20/1507", "COVID-19 Vaccine Moderna"),
        Pair.create("EU/1/20/1525", "COVID-19 Vaccine Janssen"),
        Pair.create("EU/1/20/1528", "Comirnaty"),
        Pair.create("EU/1/21/1529", "Vaxzevria"),
        Pair.create("EpiVacCorona", "EpiVacCorona"),
        Pair.create("Inactivated-SARS-CoV-2-Vero-Cell", "Inactivated SARS-CoV-2 (Vero Cell)"),
        Pair.create("Sputnik-V", "Sputnik-V")
    ));

    public static final List<Pair<String,String>> VACCINE_PROPHYLAXIS = new ArrayList<>(Arrays.asList(
        Pair.create("1119305005", "SARS-CoV-2 antigen vaccine"),
        Pair.create("1119349007", "SARS-CoV-2 mRNA vaccine"),
        Pair.create("J07BX03", "covid-19 vaccines")
    ));

    public static String getValueFromKey(List<Pair<String,String>> list, String key) {
        for (Pair<String,String> i : list) {
            if (i.first.equals(key)) return i.second;
        }
        return null;
    }
    
    // Hidden constructor
    private ValueSets() {}
}
