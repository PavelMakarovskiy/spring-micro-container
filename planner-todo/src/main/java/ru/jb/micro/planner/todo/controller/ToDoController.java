package ru.jb.micro.planner.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class ToDoController {

    @GetMapping("/get")
    public ResponseEntity<String> getCategory() {
        return ResponseEntity.ok("Category");
    }
}
