package code.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import code.connection.ClientConnection;
import code.model.*;

public class ClientController {

    // LEFT SIDE

    // The mail of the Client working
    @FXML
    private Label email;

    // A table that shows all the mails received
    @FXML
    private TableView<Mail> listOfMessages;
    @FXML
    private TableColumn<Mail, String> columnSource;
    @FXML
    private TableColumn<Mail, String> columnArgument;
    @FXML
    private TableColumn<Mail, String> columnDate;

    // Switch view button
    @FXML
    private Button switchViewButton;

    // RIGHT SIDE

    // Source of the mail
    @FXML
    private Label source;

    // Date of the mail
    @FXML
    private Label date;

    // Argument of the mail
    @FXML
    private Label argument;

    // List of the receivers
    @FXML
    private ComboBox<String> destinationBox;

    // Text of the mail
    @FXML
    private Text text;

    // "From:"
    @FXML
    private Label fromlLabel;

    // "Subject:"
    @FXML
    private Label subjectLabel;

    // Grid where the detailed informations of the mail are shown
    @FXML
    private GridPane showGrid;

    // The text of the mail is shown here
    @FXML
    private ScrollPane textPane;

    // Buttons Delete, Reply and ReplyAll
    @FXML
    private GridPane interactionGrid;

    // Connection status 
    @FXML
    private Circle connectionCircle;

    @FXML
    private Label connectionLabel;

    private ClientMail model;
    private Mail selectedMail;

    private ClientConnection connection;

    public ClientController() {}

    public void initModel(String email) {
        // Creating the Model
        model = new ClientMail(email);

        // Binding the Properties
        bind();
        initTable();

        // Hiding the "mail-in-detail" part
        showGrid.setVisible(false);
        textPane.setVisible(false);
        interactionGrid.setVisible(false);
        
        // Connection with the Server
        connection = new ClientConnection(this.model);
    }

    /**
     * It shows the details of the selected mail when clicked
     */
    @FXML
    public void showDetails() {
        showGrid.setVisible(true);
        textPane.setVisible(true);
        interactionGrid.setVisible(true);

        selectedMail = listOfMessages.getSelectionModel().getSelectedItem();
        model.setSelectedMail(selectedMail);
        destinationBox.getSelectionModel().selectFirst();
    }

    /**
     * It deletes the selected mail
     */
    @FXML
    public void deleteMail() {
        Mail delete = listOfMessages.getSelectionModel().getSelectedItem();
        model.deleteMail(delete);
    
        model.setSelectedMailEmpty();

        connection.deleteMail(delete);
    
        showGrid.setVisible(false);
        textPane.setVisible(false);
        interactionGrid.setVisible(false);
    }
    
    
    /**
     * Called when the write button is clicked
     * It creates a new window that allows us to write an email
     * @throws IOException
     */
    @FXML
    public void writeMail() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Writing...");

        FXMLLoader writeWindowLoader = new FXMLLoader(ClientController.class.getResource("/fxml/WriteMail.fxml"));
        Scene scene = new Scene(writeWindowLoader.load(), 603, 400);

        WriteController wc = writeWindowLoader.getController();
        wc.initModel(model, connection);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Called when the reply button is clicked
     * It creates a new window that allows us to write an email in response to a received one
     * The destination is the source of the received one
     * @throws IOException
     */
    @FXML
    public void reply() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Replying...");

        FXMLLoader replyWindowLoader = new FXMLLoader(ClientController.class.getResource("/fxml/ReplyMail.fxml"));
        Scene scene = new Scene(replyWindowLoader.load(), 603, 400);

        ReplyController rc = replyWindowLoader.getController();
        rc.initModel(model, connection);

        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Called when the replyAll button is clicked
     * It creates a new window that allows us to write an email in response to a received one
     * The destination is the source of the received one + all the other emails where it was sent
     * @throws IOException
     */
    @FXML
    public void replyAll() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Replying...");

        FXMLLoader replyAllWindowLoader = new FXMLLoader(ClientController.class.getResource("/fxml/ReplyAllMail.fxml"));
        Scene scene = new Scene(replyAllWindowLoader.load(), 603, 400);

        ReplyAllController rc = replyAllWindowLoader.getController();
        rc.initModel(model, connection);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Called when the forward button is clicked
     * It creates a new window that allows us to forward the received mail to 1 or more emails
     * @throws IOException
     */
    @FXML
    public void forward() throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Forwarding...");

        FXMLLoader forwardWindowLoader = new FXMLLoader(ClientController.class.getResource("/fxml/ForwardMail.fxml"));
        Scene scene = new Scene(forwardWindowLoader.load(), 603, 400);

        ForwardController fc = forwardWindowLoader.getController();
        fc.initModel(model, connection);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchView() {
        model.switchView();

        if (switchViewButton.getText().equals("Click to see SENT")) {
            switchViewButton.setText("Click to see RECEIVED");
        } else {
            switchViewButton.setText("Click to see SENT");
        }
    }

    /**
     * It binds all the components of the GUI with the respective properties of the model.ClientMail
     */
    private void bind() {
        email.textProperty().bind(model.getEmailProperty());

        listOfMessages.setItems(model.getListOfMessages());

        selectedMail = model.getSelectedMail();
        source.textProperty().bind(selectedMail.getSourceProperty());
        argument.textProperty().bind(selectedMail.getArgumentProperty());
        text.textProperty().bind(selectedMail.getTextProperty());
        date.textProperty().bind(selectedMail.getDateProperty());
        destinationBox.setItems(selectedMail.getDestinationProperty());

        connectionLabel.textProperty().bind(model.getConnectedProperty());
        connectionLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Connesso al Server")) {
                connectionLabel.setTextFill(Color.GREEN);
                connectionCircle.setFill(Color.GREEN);
            } else {
                connectionLabel.setTextFill(Color.RED);
                connectionCircle.setFill(Color.RED);
            }
        });

        switchViewButton.setText("Click to see SENT");
    }
     
    /**
     * It binds the columns to the respective properties of the model.Mail
     */
    private void initTable() {
        columnSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        columnArgument.setCellValueFactory(new PropertyValueFactory<>("argument"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    /**
     * It shuts down the Connection thread
     */
    public void shutdown() {
        connection.shutdown();
    }

}