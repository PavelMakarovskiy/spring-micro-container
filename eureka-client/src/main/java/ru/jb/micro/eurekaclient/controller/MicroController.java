package ru.jb.micro.eurekaclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class MicroController {

    @GetMapping("/micro")
    public String micro() {
        return "micro route";
    }
}
