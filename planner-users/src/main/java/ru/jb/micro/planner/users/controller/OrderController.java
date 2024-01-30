package ru.jb.micro.planner.users.controller;

import lombok.extern.java.Log;
import org.springframework.http.MediaType;
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

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
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
//            orderService.createFakeOrder();
//            System.out.println("Call faker.");
            SubscriptionReadySSEOrder readyOrders = new SubscriptionReadySSEOrder(fluxSink);
            orderService.getSubscriptionSSEOrders().add(readyOrders);
            System.out.println("Added SubscriptionReadyOrders.");
        });
    }

    @GetMapping(value = "/fake_order")
    String requestPersonalFakeOrder() {
        log.info("Call personal fake order.");
        return orderService.createPersonalFakeOrder().blockFirst();
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
