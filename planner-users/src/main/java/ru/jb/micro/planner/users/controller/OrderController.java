package ru.jb.micro.planner.users.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;
import reactor.core.publisher.Flux;
import ru.jb.micro.planner.users.dto.OrderDTO;
import ru.jb.micro.planner.users.order.OrderService;
import ru.jb.micro.planner.users.order.SubscriptionReadyOrders;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(value = "/order", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent> requestOrder(@RequestBody OrderDTO orderDTO) {
        orderService.createOrder(orderDTO);
        return Flux.create(fluxSink -> {
            SubscriptionReadyOrders readyOrders = new SubscriptionReadyOrders(fluxSink);
            orderService.getSubscriptionOrders().add(readyOrders);
        });
    }

    @GetMapping(value = "/many_orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SessionScope
    Flux<ServerSentEvent> requestManyOrders() {
        System.out.println("Call faker.");
        orderService.createFakeOrder();
        return Flux.create(fluxSink -> {
            SubscriptionReadyOrders readyOrders = new SubscriptionReadyOrders(fluxSink);
            orderService.getSubscriptionOrders().add(readyOrders);
            System.out.println("Added SubscriptionReadyOrders.");
        });
    }

//    @GetMapping(value = "/many_orders")
//    Integer requestManyOrders() {
//        System.out.println("Call faker.");
//        try {
//            Thread.sleep((int) (Math.random() * 5000));
//            System.out.println("Faker is working...");
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        return (int) (Math.random() * 5);
//    }
}
