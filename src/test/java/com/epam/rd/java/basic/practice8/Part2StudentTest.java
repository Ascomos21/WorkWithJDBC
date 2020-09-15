package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.Team;
import com.epam.rd.java.basic.practice8.db.entity.User;
import org.junit.Assert;
import org.junit.Test;

public class Part2StudentTest {

    @Test
    public void testWithEmptyTable() {
        DBManager dbManager = DBManager.getInstance();
        dbManager.clearTable("teams");
        dbManager.insertTeam(Team.createTeam("team1"));
        dbManager.insertTeam(Team.createTeam("team2"));
        String expectedResult = "[team1, team2]";
        Assert.assertEquals(expectedResult, dbManager.findAllTeams().toString());
    }

    @Test
    public void testInsertTeam() {
        DBManager dbManager = DBManager.getInstance();
        Assert.assertTrue(dbManager.insertTeam(Team.createTeam("teamA")));
    }

    @Test
    public void testCreateTeam() {
        User someUser = User.createUser("teamA");
        Assert.assertEquals("teamA", someUser.getLogin());
    }

    @Test
    public void testTeam() {
        Team team = new Team();
        team.setId(1);
        team.setName("team1");
        System.out.println(team.hashCode());
        Assert.assertEquals(110234003, team.hashCode());
        Assert.assertEquals("team1", team.toString());
        Assert.assertNotEquals(null, team);
    }

}