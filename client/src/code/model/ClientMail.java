package code.model;

import java.util.ArrayList;
import java.util.List;

import code.utilities.MailTranslator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientMail {
    private StringProperty email;

    private ObservableList<Mail> listOfMessages;
    private List<Mail> inMessages;
    private List<Mail> outMessages;
    private boolean out;
    private boolean firstConnection = true;

    private Mail selectedMail;
    private StringProperty connected;

    public ClientMail(String email) {
        this.email = new SimpleStringProperty();
        this.email.setValue(email);

        listOfMessages = FXCollections.observableArrayList();
        inMessages = new ArrayList<>();
        outMessages = new ArrayList<>();

        selectedMail = new Mail("", new ArrayList<String>(), "", "", "");

        connected = new SimpleStringProperty();
    }

    public StringProperty getEmailProperty() {
        return email;
    }

    public ObservableList<Mail> getListOfMessages() {
        return listOfMessages;
    }

    public List<Mail> getInMessages() {
        return inMessages;
    }

    public List<Mail> getOutMessages() {
        return outMessages;
    }

    public Mail getSelectedMail() {
        return selectedMail;
    }

    public StringProperty getConnectedProperty() {
        return connected;
    }

    public void setConnected(boolean connected) {
        if (connected) {
            this.connected.setValue("Connesso al Server");
        } else {
            this.connected.setValue("Server offline, le nuove richieste potrebbero non andare a buon fine... Attendere");
        }
    }
    /**
     * It sets the selectedMail to an empty representation
     */
    public void setSelectedMailEmpty() {
        selectedMail.setId("");
        selectedMail.setSource("");
        selectedMail.setDestination(new ArrayList<String>());
        selectedMail.setArgument("");
        selectedMail.setText("");
        selectedMail.setDate("");
    }

    /**
     * It sets the selectedMail property to the current selected mail values
     * @param newSelection is the mail currently selected
     */
    public void setSelectedMail(Mail newSelection) {
        selectedMail.setId(newSelection.getId());
        selectedMail.setSource(newSelection.getSource());
        selectedMail.setDestination(newSelection.getDestination());
        selectedMail.setArgument(newSelection.getArgument());
        selectedMail.setText(newSelection.getText());
        selectedMail.setDate(newSelection.getDate());
    }

    /**
     * Adds a new received Mail from the Server to the Client list of Mails
     * @param newMail is the new Mail received
     */
    public void addNewMail(Mail newMail) {
        if (newMail.getSource().equals(email.getValue())) {
            outMessages.add(0, newMail);

            // If we are viewing the outgoing messages then we update also the view
            if (out) {
                listOfMessages.add(0, newMail);
            }
        } else {
            inMessages.add(0, newMail);

            // If we are viewing the incoming messages then we update also the view
            if (!out) {
                listOfMessages.add(0, newMail);
            }
        }
    }

    /**
     * It converts the received MailSerializable into Mail and adds it to the Client list of Mails
     * @param newMail is the new MailSerializable received
     */
    public void addNewMail(MailSerializable newMail) {
        this.addNewMail(MailTranslator.toMail(newMail));
    }

    /**
     * At the start of the application it shows the incoming messages on the GUI
     */
    public void viewMessages() {
        if (firstConnection) {
            listOfMessages.clear();
            listOfMessages.addAll(inMessages);
            out = false;
            firstConnection = false;
        }
    }

    /**
     * If the list of messages contains only the received ones it clears it and it adds all the sent ones and viceversa
     */
    public void switchView() {
        if (out) {
            out = false;
            listOfMessages.clear();
            listOfMessages.addAll(inMessages);
        } else {
            out = true;
            listOfMessages.clear();
            listOfMessages.addAll(outMessages);
        }
    }

    /**
     * It deletes a specific Mail from the list of messages
     * @param delete
     */
    public void deleteMail(Mail delete) {
        listOfMessages.remove(delete);

        // But also from the list where it is still stored locally by the Client
        if (delete.getSource().equals(email.getValue())) {
            outMessages.remove(delete);
        } else {
            inMessages.remove(delete);
        }
    }

}
