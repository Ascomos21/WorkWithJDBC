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
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DBManager {
    private static final Logger logger = Logger.getLogger(DBManager.class.getName());
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
            logger.log(Level.WARNING, throwables.getMessage());
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
            logger.log(Level.WARNING, ex.getMessage());
        }
        return out;
    }

    public User getUser(String login) {
        int id = 0;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try (Connection connection = dbManager.getConnection(getUrlFromProperties())) {
            preparedStatement = connection.prepareStatement("SELECT * FROM users " +
                    "WHERE login = ?");
            preparedStatement.setString(1, login);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            logger.log(Level.WARNING, throwables.getMessage());
        } finally {
            if (preparedStatement!=null){
                try {
                    preparedStatement.close();
                } catch (SQLException throwables) {
                    logger.log(Level.WARNING, throwables.getMessage());
                }
            }
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
            logger.log(Level.WARNING, throwables.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return new Team(id, name);
    }

    public List<Team> getUserTeams(User user) {
        List<Team> teamList = new ArrayList<>();
        ResultSet selectIdTeams = null;
        PreparedStatement preparedStatement = null;
        try (Connection conn = dbManager.getConnection(getUrlFromProperties())) {
            preparedStatement = conn.prepareStatement("SELECT * FROM users_teams inner join teams on team_id = id where user_id = ?");
            preparedStatement.setInt(1, user.getId());
            selectIdTeams = preparedStatement.executeQuery();
            while (selectIdTeams.next()) {
                teamList.add(new Team(selectIdTeams.getInt("team_id"), selectIdTeams.getString("name")));
            }
        } catch (SQLException throwables) {
            logger.log(Level.WARNING, throwables.getMessage());
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException throwables) {
                    logger.log(Level.WARNING, throwables.getMessage());
                }
            }
            closeResultSet(selectIdTeams);
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
                if (connection != null)
                    connection.rollback();
            } catch (SQLException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
            logger.log(Level.WARNING, throwables.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException throwables) {
                    logger.log(Level.WARNING, throwables.getMessage());
                }
            }
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException throwables) {
                logger.log(Level.WARNING, throwables.getMessage());
            }
        }
    }

    public boolean insertUser(User user) {
        boolean flag = false;
        try (Connection connection = dbManager.getConnection(getUrlFromProperties());
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (login) VALUES (?)", RETURN_GENERATED_KEYS);) {

            statement.setString(1, user.getLogin());
            statement.executeUpdate();
            flag = true;
        } catch (SQLException throwables) {
            logger.log(Level.WARNING, throwables.getMessage());
        }
        return flag;
    }

    public boolean insertTeam(Team team) {
        boolean flag = false;
        try (Statement statement = dbManager.getConnection(getUrlFromProperties()).createStatement()) {
            statement.executeUpdate("INSERT INTO  teams (name) VALUES ('" + team.getName() + "')", RETURN_GENERATED_KEYS);
            flag = true;
        } catch (SQLException throwables) {
            logger.log(Level.WARNING, throwables.getMessage());
        }
        return flag;
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
            logger.log(Level.WARNING, throwables.getMessage());
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
            logger.log(Level.WARNING, throwables.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return userList;
    }

    public boolean deleteTeam(Team team) {
        boolean flag = false;
        PreparedStatement statement = null;
        PreparedStatement statementForDeleteFromTeam = null;
        try (Connection conn = DriverManager.getConnection(getUrlFromProperties())) {
            statement = conn.prepareStatement("delete from users_teams where team_id= ?");
            statement.setInt(1, team.getId());
            statement.executeUpdate();
            statementForDeleteFromTeam = conn.prepareStatement("delete from teams where id= ?");
            statementForDeleteFromTeam.setInt(1, team.getId());
            statementForDeleteFromTeam.executeUpdate();
            flag = true;
        } catch (SQLException throwables) {
            logger.log(Level.WARNING, throwables.getMessage());
        } finally {
            try {
                if (statementForDeleteFromTeam != null)
                    statementForDeleteFromTeam.close();
            } catch (SQLException throwables) {
                logger.log(Level.WARNING, throwables.getMessage());
            }
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException throwables) {
                logger.log(Level.WARNING, throwables.getMessage());
            }
        }
        return flag;
    }

    public boolean updateTeam(Team team) {
        boolean flag = false;
        PreparedStatement statement = null;
        try (Connection connection = DriverManager.getConnection(getUrlFromProperties())) {
            statement = connection.prepareStatement("update teams SET name= ? WHERE id = ?");
            statement.setString(1, team.getName());
            statement.setInt(2, team.getId());
            statement.executeUpdate();
            flag = true;
        } catch (SQLException throwables) {
            logger.log(Level.WARNING, throwables.getMessage());
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException throwables) {
                logger.log(Level.WARNING, throwables.getMessage());
            }
        }
        return flag;

    }

    private void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null)
                resultSet.close();
        } catch (SQLException throwables) {
            logger.log(Level.WARNING, throwables.getMessage());
        }
    }


}
