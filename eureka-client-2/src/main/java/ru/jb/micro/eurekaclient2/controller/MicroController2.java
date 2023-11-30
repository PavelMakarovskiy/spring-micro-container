package ru.jb.micro.eurekaclient2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/new")
public class MicroController2 {

    @GetMapping("/micro2")
    public String micro() {
        return "micro route2";
    }
}
