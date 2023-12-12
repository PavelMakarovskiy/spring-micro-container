package ru.jb.micro.planner.clients.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @GetMapping("/get")
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("Lesia");
    }
}
