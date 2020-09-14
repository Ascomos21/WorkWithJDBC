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
            out = prop.getProperty("connection.url");

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
        ResultSet selectIdTeams = null;
        ResultSet resultForTeams = null;
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement();
             Statement statement1 = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            selectIdTeams = statement.executeQuery("SELECT * FROM users_teams where user_id = " + user.getId());
            while (selectIdTeams.next()) {

                resultForTeams = statement1.executeQuery("SELECT  * FROM teams where id =" + selectIdTeams.getInt(2));
                while (resultForTeams.next()) {
                    teamList.add(new Team(resultForTeams.getInt(1), resultForTeams.getString(2)));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeResultSet(selectIdTeams);
            closeResultSet(resultForTeams);
        }
        return teamList;

    }

    public void setTeamsForUser(User user, Team... teams) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dbManager.getConnection(getUrlFromProperties());
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO users_teams VALUES (?,?)");
            for (Team team : teams) {
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setInt(2, team.getId());
                preparedStatement.execute();
            }
            connection.commit();
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
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
        int id;
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                id = resultSet.getInt(1);
                String login = resultSet.getString(2);
                userList.add(new User(id, login));
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
        int id;
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            resultSet = statement.executeQuery("SELECT * FROM teams");
            while (resultSet.next()) {
                id = resultSet.getInt(1);

                String name = resultSet.getString(2);
                userList.add(new Team(id, name));
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
        PreparedStatement statementForDeleteFromTeam = null;
        try (Connection conn = DriverManager.getConnection(getUrlFromProperties())) {
            statement = conn.prepareStatement("delete from users_teams where team_id= ?");
            statement.setInt(1, team.getId());
            statement.executeUpdate();
            statementForDeleteFromTeam = conn.prepareStatement("delete from teams where id= ?");
            statementForDeleteFromTeam.setInt(1, team.getId());
            statementForDeleteFromTeam.executeUpdate();
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

    public void updateTeam(Team team) {
        PreparedStatement statement = null;
        try (Connection connection = DriverManager.getConnection(getUrlFromProperties())) {
            statement = connection.prepareStatement("update teams SET name= ? WHERE id = ?");
            statement.setString(1, team.getName());
            statement.setInt(2, team.getId());
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
