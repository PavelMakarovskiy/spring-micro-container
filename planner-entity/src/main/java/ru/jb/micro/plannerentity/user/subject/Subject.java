package ru.jb.micro.plannerentity.user.subject;


import lombok.Getter;
import lombok.Setter;
import ru.jb.micro.plannerentity.category.Category;
import ru.jb.micro.plannerentity.practice.Practice;
import ru.jb.micro.plannerentity.theory.Theory;

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
