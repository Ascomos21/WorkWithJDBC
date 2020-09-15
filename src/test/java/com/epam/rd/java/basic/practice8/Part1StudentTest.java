package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.Team;
import com.epam.rd.java.basic.practice8.db.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Part1StudentTest {
    private static final PrintStream STREAM_OUT = System.out;

    @Test
    public void testWithEmptyTable() {
        DBManager dbManager = DBManager.getInstance();
        dbManager.clearTable("users");
        System.out.println(dbManager.findAllUsers().toString());
        dbManager.insertUser(User.createUser("obama"));
        dbManager.insertUser(User.createUser("petrov"));
        String expectedResult = "[obama, petrov]";
        Assert.assertEquals(expectedResult, dbManager.findAllUsers().toString());
    }

    @Test
    public void testInsertTest() {
        DBManager dbManager = DBManager.getInstance();
        Assert.assertTrue(dbManager.insertUser(User.createUser("Skauoker")));
    }

    @Test
    public void testCreateUser() {
        User someUser = User.createUser("Yoda");
        Assert.assertEquals("Yoda", someUser.getLogin());
    }

    @Test
    public void testUser() {
        User user = new User();
        user.setId(1);
        user.setLogin("Asoka");
        Assert.assertEquals(63564946, user.hashCode());
        Assert.assertEquals("Asoka", user.toString());
        Assert.assertNotEquals(null, user);
    }

    @Test
    public void testDemoMain() {
        DBManager dbManager = DBManager.getInstance();

        dbManager.clearTable("teams");
        dbManager.clearTable("users_teams");
        dbManager.clearTable("users");
        dbManager.insertUser(User.createUser("ivanov"));
        dbManager.insertTeam(Team.createTeam("teamA"));
        String expectedString = "[ivanov, obama, petrov]" + System.lineSeparator() +
                " ===========================" + System.lineSeparator() +
                "[teamA, teamB, teamC]" + System.lineSeparator() +
                " ===========================" + System.lineSeparator() +
                "[teamA]" + System.lineSeparator() +
                "~~~~~" + System.lineSeparator() +
                "[teamA, teamB, teamC]" + System.lineSeparator() +
                "~~~~~" + System.lineSeparator() +
                "[teamA, teamB]" + System.lineSeparator() +
                "~~~~~" + System.lineSeparator() +
                "===========================" + System.lineSeparator() +
                "[teamB, teamX]" + System.lineSeparator();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);
        Demo.main(new String[]{});
        String result = outputStream.toString();
        System.setOut(STREAM_OUT);
        System.out.println(result.equals(expectedString));
        Assert.assertEquals(expectedString, result);
    }


}