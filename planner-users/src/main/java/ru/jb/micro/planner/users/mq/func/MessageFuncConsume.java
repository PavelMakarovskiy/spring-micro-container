package ru.jb.micro.planner.users.mq.func;

import org.springframework.messaging.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.jb.micro.planner.entity.order.Order;
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
    public Consumer<Message<Order>> orderReadyConsume() {
        return message -> orderService.makeOrderResponse(message.getPayload());
    }

    @Bean
    public Consumer<Message<Map<Order, WebSocketSession>>> wsOrderConsume() {
        return message -> {
            Optional<Map.Entry<Order, WebSocketSession>> entry = message.getPayload()
                    .entrySet().stream().findFirst();
            if (entry.isPresent()) {
                Order order = entry.get().getKey();
                WebSocketSession wsSession = entry.get().getValue();
                try {
                    wsSession.sendMessage(new TextMessage("Order #".concat(order.getId().toString())
                            .concat(" is ".concat(order.getStatus().toString()))));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
