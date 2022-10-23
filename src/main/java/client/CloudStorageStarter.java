package client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class CloudStorageStarter extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        URL fxmlLocation = getClass().getResource("/fxml/cloud.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        primaryStage.setScene(new Scene((Parent) fxmlLoader.load()));
        primaryStage.show();

        Controller controller = fxmlLoader.getController();
       // primaryStage.setOnCloseRequest(event -> controller.getClient().sendMessage(Command.END));
    }


    public static void main(String[] args) {
        launch(args);
    }
}

