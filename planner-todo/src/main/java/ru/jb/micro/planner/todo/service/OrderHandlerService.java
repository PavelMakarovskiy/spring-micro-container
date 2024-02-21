package ru.jb.micro.planner.todo.service;

import com.netflix.discovery.EurekaClient;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.entity.order.OrderStatus;
import ru.jb.micro.planner.todo.mq.func.MessageActionsToDo;

import java.util.Map;
import java.util.Optional;
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
            messageActionsToDo.sendReadyOrder(order);
            log.info("Order № {} is sent", order.getId());
        }, executorService);
    }

    public void executeHandlingWsOrder(Map<Order, WebSocketSession> map) {
        CompletableFuture.runAsync(() ->
                {
                    log.info("Service id " + eurekaClient.getApplicationInfoManager().getInfo().getInstanceId() + " is working.");
                    Optional<Map.Entry<Order, WebSocketSession>> entry = map.entrySet().stream().findFirst();
                    if (entry.isPresent()) {
                        messageActionsToDo.sendWsOrderInfo(map);
                        Order currentOrder = entry.get().getKey();
                        WebSocketSession wsSession = entry.get().getValue();
                        int waitQty = currentOrder.getCategories().size();
                        log.info("Order # " + currentOrder.getId() + " with categories " + currentOrder.getCategories() + " " + Thread.currentThread().getName());
                        try {
                            Thread.sleep(2000L * waitQty);
                            //  Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        currentOrder.setStatus(OrderStatus.READY);
                        map.clear();
                        map.put(currentOrder, wsSession);
                        log.info("Order № {} is ready to send", currentOrder.getId());
                        messageActionsToDo.sendWsOrderInfo(map);
                        log.info("Order № {} is sent", currentOrder.getId());
                    }
                }
        );
    }

}
