package com.mray.exercise;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

/**
 * Created by mray on 2/9/15.
 */
public class ImportData {
    private String patientDataFileName = "/Users/mray/Development/CS-Exercise/data/patient-data-records.csv";
    private String physicianDataFileName = "/Users/mray/Development/CS-Exercise/data/physician-data-records.csv";
    private String physicianDetailsFileName = "/Users/mray/Development/CS-Exercise/data/physician-details.yml";
    private String patientDetailsFileName = "/Users/mray/Development/CS-Exercise/data/patient-details.yml";
    private static final Logger logger = LoggerFactory.getLogger(ImportData.class);
    private final PatientDAO patientDAO = new PatientDAO();
    private final PhysicianDAO physicianDAO = new PhysicianDAO();
    private final List<Map<String, String>> patientDataList = new ArrayList<Map<String, String>>();
    private final List<Map<String, String>> physicianDataList = new ArrayList<Map<String, String>>();

    public static void main(String[] args) {
        logger.info("Data import started.");
        ImportData importData = new ImportData();
        importData.importPhysicianData();
        importData.importPatientData();
        importData.importPhysicianDetails();
        importData.importPatientDetails();
    }

    public ImportData() {
        Properties properties = new Properties();
        try (InputStream in = this.getClass().getResourceAsStream("config.properties")){
            if(in != null) {
                properties.load(in);
                logger.info("Using configuration properties: " + properties.toString());
                physicianDataFileName = properties.getProperty("physicianDataFileName");
                physicianDetailsFileName = properties.getProperty("physicianDetailsFileName");
                patientDataFileName = properties.getProperty("patientDataFileName");
                patientDetailsFileName = properties.getProperty("patientDetailsFileName");
                PersistenceHelper.setServerRoot(properties.getProperty("neo4j.uri"));
            }
        } catch(IOException e) {
            logger.error("Unable to read config.properties. Using default configuration.", e);
        }

    }

    private void importPhysicianData() {
        readCSVData(physicianDataList, physicianDataFileName);
        for (Map<String, String> data : physicianDataList) {
            physicianDAO.createPhysicianNode(data);
        }
    }

    private void importPatientData() {
        readCSVData(patientDataList, patientDataFileName);
        for (Map<String, String> data : patientDataList) {
            patientDAO.createPatientNode(data, physicianDAO.getPhysicianNode(data.get("Physician")));
        }
    }

    private void importPhysicianDetails() {
        try {
            String yamlString = new String(readAllBytes(get(physicianDetailsFileName)));
            yamlString = fixTabsInYaml(yamlString);

            YamlReader reader = new YamlReader(yamlString);
            while (true) {
                Map physicianDetails = (Map) reader.read();
                if (physicianDetails == null) break;
                physicianDAO.populatePhysicianDetails(physicianDetails);
            }
        } catch (Exception e) {
            logger.error("Error reading physician details", e);
        }
    }

    private void importPatientDetails() {
        try {
            String yamlString = new String(readAllBytes(get(patientDetailsFileName)));
            yamlString = fixTabsInYaml(yamlString);

            YamlReader reader = new YamlReader(yamlString);
            while (true) {
                Map patientDetails = (Map) reader.read();
                if (patientDetails == null) break;
                patientDAO.populatePatientDetails(patientDetails);
            }
        } catch (Exception e) {
            logger.error("Error reading patient details", e);
        }
    }

    // Method to import data from a CSV file into a list of maps (list of KVP), where each entry in the list represents
    // a line in the data file, and each kvp represents a cell
    private void readCSVData(List<Map<String, String>> dataList, String dataFile) {
        try {
            Reader in = new FileReader(dataFile);
            List<String> keyList = new ArrayList<String>();
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

            int index = 0;
            for (CSVRecord record : records) {
                // The header row. Build the list of keys. Assumes that no headers are blank!
                if (index == 0) {
                    index++;
                    for (int i = 0; i < record.size(); i++) {
                        if (StringUtils.isNotBlank(record.get(i))) {
                            keyList.add(record.get(i));
                        }
                    }
                } else {
                    if (StringUtils.isNotBlank(record.get(0))) {
                        Map<String, String> dataMap = new HashMap<String, String>();
                        for (int i = 0; i < keyList.size(); i++) {
                            // For now allow blank values and handle downstream...
                            dataMap.put(keyList.get(i), record.get(i));
                        }
                        dataList.add(dataMap);
                    }
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    // The supplied YAML files have TAB characters in them, which is a violation of the YAML spec. Simple utility method
    // to remove those characters so the files can be parsed.
    private static String fixTabsInYaml(String yamlString) {
        return yamlString.replace("\t", "    ");
    }


}
