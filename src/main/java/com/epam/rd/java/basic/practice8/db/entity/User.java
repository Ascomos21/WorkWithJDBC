package com.epam.rd.java.basic.practice8.db.entity;

import java.util.Objects;

public class User {
    String login;
    int id;

    public String getLogin() {
        return login;
    }

    public User(String login) {
        this.login = login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public static User createUser(String name) {
        return new User(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return
                login;

    }
}
