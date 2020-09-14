package com.epam.rd.java.basic.practice8.db.entity;

import java.util.Objects;

public class Team {
    int id;
    String name;

    public String getName() {
        return name;
    }

    public Team(String name) {
        this.name = name;
    }

    public Team(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Team createTeam(String name) {
        return new Team(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
