package ru.jb.micro.planner.users.mq.func;

import org.springframework.messaging.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.entity.order.OrderStatus;
import ru.jb.micro.planner.users.order.OrderService;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Configuration
public class MessageFuncConsume {

    private final OrderService orderService;

    public MessageFuncConsume(OrderService orderService) {
        this.orderService = orderService;
    }

    @Bean
    public Consumer<Message<Order>> orderReadyUserSideConsume() {
        return message -> orderService.makeOrderResponse(message.getPayload());
    }

    @Bean
    public Consumer<Message<Order>> wsOrdersUserSideConsume() {
        return message -> {
            Map<Long, WebSocketSession> wsMap = orderService.getWsMap();
            Order order = message.getPayload();
            orderService.updateOrderStatus(order);
            if (wsMap.containsKey(order.getId())) {
                WebSocketSession wsSession = wsMap.get(order.getId());
                try {
                    Optional<Order> optionalOrder = orderService.getOrderByIdWithCategories(order.getId());
                    if (optionalOrder.isPresent()) {
                        Order updatedOrder = optionalOrder.get();
                        wsSession.sendMessage(new TextMessage("Order #".concat(updatedOrder.getId().toString()).concat(" is ".concat(order.getStatus().toString()))));
                        if (updatedOrder.getStatus().equals(OrderStatus.READY)) {
                            wsMap.remove(updatedOrder.getId());
                            wsSession.close();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
