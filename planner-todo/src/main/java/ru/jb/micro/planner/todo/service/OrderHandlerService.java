package ru.jb.micro.planner.todo.service;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.todo.mq.func.MessageActionsToDo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Getter
@Setter
public class OrderHandlerService {

    Logger log = LoggerFactory.getLogger(OrderHandlerService.class);


    public void executeHandlingOrder() {

    }

}
