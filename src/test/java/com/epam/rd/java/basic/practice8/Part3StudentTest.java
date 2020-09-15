package com.epam.rd.java.basic.practice8;

import com.epam.rd.java.basic.practice8.db.DBManager;
import com.epam.rd.java.basic.practice8.db.entity.Team;
import com.epam.rd.java.basic.practice8.db.entity.User;
import org.junit.Assert;
import org.junit.Test;


public class Part3StudentTest {

    @Test
    public void testGet(){
        DBManager dbManager = DBManager.getInstance();
        User someUser = dbManager.getUser("ivanov");
        Team someTeam = dbManager.getTeam("teamA");
        Assert.assertEquals("ivanov", someUser.getLogin());
        Assert.assertEquals("teamA", someTeam.getName());
    }



}