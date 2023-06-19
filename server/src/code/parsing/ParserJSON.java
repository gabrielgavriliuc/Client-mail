package code.parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;

import code.model.MailSerializable;

public class ParserJSON {
    private static ObjectMapper mapper = new ObjectMapper();
    private String clientEmail;
    private File JSONFile;

    public ParserJSON() {
        JSONFile = new File("src/code/parsing/clients.json");
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    /**
     * It gets all the new messages of a specified client, starting from his last received mail
     * @param lastReceivedMail is the latest mail that the client already have
     * @return all the new messages of that user if it is already registered
     *         null otherwise
     */
    public List<MailSerializable> getListOfMessages(String lastReceivedMail) {
        List<MailSerializable> mails = new ArrayList<>();

        try {
            JsonNode root = mapper.readTree(JSONFile);
            ArrayNode clients = (ArrayNode) root.get("clients");

            for (JsonNode jn : clients) {
                if (jn.get("email").asText().equals(clientEmail)) {
                    ClientMail c = mapper.treeToValue(jn, ClientMail.class);

                    // If the client hasn't received any mail then we send him all of them
                    if (lastReceivedMail.equals("No mails")) {
                        mails.addAll(c.getMails());

                    // Otherwise just the new ones
                    } else {
                        getNewMails(c, lastReceivedMail, mails);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(
                    "IOException in ParserJSON during searchClientInJSONFile method");
                    e.printStackTrace();
        }

        return mails;
    }

    /**
     * It gets only the new mails
     * @param c is the client identified in the JSON file
     * @param lastReceivedMail is the id of the last received mail from the client
     * @param mails are all the new mails
     */
    private void getNewMails(ClientMail c, String lastReceivedMail, List<MailSerializable> mails) {
        for (MailSerializable m : c.getMails()) {
            if (Integer.valueOf(m.getId()) > Integer.valueOf(lastReceivedMail)) {
                mails.add(m);
            }
        }
    }

    /**
     * Finds an assignable id -> an id never used for a Mail
     * @return a usable id
     */
    public String getNextId() {
        String nextId = "";

        try {
            Clients jsonPOJO = mapper.readValue(JSONFile, Clients.class);

            // Getting the next Id 
            nextId = jsonPOJO.getNextId();
            
            // Updating the next ID
            int newNextId = Integer.valueOf(nextId) + 1;
            jsonPOJO.setNextId(String.valueOf(newNextId));
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(JSONFile, jsonPOJO);
        } catch (IOException e) {
            System.out.println("IOException in ParserJSON during getNextId() method");
            e.printStackTrace();
        }
        
        return nextId;
    }

    public void addMailToJSON(MailSerializable sendMail) {
        try {
            Clients jsonPOJO = mapper.readValue(JSONFile, Clients.class);

            // Adding the mail to the source
            ClientMail client = null;

            for (ClientMail c : jsonPOJO.getClients()) {
                if (c.getEmail().equals(sendMail.getSource())) {
                    c.addNewMail(sendMail);
                    client = c;
                }
            }

            // Adding the mail to all the destinations
            for (String destinationUser : sendMail.getDestination()) {
                boolean userExists = false;
                
                for (ClientMail c : jsonPOJO.getClients()) {
                    if (c.getEmail().equals(destinationUser)) {
                        userExists = true;
                        c.addNewMail(sendMail);
                    }
                }

                // One of the destinations doesn't exist in the JSON so we are sending back to the Client a message
                if (!userExists) {
                    List<String> destination = new ArrayList<>();
                    destination.add(clientEmail);
            
                    MailSerializable alertMail = new MailSerializable(getNextId(), "no-reply@gmail.com", destination,
                            "USER DOESN'T EXISTS", "The user: " + destinationUser + " from one of the last mails sent doesn't exist",
                            sendMail.getDate());

                    client.addNewMail(alertMail);                    
                }
            }

            // Updating the JSON
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(JSONFile, jsonPOJO);

        } catch (IOException e) {
            System.out.println("IOException in ParserJSON during addMailToJSON() method \n" + e.getMessage() + "\n");
        }
    }
    

    /**
     * It removes a specific Mail only from the authenticated Client's list of Mails
     * @param deleteMail is the Mail that will be removed
     */
    public void deleteMailFromJSON(MailSerializable deleteMail) {
        try {
            Clients jsonPOJO = mapper.readValue(JSONFile, Clients.class);

            // Deleting the mail from the source
            for (ClientMail c : jsonPOJO.getClients()) {
                if (c.getEmail().equals(clientEmail)) {
                    c.removeMail(deleteMail);
                }
            }

            // Updating the JSON
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(JSONFile, jsonPOJO);

        } catch (IOException e) {
            System.out.println("IOException in ParserJSON during addMailToJSON() method \n" + e.getMessage() + "\n");
        }
    }

}
