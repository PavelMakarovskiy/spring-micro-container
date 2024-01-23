package ru.jb.micro.planner.todo.service;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.todo.mq.func.MessageActionsToDo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Getter
@Setter
public class OrderHandlerService {

    Logger log = LoggerFactory.getLogger(OrderHandlerService.class);

    private final static ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final MessageActionsToDo messageActionsToDo;

    public OrderHandlerService(MessageActionsToDo messageActionsToDo) {
        this.messageActionsToDo = messageActionsToDo;
    }

    public void executeHandlingOrder(Order order) {
        CompletableFuture.runAsync(() ->
        {
            int waitQty = order.getCategories().size();
            log.info("Order # " + order.getId() + " with categories " + order.getCategories() + " " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000L * waitQty);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Order № {} is ready to send", order.getId());
            messageActionsToDo.sendReadyOrder(order);
            log.info("Order № {} is sent", order.getId());
        }, executorService);
    }

}
