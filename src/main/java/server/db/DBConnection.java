package server.db;

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


    public void getAll() {
        String request = Request.selectAll();
        try {
            ResultSet resultSet = getStatement().executeQuery(request);
            //log.debug(resultSet.getCursorName());
            //log.debug(String.valueOf(resultSet.findColumn("name")));
            resultSet.next();
            log.debug( resultSet.getString("password"));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

    }

}
