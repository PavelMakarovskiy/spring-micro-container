package ru.jb.micro.planner.users.order;

import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.users.dto.OrderDTO;

import java.util.Optional;

@Service
public interface OrderService {

    Optional<Order> createOrder(OrderDTO orderDTO);
}
