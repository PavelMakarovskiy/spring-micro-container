package ru.jb.micro.plannerentity.user;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {
    Optional<User> getUserById(Long id);
}
