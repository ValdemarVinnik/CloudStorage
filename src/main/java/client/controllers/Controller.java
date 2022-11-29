package client.controllers;


import javafx.stage.Window;

public interface Controller {

     Window getWindow();

    void displayUsersListView(String... files);

    void displayServerListView(String... files);

    void displayServerCurrentPath(String currentPath);

    void displayUserCurrentPath(String currentPath);
}
