package ru.jb.micro.planner.users.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jb.micro.planner.users.mq.func.MessageFuncActions;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RequestMapping("/client")
@RestController
public class UserController {

    private final UserService userService;
    private final MessageFuncActions messageFuncActions;


    @Autowired
    public UserController(UserService userService, MessageFuncActions messageFuncActions) {
        this.userService = userService;
        this.messageFuncActions = messageFuncActions;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getData(@PathVariable("id") Long id) {
        AtomicReference<String> userNameResult = new AtomicReference<>("No users with such id");
        userService.getUserById(id).ifPresent(user -> userNameResult.set(user.getName()));
        return new ResponseEntity<>(userNameResult.get(), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        userOptional.ifPresent(user -> messageFuncActions.sendNewUserMessage(user.getId()));
        return ResponseEntity.ok(userOptional.get());
    }
}
