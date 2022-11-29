package client;


import client.controllers.Controller;
import common.Command;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class CloudStorageStarter extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/fxml/auth.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        primaryStage.setScene(new Scene((Parent) fxmlLoader.load()));
        primaryStage.show();

        Controller controller = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }


    public static void main(String[] args) {
        launch(args);
    }
}

