package code.gui;

import code.controller.ServerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Server extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("/fxml/ServerGui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 530);
        stage.setTitle("Server");

        ServerController controller = fxmlLoader.getController();
        controller.initModel();

        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent arg0) {
                // Closing the Connection thread
                controller.closeApplication();
                Platform.exit();
            }
            
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
