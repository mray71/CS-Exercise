package com.mray.exercise;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by mray on 2/9/15.
 */
public class PersistenceHelper {
    private static String SERVER_ROOT_URI = "http://localhost:7474/db/data/";
    private static final Logger logger = LoggerFactory.getLogger(PersistenceHelper.class);

    public static void setServerRoot(String serverRoot) {
        SERVER_ROOT_URI = serverRoot;
    }

    public static URI insertNode(String jsonData) {
        final String nodeEntryPointUri = SERVER_ROOT_URI + "node";

        WebResource resource = Client.create().resource(nodeEntryPointUri);
        // POST {} to the node entry point URI
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity(jsonData)
                .post(ClientResponse.class);

        final URI location = response.getLocation();
        logger.info(String.format("POST to [%s], status code [%d], location header [%s]",
                nodeEntryPointUri, response.getStatus(), location.toString()));
        response.close();
        return location;
    }

    public static URI addRelationship(URI startNode, URI endNode,
                                      String relationshipType, String jsonAttributes)
            throws URISyntaxException {
        URI fromUri = new URI(startNode.toString() + "/relationships");
        String relationshipJson = generateJsonRelationship(endNode, relationshipType, jsonAttributes);
        WebResource resource = Client.create().resource(fromUri);
        // POST JSON to the relationships URI
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity(relationshipJson)
                .post(ClientResponse.class);
        final URI location = response.getLocation();
        logger.info(String.format("POST to [%s], status code [%d], location header [%s]",
                fromUri, response.getStatus(), location.toString()));
        response.close();
        return location;
    }

    private static String generateJsonRelationship(URI endNode,
                                                   String relationshipType, String... jsonAttributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"to\" : \"");
        sb.append(endNode.toString());
        sb.append("\", ");
        sb.append("\"type\" : \"");
        sb.append(relationshipType);
        if (jsonAttributes == null || jsonAttributes.length < 1) {
            sb.append("\"");
        } else {
            sb.append("\", \"data\" : ");
            for (int i = 0; i < jsonAttributes.length; i++) {
                sb.append(jsonAttributes[i]);
                if (i < jsonAttributes.length - 1) { // Miss off the final comma
                    sb.append(", ");
                }
            }
        }
        sb.append(" }");
        return sb.toString();
    }

    public static void addLabel(URI physicianNode, String label) throws URISyntaxException {
        URI fromUri = new URI(physicianNode.toString() + "/labels");
        WebResource resource = Client.create().resource(fromUri);
        // POST JSON to the relationships URI
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity("\"" + label + "\"")
                .post(ClientResponse.class);
        logger.info(String.format("POST to [%s], status code [%d]",
                fromUri, response.getStatus()));
        response.close();
    }
}
