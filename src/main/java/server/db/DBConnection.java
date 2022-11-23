package server.db;

import common.Command;
import common.model.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class DBConnection {
    private String db = "jdbc:mysql://";
    private String dbHost = "localhost";
    private String dbPort = "3306";
    private String dbUser = "root";
    private String dbPass = "314159";
    private String dbName = "cloud_store";

    private Connection connection;
    private Statement statement;

    private Connection getConnection() {
        String connectionString = db + dbHost + ":" + dbPort + "/" + dbName;
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(connectionString, dbUser, dbPass);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    private Statement getStatement() {
        if (statement == null) {
            try {
                statement = getConnection().createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statement;
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserByLogin(String login) {
        String request = Request.selectByLogin(login);

        try {
            ResultSet resultSet = getStatement().executeQuery(request);
            resultSet.next();

            if (resultSet == null) {
                return null;
            }
            String name = resultSet.getString("name");
            String password = resultSet.getString("password");
            String user_folder_path = resultSet.getString("user_folder_path");
            return new User(name, login, password, user_folder_path);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(User user) {
        String login = user.getLogin();
        return (getUserByLogin(login) == null);
        /**
         * докрутить создание записи в таблице
         */

    }


    public User getUserByLoginAndPassword(String login, String password) {
        String request = Request.selectByLoginAndPassword(login, password);

        try {
            ResultSet resultSet = getStatement().executeQuery(request);
            resultSet.next();

            if (resultSet == null) {
                return null;
            }
            String name = resultSet.getString("name");
            //String password = resultSet.getString("password");
            String user_folder_path = resultSet.getString("user_folder_path");
            return new User(name, login, password, user_folder_path);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
