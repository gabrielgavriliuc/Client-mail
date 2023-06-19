package code.gui;

import java.io.IOException;

import code.controller.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * JavaFX App
 */
public class Client extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("/fxml/ClientGui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 500);
        stage.setTitle("Client");

        ClientController controller = fxmlLoader.getController();
        controller.initModel("charlie@gmail.com");

        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent arg0) {
                // Closing the Connection thread
                controller.shutdown();
                Platform.exit();
            }
            
        });
    }

    public static void main(String[] args) {
        launch();
    }

}