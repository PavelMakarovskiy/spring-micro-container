package ru.jb.micro.planner.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/get")
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("Lesia");
    }
}
