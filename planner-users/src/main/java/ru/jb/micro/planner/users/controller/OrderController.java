package ru.jb.micro.planner.users.controller;

import com.netflix.discovery.EurekaClient;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.jb.micro.planner.users.dto.OrderDTO;
import ru.jb.micro.planner.users.order.OrderService;
import ru.jb.micro.planner.users.order.SubscriptionReadySSEOrder;

@RestController
@RequestMapping("/orders")
@Log
public class OrderController {

    private final EurekaClient eurekaClient;

    private final OrderService orderService;

    @Autowired
    public OrderController(EurekaClient eurekaClient, OrderService orderService) {
        this.eurekaClient = eurekaClient;
        this.orderService = orderService;
    }

    @PostMapping(value = "/order", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent> requestOrder(@RequestBody OrderDTO orderDTO) {
        orderService.createOrder(orderDTO);
        return Flux.create(fluxSink -> {
            SubscriptionReadySSEOrder readyOrders = new SubscriptionReadySSEOrder(fluxSink);
            orderService.getSubscriptionSSEOrders().add(readyOrders);
        });
    }

    @GetMapping(value = "/many_orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent> requestManyOrders() {
        return Flux.create(fluxSink -> {
            log.info("Call many_orders. Service id " + eurekaClient.getApplicationInfoManager().getInfo().getInstanceId() + " is working.");
            SubscriptionReadySSEOrder readyOrders = new SubscriptionReadySSEOrder(fluxSink);
            orderService.getSubscriptionSSEOrders().add(readyOrders);
            System.out.println("Added SubscriptionReadyOrders.");
        });
    }

    @GetMapping(value = "/fake_order")
    ResponseEntity<String> requestPersonalFakeOrder() {
        log.info("Call personal fake order. Service id " + eurekaClient.getApplicationInfoManager().getInfo().getInstanceId() + " is working.");
        return ResponseEntity.ok(orderService.createPersonalFakeOrder().blockFirst());
    }

}
