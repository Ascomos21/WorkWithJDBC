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

    public Connection getConnection(String connectionUrl) throws SQLException {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(connectionUrl);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    public String getUrlFromProperties() {
        String out = null;
        try (InputStream input = new FileInputStream("app.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            out = prop.getProperty("myConnection.url");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return out;
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
            resultSet = statement.executeQuery("SELECT * FROM teams " +
                    "WHERE name = '" + name + "'");
            if (resultSet.next())
                id = resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeResultSet(resultSet);
        }
        return new Team(id, name);
    }

    public List<Team> getUserTeams(User user) {
        List<Team> teamList = new ArrayList<>();
        ResultSet resultSet = null;
        ResultSet resultForTeams = null;
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement();
             Statement statement1 = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM users_teams where team_id =" + user.getId());
            while (resultSet.next()) {
                resultForTeams = statement1.executeQuery("SELECT  * FROM teams where id =" + resultSet.getInt(2));
                while (resultForTeams.next()) {
                    teamList.add(new Team(resultForTeams.getInt(1), resultForTeams.getString(2)));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeResultSet(resultSet);
            closeResultSet(resultForTeams);
        }
        return teamList;

    }

    public void setTeamsForUser(User user, Team... teams) {
        Connection connection;
        PreparedStatement preparedStatement = null;
        try {
            connection = dbManager.getConnection(getUrlFromProperties());
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO users_teams VALUES (?,?)");
            for (Team team : teams) {
               // System.out.println("USER ID==>" + user.getId());
               // System.out.println("Team Id==>" + team.getId());
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setInt(2, team.getId());
                preparedStatement.execute();
            }
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
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

    public void deleteTeam(Team team) {
        PreparedStatement statement = null;
        try (Connection conn = DriverManager.getConnection(getUrlFromProperties())) {
            statement = conn.prepareStatement("delete from users_teams where team_id= ?");
            statement.setInt(1, team.getId());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }


    private void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null)
                resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
