package ru.jb.micro.planner.users.order;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.users.dto.OrderDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface OrderService {

    void createOrder(OrderDTO orderDTO);

    void makeOrderResponse(Order order);

    List<SubscriptionReadySSEOrder> getSubscriptionSSEOrders();

    List<SubscriptionReadyOrder> getSubscriptionReadyOrders();

    Long createFakeOrder();

    Flux<String> createPersonalFakeOrder();

    void createWsFakeOrder(WebSocketSession wsSession);

    Map<Long, WebSocketSession> getWsMap();

    void updateOrderStatus(Order order);

    Optional<Order> getOrderByIdWithCategories(Long orderId);
}
