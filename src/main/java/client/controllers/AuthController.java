package client.controllers;

import client.Client;
import common.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class AuthController implements Controller{


    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    private Button authButton;

    @FXML
    private Button regButton;


    private Client client;

    public AuthController() {
        this.client = Client.getInstance();
        client.setController(this);
    }

    public void auth(ActionEvent actionEvent) {

        String login = loginField.getText();
        String password = passwordField.getText();

        User user = new User(login, password);
        openConnect();

        client.authenticate(user);

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

    public void openConnect() {
        try {
            client.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            showNotification();
        }
    }

    private void showNotification() {
        final Alert alert = new Alert(Alert.AlertType.ERROR,
                "I can't connect to the server.",
                new ButtonType("Try again", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE));

        alert.setTitle("Connection error!");
        final Optional<ButtonType> answer = alert.showAndWait();
        final boolean isExist = answer.map(select -> select.getButtonData().isCancelButton()).orElse(false);
        if (isExist) {
            System.exit(0);
        }
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

    @Override
    public Window getWindow() {
       return passwordField.getScene().getWindow();
    }

    @Override
    public void displayUsersListView(String... files) {

    }

    @Override
    public void displayServerListView(String... files) {

    }

    @Override
    public void displayServerCurrentPath(String currentPath) {

    }

    @Override
    public void displayUserCurrentPath(String currentPath) {

    }
}
