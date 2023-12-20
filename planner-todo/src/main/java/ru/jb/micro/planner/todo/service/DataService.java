package ru.jb.micro.planner.todo.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class DataService {

    String result;

    public void initData(Long userId) {
        setResult("we got user id: ".concat(userId.toString()));
    }
}
