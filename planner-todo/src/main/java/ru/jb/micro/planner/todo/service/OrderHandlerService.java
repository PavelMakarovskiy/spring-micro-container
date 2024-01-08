package ru.jb.micro.planner.todo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.todo.mq.func.MessageActionsToDo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OrderHandlerService {

    Logger log = LoggerFactory.getLogger(OrderHandlerService.class);
    static ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final MessageActionsToDo messageActionsToDo;

    public OrderHandlerService(MessageActionsToDo messageActionsToDo) {
        this.messageActionsToDo = messageActionsToDo;
    }

    public void executeOrder(Order order) throws InterruptedException {
        CompletableFuture<Order> completableFuture = CompletableFuture.supplyAsync(() ->
        {
            int waitQty = order.getCategories().size();
            try {
                Thread.sleep(4000L * waitQty);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Order № {} is in process", order.getId());
            return order;
        }, executorService);
        try {
            messageActionsToDo.sendReadyOrder(completableFuture.get());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        // executorService.submit(new OrderHandlerThread(order));
    }

}
