package ru.jb.micro.planner.entity.category;

import lombok.Getter;

@Getter
public enum Category {
    JAVA("Java"),
    KOTLIN("Kotlin"),
    SQL("SQL"),
    SPRING("Spring"),
    DATA_BASE("Data bases"),
    MICROSERVICES("Microservices"),
    PERFORMANCE("Memory optimization, performance improvement, cashing"),
    BROKER_MESSAGES("Broker messages"),
    FRONTEND("Frontend"),
    NOSQL("NOSQL DB"),
    SECURITY("Information Security"),
    HIGH_LOAD("High load"),
    NETWORKS("Networks, protocols"),
    SOFT_SKILLS("Soft skills");

    private String description;

    Category(String description) {
        this.description = description;
    }
}
