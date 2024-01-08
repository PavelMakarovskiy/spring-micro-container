package ru.jb.micro.planner.users.order;

import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.users.dto.OrderDTO;

import java.util.List;

@Service
public interface OrderService {

    void createOrder(OrderDTO orderDTO);

    void makeOrderResponse(Order order);

    List<SubscriptionReadyOrders> getSubscriptionOrders();
}
