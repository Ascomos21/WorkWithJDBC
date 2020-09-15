package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.User;
import org.junit.Assert;
import org.junit.Test;

public class Part1StudentTest {

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


}