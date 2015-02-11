package com.mray.exercise;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mray on 2/9/15.
 */
public class PatientDAO {
    private static final Logger logger = LoggerFactory.getLogger(PatientDAO.class);
    private final Map<String, URI> patientIdMap = new HashMap<String, URI>();

    public void createPatientNode(Map<String, String> patientData, URI physicianNode) {
        String patientId = patientData.get("ID");
        URI patientNode = PersistenceHelper.insertNode(JsonHelper.buildJSONString(patientData));
        patientIdMap.put(patientId, patientNode);
        try {
            PersistenceHelper.addLabel(patientNode, "Patient");
            PersistenceHelper.addRelationship(patientNode, physicianNode, "patient_of", null);
            PersistenceHelper.addRelationship(physicianNode, patientNode, "physician_for", null);
        } catch (URISyntaxException e) {
            logger.error("Error creating relationship", e);
        }
    }

    public void populatePatientDetails(Map patientDetails) {
        String id = patientDetails.get("ID").toString();
        if(StringUtils.isNotBlank(id)) {
            URI patientNode = patientIdMap.get(id);
            Map patientInfo = (Map)patientDetails.get("patient");
            if(patientInfo.containsKey(DIAGNOSTICS)) {
                populatePatientDiagnostics(patientNode, (ArrayList)patientInfo.get(DIAGNOSTICS));
            }
            if(patientInfo.containsKey(MEDICATIONS)) {
                populatePatientMedications(patientNode, (ArrayList) patientInfo.get(MEDICATIONS));
            }
            if(patientInfo.containsKey(ALLERGIES)) {
                populatePatientAllergies(patientNode, (ArrayList) patientInfo.get(ALLERGIES));
            }
        }

    }

    private void populatePatientAllergies(URI patientNode, ArrayList allergyList) {
        for(Object listEntry : allergyList) {
            String allergy = (String)listEntry;
            URI allergyNode = PersistenceHelper.insertNode(JsonHelper.buildJSONString("name", allergy));
            try {
                PersistenceHelper.addLabel(allergyNode, "Allergen");
                PersistenceHelper.addRelationship(patientNode, allergyNode, "allergic_to",null);
            } catch (URISyntaxException e) {
                logger.error("Error creating relationship", e);
            }
        }
    }

    private void populatePatientDiagnostics(URI patientNode, ArrayList diagnosticsList) {
        for(Object listEntry : diagnosticsList) {
            Map diagnosticEntry = (Map)listEntry;
            URI diagnosticNode = PersistenceHelper.insertNode(JsonHelper.buildJSONString(DIAGNOSTIC_BMI, (String)diagnosticEntry.get(DIAGNOSTIC_BMI)));
            try {
                PersistenceHelper.addLabel(diagnosticNode, "Diagnostic");
                // Todo: There's probably a better way to handle dates in neo4j
                PersistenceHelper.addRelationship(patientNode, diagnosticNode, "diagnosed",
                        JsonHelper.buildJSONString(DIAGNOSTIC_DATE, (String)diagnosticEntry.get(DIAGNOSTIC_DATE)));
            } catch (URISyntaxException e) {
                logger.error("Error creating relationship", e);
            }
        }
    }

    private void populatePatientMedications(URI patientNode, ArrayList medicationList) {
        for(Object listEntry : medicationList) {
            Map medicationEntry = (Map)listEntry;
            URI medicationNode = PersistenceHelper.insertNode(JsonHelper.buildJSONString(MEDICATION_NAME, (String)medicationEntry.get(MEDICATION_NAME)));
            try {
                PersistenceHelper.addLabel(medicationNode, "Medication");
                // Todo: This smells. Also handle dates better
                Map<String, String> relationshipDataMap = new HashMap<String, String>();
                relationshipDataMap.put(MEDICATION_NOTES, medicationEntry.get(MEDICATION_NOTES).toString());
                relationshipDataMap.put(MEDICATION_START, medicationEntry.get(MEDICATION_START).toString());
                PersistenceHelper.addRelationship(patientNode, medicationNode, "prescribed",
                        JsonHelper.buildJSONString(relationshipDataMap));
            } catch (URISyntaxException e) {
                logger.error("Error creating relationship", e);
            }
        }
    }

    public static final String DIAGNOSTICS = "diagnostics";
    public static final String DIAGNOSTIC_BMI = "bmi";
    public static final String DIAGNOSTIC_DATE = "date";
    public static final String MEDICATIONS = "medications";
    public static final String MEDICATION_NAME = "name";
    public static final String MEDICATION_NOTES = "notes";
    public static final String MEDICATION_START = "start";
    public static final String ALLERGIES = "allergies";
}
