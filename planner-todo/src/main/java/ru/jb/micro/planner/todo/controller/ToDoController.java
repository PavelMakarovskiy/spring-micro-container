package ru.jb.micro.planner.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jb.micro.planner.todo.service.DataService;

@RestController
@RequestMapping("/category")
public class ToDoController {

    private final DataService dataService;

    public ToDoController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/get")
    public ResponseEntity<String> getCategory() {
        return ResponseEntity.ok(dataService.getResult());
    }
}
