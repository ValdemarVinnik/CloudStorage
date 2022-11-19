package client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
@Slf4j
public class AuthController {


    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    private Button authButton;

    @FXML
    private Button regButton;


    public void auth(ActionEvent actionEvent) {
        authButton.getScene().getWindow().hide();

        URL fxmlLocation = getClass().getResource("/fxml/cloud.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Stage primaryStage = new Stage();
       try{
           primaryStage.setScene(new Scene((Parent) fxmlLoader.load()));
       }catch (IOException e){
           log.error(e.toString());
       }
        primaryStage.show();


    }

    public void registration(ActionEvent actionEvent) {
        regButton.getScene().getWindow().hide();

        URL fxmlLocation = getClass().getResource("/fxml/reg.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Stage primaryStage = new Stage();
        try{
            primaryStage.setScene(new Scene((Parent) fxmlLoader.load()));
        }catch (IOException e){
            log.error(e.toString());
        }
        primaryStage.show();
    }
}
