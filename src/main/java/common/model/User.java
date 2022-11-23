package common.model;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String login;
    private String password;
    private String user_folder_path;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String name, String login, String password, String user_folder_path) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.user_folder_path = user_folder_path;
    }

    public User(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
