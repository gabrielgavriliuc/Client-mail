package code.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import code.connection.ClientConnection;
import code.model.*;
import code.utilities.CurrentDate;

public class WriteController {

    @FXML
    private TextField destination;
    @FXML
    private TextField subject;
    @FXML
    private TextArea text;
    @FXML
    private Button send;
    @FXML
    private Label errorMessage;

    private ClientMail m;
    private ClientConnection connection;

    public void initModel(ClientMail m, ClientConnection connection) {
        this.m = m;
        this.connection = connection;
    }

    @FXML
    public void sendMail() {
        List<String> destinationList = new ArrayList<>();
        String[] destToken = destination.getText().split(",");
        for (int i = 0; i < destToken.length; i++) {
            destinationList.add(destToken[i].replaceAll(" ", ""));  // Removing all the spaces if present
        }

        if (emailsValidation(destinationList)) {
            MailSerializable sendMail = new MailSerializable(m.getEmailProperty().getValue(), destinationList, subject.getText(), text.getText(), CurrentDate.getCurrentDate());
            connection.sendMail(sendMail);

            // Closing the window
            Stage stage = (Stage) send.getScene().getWindow();
            stage.close();
        } else {
            errorMessage.setVisible(true);
        }
    }
    
    /**
     * It hides the error message when the destination TextLabel is clicked
     */
    @FXML
    public void newAttempt() {
        errorMessage.setVisible(false);
    }
    
    /**
     * Checks if the emails given as list respect the email pattern
     * @param emails list of emails to check
     * @return  true if all the emails respect the email pattern
     *          false otherwise
     */
    private boolean emailsValidation(List<String> emails) {
        String validPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(validPattern);
        Matcher matcher;

        for (String e : emails) {
            matcher = pattern.matcher(e);

            if (!matcher.matches()) {
                return false;
            }
        }
        
        return true;
    }
}