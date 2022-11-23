package client.controllers;

public interface Controller {

    void displayUsersListView(String... files);

    void displayServerListView(String... files);

    void displayServerCurrentPath(String currentPath);

    void displayUserCurrentPath(String currentPath);
}
