package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.Team;
import org.junit.Assert;
import org.junit.Test;

public class Part5StudentTest {
    @Test
    public void testUpdate() {
        DBManager dbManager = DBManager.getInstance();
        Team teamC = Team.createTeam("teamC");
        dbManager.insertTeam(teamC);

        teamC.setName("teamX");
        Assert.assertTrue(dbManager.updateTeam(teamC));
    }
}