package ru.jb.micro.planner.entity.order;

import lombok.Getter;
import lombok.Setter;
import ru.jb.micro.planner.entity.category.Category;

import java.util.List;

@Getter
@Setter
public class Order {

    private Long id;

    private List<Category> categories;

    private Long user_id;
}
