package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class Controller {

    public Button openConnection;
    private Client client;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<String> userViewListField;

    @FXML
    private ListView<String> serverViewListField;

    @FXML
    private TextArea logArea;

    @FXML
    private Button unloadButton;

    @FXML
    private TextField userPathField;

    @FXML
    private Button userRiseUpButton;

    @FXML
    private Button serverRiseUpButton;

    @FXML
    private TextField serverPathField;

    @FXML
    private Button downloadButton;

    private static final File CLIENT_ROOT = new File("src/main/java/client/root");

    private static String currentPath = CLIENT_ROOT.getAbsolutePath();
    private File selectedFile;


    @FXML
    public void initialize() {
        displayUsersListView(CLIENT_ROOT.list());
        displayServerListView(client.getContentCurrentDirectory());
        displayServerCurrentPath(client.getCurrentPathOnTheServer());
        displayUserCurrentPath(client.getCurrentPath());
        viewSelectedUserFile();
        viewSelectedServerFile();
    }

    public Controller() {
        this.client = new Client(this);

    }

    public void addMessage(String message) {
        logArea.appendText(message + "\n");

    }

    public void displayUsersListView(String... files) {
         userViewListField.getItems().clear();
        Platform.runLater(() -> userViewListField.getItems().addAll(files));
    }

    public void displayServerListView(String... files) {
        Platform.runLater(() -> serverViewListField.getItems().clear());
        Platform.runLater(() -> serverViewListField.getItems().addAll(files));
    }

    public void displayUserCurrentPath(String currentPath){
        userPathField.clear();
        Platform.runLater(() -> userPathField.appendText(currentPath));
    }

    public void displayServerCurrentPath(String currentPath){
        serverPathField.clear();
        Platform.runLater(() -> serverPathField.appendText(currentPath));
    }

    public void viewSelectedUserFile() {
        userViewListField.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String selectedFileName = userViewListField.getSelectionModel().getSelectedItem();
                client.GoDown(selectedFileName);
                client.createSelectedFile();
                addMessage("Selected " + client.getSelectedFile().getName());
                if (client.getSelectedFile().isDirectory()) {
                    displayUsersListView(client.getSelectedFile().list());
                }
            }
        });

    }

    public void viewSelectedServerFile() {
        serverViewListField.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String selectedServerFileName = serverViewListField.getSelectionModel().getSelectedItem();
                try {
                    client.goDownServerPath(selectedServerFileName);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }
    public void openConnect(ActionEvent actionEvent) {
        try {
            client.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(ActionEvent actionEvent) {

    }

    public void unloadFile(ActionEvent actionEvent) {
        if (selectedFile != null && !selectedFile.isDirectory()) {
            try {
                client.unloadFile(selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void userPathRiseUp(ActionEvent actionEvent) {
        client.riseUp();
        displayUserCurrentPath(client.getCurrentPath());
        displayUsersListView(client.getSelectedFile().list());
    }
}
