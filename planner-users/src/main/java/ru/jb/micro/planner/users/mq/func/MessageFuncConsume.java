package ru.jb.micro.planner.users.mq.func;

import org.springframework.messaging.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.users.order.OrderService;

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
}
