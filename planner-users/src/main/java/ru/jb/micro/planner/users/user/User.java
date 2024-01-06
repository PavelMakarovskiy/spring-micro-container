package ru.jb.micro.planner.users.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;

    private String name;

    public User(String name) {
        this.name = name;
    }
}
