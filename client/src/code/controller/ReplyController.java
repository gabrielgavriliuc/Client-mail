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

public class ReplyController {
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

        Mail selectedMail = m.getSelectedMail();
        destination.setText(selectedMail.getSource());
        destination.setEditable(false);
    }

     @FXML
    public void sendMail() {
        List<String> destinationList = new ArrayList<>();
        destinationList.add(destination.getText());

        MailSerializable replyMail = new MailSerializable(m.getEmailProperty().getValue(), destinationList, subject.getText(), text.getText(), CurrentDate.getCurrentDate());
        connection.sendMail(replyMail);

        // Closing the window
        Stage stage = (Stage) send.getScene().getWindow();
        stage.close();
    }
}