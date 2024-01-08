package ru.jb.micro.planner.users.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.users.dto.OrderDTO;
import ru.jb.micro.planner.users.order.OrderService;
import ru.jb.micro.planner.users.order.SubscriptionReadyOrders;
import ru.jb.micro.planner.users.user.User;
import ru.jb.micro.planner.users.user.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping(value = "/order", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent> requestOrder(@RequestBody OrderDTO orderDTO) {
        orderService.createOrder(orderDTO);
        return Flux.create(fluxSink -> {
            SubscriptionReadyOrders readyOrders = new SubscriptionReadyOrders(fluxSink);
            orderService.getSubscriptionOrders().add(readyOrders);
        });
    }
}
