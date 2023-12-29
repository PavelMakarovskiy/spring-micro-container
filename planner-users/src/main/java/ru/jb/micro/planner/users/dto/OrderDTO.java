package ru.jb.micro.planner.users.dto;

import ru.jb.micro.planner.entity.category.Category;

import java.util.List;

public class OrderDTO {
    private Long id;

    private List<Category> categories;

    private Long user_id;

    private String user_name;
}
