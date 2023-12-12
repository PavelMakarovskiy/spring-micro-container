package ru.jb.micro.planner.entity.user.subject;


import lombok.Getter;
import lombok.Setter;
import ru.jb.micro.planner.entity.category.Category;
import ru.jb.micro.planner.entity.practice.Practice;
import ru.jb.micro.planner.entity.theory.Theory;

import java.util.List;

@Getter
@Setter
public class Subject {

    private Long id;

    private String name;

    private List<Category> categories;

    private String description;

    private List<Theory> theoryList;

    private List<Practice> practiceList;
}
