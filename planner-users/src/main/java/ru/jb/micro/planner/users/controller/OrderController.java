package ru.jb.micro.planner.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.users.dto.OrderDTO;
import ru.jb.micro.planner.users.order.OrderService;

import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    ResponseEntity<String> requestOrder(@RequestBody OrderDTO orderDTO) {
        Optional<Order> order = orderService.createOrder(orderDTO);
        StringBuilder sb = new StringBuilder();
        order.get().getCategories().forEach(s -> sb.append(s).append(", "));
        return ResponseEntity.ok("Your order №".concat(String.valueOf(order.get().getId()))
                .concat(" with categories: ").concat(sb.substring(0, sb.length() - 2) + "."));
    }
}
