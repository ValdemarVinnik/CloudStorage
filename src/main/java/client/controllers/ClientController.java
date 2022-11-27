package client.controllers;

import client.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class ClientController implements Controller{

    public Button openConnection;
    private Client client;
    private ContextMenu menu;

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
        displayServerListView(client.getContentCurrentServersDirectory());
        displayServerCurrentPath(client.getCurrentPathOnTheServer());
        displayUserCurrentPath(client.getCurrentPath());
        viewSelectedUserFile();
        viewSelectedServerFile();
    }

    public ClientController() {
        this.client = Client.getInstance();
        client.setController(this);
        createServersContextMenu();
    }

    private void createServersContextMenu() {
        MenuItem rename = new MenuItem("rename");
        rename.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String oldFileName = serverViewListField.getSelectionModel().getSelectedItem();
                serverViewListField.setEditable(true);
                int selectedIndex = serverViewListField.getSelectionModel().getSelectedIndex();

                serverViewListField.setCellFactory(TextFieldListCell.forListView());
                serverViewListField.layout();
                serverViewListField.edit(selectedIndex);
                serverViewListField.setOnEditCommit(e -> {
                    if (e.getNewValue().length() != 0) {
                        serverViewListField.getItems().set(selectedIndex, e.getNewValue());
                        String newFileName = e.getNewValue();
                        log.debug("new File name : " + newFileName);
                        client.renameFileOnServer(oldFileName, newFileName);
                    }
                });
                serverViewListField.setEditable(false);

            }
        });

        MenuItem delete = new MenuItem("delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                log.debug("delete");

                String selectedItem = serverViewListField.getSelectionModel().getSelectedItem();
                client.deleteFileOnServer(selectedItem);
            }
        });

        MenuItem createFolder = new MenuItem("create folder");
        createFolder.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                log.debug("create folder");


                List<String> newContent = Arrays.stream(client.getContentCurrentServersDirectory())
                        .collect(Collectors.toList());
                newContent.add(0,"new folder");

                displayServerListView(newContent);
                serverViewListField.refresh();
                serverViewListField.setEditable(true);
                serverViewListField.setCellFactory(TextFieldListCell.forListView());
                serverViewListField.layout();
                serverViewListField.edit(0);
                serverViewListField.setOnEditCommit(e -> {
                    if (e.getNewValue().length() != 0) {
                        serverViewListField.getItems().set(0, e.getNewValue());
                        String newFolderName = e.getNewValue();
                        log.debug("new folder name : " + newFolderName);
                        client.createNewFolderOnServer(newFolderName);
                    }
                });
                serverViewListField.setEditable(false);
            }
        });
        this.menu = new ContextMenu(rename, delete, createFolder);
    }

    public void addMessage(String message) {
        logArea.appendText(message + "\n");

    }

    @Override
    public Window getWindow() {
        return userViewListField.getScene().getWindow();
    }

    public void displayUsersListView(String... files) {
        Platform.runLater(() -> userViewListField.getItems().clear());
        Platform.runLater(() -> userViewListField.getItems().addAll(files));
    }

    public void displayServerListView(String... files) {
        Platform.runLater(() -> serverViewListField.getItems().clear());
        Platform.runLater(() -> serverViewListField.getItems().addAll(files));
    }

    public void displayServerListView(List<String> files) {
        Platform.runLater(() -> serverViewListField.getItems().clear());
        Platform.runLater(() -> serverViewListField.getItems().addAll(files));
    }

    public void displayUserCurrentPath(String currentPath) {
        userPathField.clear();
        Platform.runLater(() -> userPathField.appendText(currentPath));
    }

    public void displayServerCurrentPath(String currentPath) {
        Platform.runLater(() -> serverPathField.clear());
        Platform.runLater(() -> serverPathField.appendText(currentPath));
    }

    public void viewSelectedUserFile() {
        userViewListField.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String selectedFileName = userViewListField.getSelectionModel().getSelectedItem();
                client.GoDown(selectedFileName);
                client.createSelectedFile();
                addMessage("Selected " + client.getSelectedUserFile().getName());
                if (client.getSelectedUserFile().isDirectory()) {
                    displayUsersListView(client.getSelectedUserFile().list());
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

            if (e.getButton() == MouseButton.SECONDARY) {

                if (menu.isShowing()) {
                    menu.hide();
                }

                double screenX = e.getScreenX();
                double screenY = e.getScreenY();

                String text = serverViewListField.getSelectionModel().getSelectedItem();

                if (text != null) {
                    menu.show(serverViewListField, screenX, screenY);
                }
            }
        });

    }

    public void openConnect(ActionEvent actionEvent) {
        try {
            client.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
           // showNotification();
        }
    }

//    private void showNotification() {
//        final Alert alert = new Alert(Alert.AlertType.ERROR,
//                "I can't connect to the server.",
//                new ButtonType("Try again", ButtonBar.ButtonData.OK_DONE),
//                new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE));
//
//        alert.setTitle("Connection error!");
//        final Optional<ButtonType> answer = alert.showAndWait();
//        final boolean isExist = answer.map(select -> select.getButtonData().isCancelButton()).orElse(false);
//        if (isExist) {
//            System.exit(0);
//        }
//    }


    public void downloadFile(ActionEvent actionEvent) {
        try {
            client.sendDownloadRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unloadFile(ActionEvent actionEvent) {
        try {
            client.sendSelectedFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void userPathRiseUp(ActionEvent actionEvent) {
        client.riseUp();
        displayUserCurrentPath(client.getCurrentPath());
        displayUsersListView(client.getSelectedUserFile().list());
    }

    public void serverPathRiseUp(ActionEvent actionEvent) throws IOException {
        client.goUpServerPath();
        displayServerCurrentPath(client.getCurrentPathOnTheServer());
        displayServerListView(client.getContentCurrentServersDirectory());
    }
}
