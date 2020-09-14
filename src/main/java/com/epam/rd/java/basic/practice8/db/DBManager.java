package com.epam.rd.java.basic.practice8.db;

import com.epam.rd.java.basic.practice8.db.entity.Team;
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
        if (dbManager == null)
            dbManager = new DBManager();
        return dbManager;
    }


    public User getUser(String login) {
        int id = 0;
        ResultSet resultSet = null;
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM users " +
                    "WHERE login = '" + login + "'");
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeResultSet(resultSet);
        }
        return new User(id, login);
    }

    public Team getTeam(String name) {
        ResultSet resultSet = null;
        int id = 0;
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM users " +
                    "WHERE name = '" + name + "'");
            id = resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeResultSet(resultSet);
        }
        return new Team(id, name);
    }

    public void setTeamsForUser(User user, Team... teams) {
        Connection connection;
        PreparedStatement preparedStatement = null;
        try {
            connection = dbManager.getConnection(getUrlFromProperties());
            preparedStatement = connection.prepareStatement("INSERT INTO users_teams VALUES (?,?)");
            connection.setAutoCommit(false);
            for (Team team : teams) {
                preparedStatement.setString(1, user.getLogin());
                preparedStatement.setString(2, team.getName());
                preparedStatement.execute();
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void insertUser(User user) {
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            statement.executeUpdate("INSERT INTO users (login) VALUES ('" + user.getLogin() + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void insertTeam(Team team) {
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            statement.executeUpdate("INSERT INTO  teams (name) VALUES ('" + team.getName() + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<User> findAllUsers() {
        List<User> userList = new ArrayList<>();
        ResultSet resultSet = null;
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                String login = resultSet.getString(2);
                userList.add(new User(login));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeResultSet(resultSet);
        }
        return userList;
    }

    public List<Team> findAllTeams() {
        List<Team> userList = new ArrayList<>();
        ResultSet resultSet = null;
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM teams");
            while (resultSet.next()) {
                String name = resultSet.getString(2);
                userList.add(new Team(name));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeResultSet(resultSet);
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

    private void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null)
                resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Connection getConnection(String connectionUrl) throws SQLException {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(connectionUrl);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

}
