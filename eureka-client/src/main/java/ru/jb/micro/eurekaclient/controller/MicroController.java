package ru.jb.micro.eurekaclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MicroController {

    @GetMapping("/micro")
    public String micro() {
        return "micro";
    }
}
