package ru.jb.micro.planner.entity.practice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Practice {
    private Long id;

    private String taskName;

    private String taskContent;

    private String taskSolution;

    private String remarks;
}
