package ru.jb.micro.plannerentity.user;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {
    private Long userId;

    private String name;
}
