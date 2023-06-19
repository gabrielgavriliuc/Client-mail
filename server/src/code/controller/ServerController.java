package code.controller;

import java.io.IOException;

import code.connection.ServerConnection;
import code.model.ServerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class ServerController {

    @FXML
    private ListView<String> logView;
    @FXML
    private Button turnOnButton;
    @FXML
    private Button turnOffButton;

    private ServerModel model;
    private ServerConnection connection;

    public void initModel() {
        this.model = new ServerModel();

        // Binding
        logView.setItems(model.getLogList());

        turnOffButton.setVisible(false);

        connection = new ServerConnection(model);
    }

    @FXML
    public void turnOn() {
        try {
            connection.turnServerOn();
            turnOnButton.setVisible(false);
            turnOffButton.setVisible(true);
        } catch (IOException e) {
            System.out.println("IOException in ServerController during turnOn() method \n" + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
    
    @FXML
    public void turnOff() {
        connection.turnServerOff();
        turnOffButton.setVisible(false);
        turnOnButton.setVisible(true);
    }

    /**
     * It closes the application when the GUI is closed
     */
    public void closeApplication() {
        turnOff();
        connection.applicationClosed();
    }
    

}
