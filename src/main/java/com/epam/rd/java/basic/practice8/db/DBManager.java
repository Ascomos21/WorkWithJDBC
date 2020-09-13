package com.epam.rd.java.basic.practice8.db;

import com.epam.rd.java.basic.practice8.db.entity.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBManager {
    private static DBManager dbManager;

    private DBManager() {
    }

    public static DBManager getInstance() {
        dbManager = new DBManager();
        return dbManager;
    }

    public void insertUser(User user) {
        Statement statement;
        try {
           // System.out.println(user.getLogin());
            statement = dbManager.getConnection(getUrlFromProperties()).createStatement();
            statement.executeUpdate("INSERT INTO users (login) VALUES ('" + user.getLogin() + "')");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<User> findAllUsers() {
        List<User> userList = new ArrayList<>();

        try {
            Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                String login = resultSet.getString(2);
                userList.add(new User(login));
            }
         //   System.out.println(userList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userList;
    }

    public String getUrlFromProperties() {
        String out = null;
        try (InputStream input = new FileInputStream("app.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            out = prop.getProperty("connection.url");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    public Connection getConnection(String connectionUrl) throws SQLException {
        /*try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Success.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // Fail.
        }*/
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(connectionUrl);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

}
