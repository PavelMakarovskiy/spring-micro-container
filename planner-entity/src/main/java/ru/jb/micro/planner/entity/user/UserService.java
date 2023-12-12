package ru.jb.micro.planner.entity.user;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    Optional<User> getUserById(Long id);
}
