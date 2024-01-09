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
    static ExecutorService executorService = Executors.newFixedThreadPool(3);

    ConcurrentLinkedQueue<Order> orderQueue = new ConcurrentLinkedQueue<>();

    private final MessageActionsToDo messageActionsToDo;

    public OrderHandlerService(MessageActionsToDo messageActionsToDo) {
        this.messageActionsToDo = messageActionsToDo;
    }

    public void executeHandlingOrder() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        while (true) {
            if (!orderQueue.isEmpty()) {
          //      log.info("Order # " + order.getId() + " with categories " + order.getCategories() + " started " + Thread.currentThread().getName());
                //  orderHandlerService.executeOrder(message.getPayload());
                CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() ->
                {
                    Order order = orderQueue.poll();
                    int waitQty = order.getCategories().size();
                    log.info("Order # " + order.getId() + " with categories " + order.getCategories() + " " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(4000L * waitQty);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("Order № {} is ready to send", order.getId());
                    messageActionsToDo.sendReadyOrder(order);
                    log.info("Order № {} is sent", order.getId());
                }, executorService);

            }
        }
        // executorService.submit(new OrderHandlerThread(order));
    }

}
