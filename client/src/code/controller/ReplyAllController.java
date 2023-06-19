package code.controller;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import code.connection.ClientConnection;
import code.model.*;
import code.utilities.CurrentDate;

public class ReplyAllController {
    @FXML
    private TextField destination;
    @FXML
    private TextField subject;
    @FXML
    private TextArea text;
    @FXML
    private Button send;

    private ClientMail m;
    private ClientConnection connection;

    public void initModel(ClientMail m, ClientConnection connection) {
        this.m = m;
        this.connection = connection;

        setDestinations();
    }

     @FXML
     public void sendMail() {
        // Getting the destination List
        List<String> destinationList = new ArrayList<>();
        destinationList.add(m.getSelectedMail().getSource());
        destinationList.addAll(m.getSelectedMail().getDestination());
        destinationList.remove(m.getEmailProperty().getValue());

        // Sending the Mail to the server
        MailSerializable mailToReplyAll = new MailSerializable(m.getEmailProperty().getValue(), destinationList, subject.getText(), text.getText(), CurrentDate.getCurrentDate());
        connection.sendMail(mailToReplyAll);

        // Closing the window
        Stage stage = (Stage) send.getScene().getWindow();
        stage.close();
    }
    
    private void setDestinations() {
        Mail selectedMail = m.getSelectedMail();
        List<String> destinations = new ArrayList<>();
        destinations.add(selectedMail.getSource());
        destinations.addAll(selectedMail.getDestination());
        destinations.remove(m.getEmailProperty().getValue()); // Removing Clients' mail from destinations
        
        // Generating the String to insert in the destination TextField
        String res = "";
        for (String s : destinations) {
            res += s + ", ";
        }
        res = res.substring(0, res.length() - 2);

        destination.setText(res);
        destination.setEditable(false);
    }
}