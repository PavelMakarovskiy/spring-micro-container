package ru.jb.micro.planner.users.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long user_id;
    private String user_name;
    private List<String> categories;

}
