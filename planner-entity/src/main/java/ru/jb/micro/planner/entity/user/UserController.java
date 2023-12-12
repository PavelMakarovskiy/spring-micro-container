package ru.jb.micro.planner.entity.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;

@RequestMapping("/user")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<String> getData(@PathVariable("id") Long id) {
        AtomicReference<String> userNameResult = new AtomicReference<>("No users with such id");
        userService.getUserById(id).ifPresent(user -> userNameResult.set(user.getName()));
        return new ResponseEntity<>(userNameResult.get(), HttpStatus.OK);
    }
}
