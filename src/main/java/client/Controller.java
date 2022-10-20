package client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<?> userViewListField;

    @FXML
    private ListView<?> serverViewListField;

    @FXML
    private TextField logField;

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

    @FXML
    void initialize() {

    }
}
