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
public class PhysicianDAO {
    private static final Logger logger = LoggerFactory.getLogger(PhysicianDAO.class);
    private final Map<String, URI> physicianIdMap = new HashMap<String, URI>();

    public void createPhysicianNode(Map<String, String> physicianData) {
        String patientId = physicianData.get("ID");
        URI physicianNode = PersistenceHelper.insertNode(JsonHelper.buildJSONString(physicianData));
        physicianIdMap.put(patientId, physicianNode);
        try {
            PersistenceHelper.addLabel(physicianNode, "Physician");
        } catch(URISyntaxException e) {
            logger.error("Error adding label", e);
        }
    }

    public URI getPhysicianNode(String id) {
        return physicianIdMap.get(id);
    }

    public void populatePhysicianDetails(Map physicianDetails) {
        String id = physicianDetails.get("ID").toString();
        if(StringUtils.isNotBlank(id)) {
            URI physicianNode = physicianIdMap.get(id);
            if(physicianDetails.containsKey(OTHER_APPOINTMENTS)) {
                populateAppointments(physicianNode, (ArrayList)physicianDetails.get(OTHER_APPOINTMENTS));
            }
            if(physicianDetails.containsKey(EDUCATION)) {
                populateEducation(physicianNode, (ArrayList) physicianDetails.get(EDUCATION));
            }
            if(physicianDetails.containsKey(BOARD_CERTIFICATIONS)) {
                populateBoardCertifications(physicianNode, (ArrayList) physicianDetails.get(BOARD_CERTIFICATIONS));
            }
            if(physicianDetails.containsKey(PROFESSIONAL_MEMBERSHIPS)) {
                populateProfessionalMemberships(physicianNode, (ArrayList) physicianDetails.get(PROFESSIONAL_MEMBERSHIPS));
            }
            if(physicianDetails.containsKey(PUBLICATIONS)) {
                populatePublications(physicianNode, (ArrayList) physicianDetails.get(PUBLICATIONS));
            }
        }

    }

    private void populateProfessionalMemberships(URI physicianNode, ArrayList professionalMembershipList) {
        for(Object listEntry : professionalMembershipList) {
            Map membershipEntry = (Map)listEntry;
            URI membershipNode = PersistenceHelper.insertNode(JsonHelper.buildJSONString(ASSOCIATION, (String)membershipEntry.get(ASSOCIATION)));
            try {
                PersistenceHelper.addLabel(membershipNode, "Association");
                Map<String, String> map = new HashMap<>();
                if(membershipEntry.containsKey(LOCALITY)) {
                    map.put(LOCALITY, (String) membershipEntry.get(LOCALITY));
                }
                map.put(CAPACITY, (String)membershipEntry.get(CAPACITY));
                map.put(SINCE, (String)membershipEntry.get(SINCE));

                PersistenceHelper.addRelationship(physicianNode, membershipNode, MEMBER_OF,
                        JsonHelper.buildJSONString(map));
            } catch (URISyntaxException e) {
                logger.error("Error creating relationship", e);
            }
        }

    }

    private void populateBoardCertifications(URI physicianNode, ArrayList boardCertificationList) {
        for(Object listEntry : boardCertificationList) {
            Map certificationEntry = (Map)listEntry;
            URI certificationNode = PersistenceHelper.insertNode(JsonHelper.buildJSONString(SPECIALITY, (String)certificationEntry.get(SPECIALITY)));
            try {
                PersistenceHelper.addLabel(certificationNode, "Speciality");
                // Todo: There's probably a better way to handle dates in neo4j
                PersistenceHelper.addRelationship(physicianNode, certificationNode, BOARD_CERTIFIED,
                        JsonHelper.buildJSONString(DATE, (String)certificationEntry.get(DATE)));
            } catch (URISyntaxException e) {
                logger.error("Error creating relationship", e);
            }
        }

    }

    private void populateEducation(URI physicianNode, ArrayList educationList) {
        //Todo
    }

    private void populatePublications(URI physicianNode, ArrayList publicationsList) {
        //Todo
    }

    private void populateAppointments(URI physicianNode, ArrayList otherAppointmentsList) {
        //Todo
    }

    public static final String OTHER_APPOINTMENTS = "other-appointments";
    public static final String EDUCATION = "education";
    public static final String BOARD_CERTIFICATIONS = "board certifications";
    public static final String PROFESSIONAL_MEMBERSHIPS = "professional memberships";
    public static final String PUBLICATIONS = "publications";
    public static final String SPECIALITY = "speciality";
    public static final String DATE = "date";
    public static final String BOARD_CERTIFIED = "board_certified_in";
    public static final String ASSOCIATION = "association";
    public static final String LOCALITY = "locality";
    public static final String CAPACITY = "capacity";
    public static final String SINCE = "since";
    public static final String MEMBER_OF = "member_of";
}
