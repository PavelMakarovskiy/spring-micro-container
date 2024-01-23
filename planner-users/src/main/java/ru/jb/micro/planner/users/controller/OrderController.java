package ru.jb.micro.planner.users.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping(value = "/many_orders/{qty}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent> requestManyOrders(@PathVariable("qty") Long qty) {
        orderService.makeManyOrders(qty);
        return Flux.create(fluxSink -> {
            SubscriptionReadyOrders readyOrders = new SubscriptionReadyOrders(fluxSink);
            orderService.getSubscriptionOrders().add(readyOrders);
        });
    }
}
