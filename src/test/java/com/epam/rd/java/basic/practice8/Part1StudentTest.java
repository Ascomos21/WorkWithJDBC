package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.User;
import org.junit.Assert;
import org.junit.Test;

public class Part1StudentTest {

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
        if (user.equals("Asoka"))
            Assert.assertEquals("Asoka", user.toString());
    }


}