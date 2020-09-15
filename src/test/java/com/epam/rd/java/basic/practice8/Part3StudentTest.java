package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.Team;
import com.epam.rd.java.basic.practice8.db.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class Part3StudentTest {

    @Test
    public void testGet() {
        DBManager dbManager = DBManager.getInstance();
        dbManager.clearTable("teams");
        dbManager.clearTable("users_teams");
        dbManager.clearTable("users");
        dbManager.insertUser(User.createUser("petrov"));
        dbManager.insertTeam(Team.createTeam("teamB"));

        User someUser = dbManager.getUser("petrov");
        Team someTeam = dbManager.getTeam("teamB");
        Assert.assertEquals("petrov", someUser.getLogin());
        Assert.assertEquals("teamB", someTeam.getName());
        dbManager.setTeamsForUser(someUser, someTeam);
        for (User user : dbManager.findAllUsers()) {

            for (Team team : dbManager.getUserTeams(user))
                Assert.assertEquals("teamB", team.getName());

        }

    }

    @Test
    public void testUserTeams() {
        DBManager dbManager = DBManager.getInstance();

        List<Team> teams = new ArrayList<>();
        List<Team> teams2 = new ArrayList<>();
        for (User user : dbManager.findAllUsers()) {

            for (Team team : dbManager.getUserTeams(user)) {
                teams.add(team);
            }
            for (Team team : dbManager.getUserTeams(user)) {
                teams2.add(team);
            }
            for (int i = 0; i < teams.size(); i++) {
                Assert.assertEquals(teams.get(i).getName(), teams2.get(i).getName());
            }
        }
    }


}