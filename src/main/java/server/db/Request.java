package server.db;

public class Request {

    static String selectByLogin(String login) {
        return String.format("select * from users where login = '%s'", login);
    }

    static String selectByLoginAndPassword(String login, String password) {
        return String.format("select * from users where login = '%s' AND password = '%s'", login, password);
    }

    public static String createNewUser(String name, String login, String password, String user_folder_path) {
        return String.format("INSERT INTO users (name, login, password, user_folder_path) VALUES ('%s','%s','%s','%s')",
                name, login, password, user_folder_path);
    }

    public static String selectUserFolderPath(String login, String password) {
        return String.format("select user_folder_path from users where login = '%s' AND password = '%s'", login, password);
    }
}
