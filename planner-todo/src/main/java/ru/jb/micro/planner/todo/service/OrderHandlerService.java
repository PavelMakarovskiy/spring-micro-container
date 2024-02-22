package ru.jb.micro.planner.todo.service;

import com.netflix.discovery.EurekaClient;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.entity.order.OrderStatus;
import ru.jb.micro.planner.todo.mq.func.MessageActionsToDo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Getter
@Setter
public class OrderHandlerService {

    private final EurekaClient eurekaClient;

    Logger log = LoggerFactory.getLogger(OrderHandlerService.class);

    private final static ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final MessageActionsToDo messageActionsToDo;

    public OrderHandlerService(EurekaClient eurekaClient, MessageActionsToDo messageActionsToDo) {
        this.eurekaClient = eurekaClient;
        this.messageActionsToDo = messageActionsToDo;
    }

    public void executeHandlingOrder(Order order) {
        CompletableFuture.runAsync(() ->
        {
            log.info("Service id " + eurekaClient.getApplicationInfoManager().getInfo().getInstanceId() + " is working.");
            int waitQty = order.getCategories().size();
            log.info("Order # " + order.getId() + " with categories " + order.getCategories() + " " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000L * waitQty);
                //  Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Order № {} is ready to send", order.getId());
            messageActionsToDo.sendReadyOrderToDoSide(order);
            log.info("Order № {} is sent", order.getId());
        }, executorService);
    }

    public void executeHandlingWsOrder(Order order) {
        CompletableFuture.runAsync(() ->
        {
            log.info("Service id " + eurekaClient.getApplicationInfoManager().getInfo().getInstanceId() + " is working.");
            messageActionsToDo.sendReadyWsOrderToDoSide(order);
            log.info("Order # {} is {}" , order.getId(), order.getStatus());
            int waitQty = order.getCategories().size();
            log.info("Order # " + order.getId() + " with categories " + order.getCategories() + " " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000L * waitQty);
                //  Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            order.setStatus(OrderStatus.READY);
            log.info("Order № {} has status {}", order.getId(), order.getStatus());
            messageActionsToDo.sendReadyWsOrderToDoSide(order);
            log.info("Order № {} is sent", order.getId());
        }, executorService);
    }

}
