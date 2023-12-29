package ru.jb.micro.planner.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.users.dto.OrderDTO;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @GetMapping("/order")
    ResponseEntity<Order> requestOrder(@RequestBody OrderDTO orderDTO) {

    }
}
