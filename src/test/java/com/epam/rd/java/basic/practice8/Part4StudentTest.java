package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.Team;
import org.junit.Assert;
import org.junit.Test;

public class Part4StudentTest {

    @Test
    public void testDelete() {
        DBManager dbManager = DBManager.getInstance();
        Team teamB = Team.createTeam("boosterenko");
        dbManager.insertTeam(teamB);
        Assert.assertTrue(dbManager.deleteTeam(teamB));

    }

}