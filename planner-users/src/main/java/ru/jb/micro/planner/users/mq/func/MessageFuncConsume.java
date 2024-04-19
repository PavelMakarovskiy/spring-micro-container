package ru.jb.micro.planner.users.mq.func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.entity.order.OrderStatus;
import ru.jb.micro.planner.users.order.OrderService;
import ru.jb.micro.planner.users.user.User;
import ru.jb.micro.planner.users.user.UserService;
import ru.jb.micro.planner.websocket.WebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Configuration
public class MessageFuncConsume {

    Logger log = LoggerFactory.getLogger(MessageFuncConsume.class);

    private final OrderService orderService;

    private final UserService userService;

    public MessageFuncConsume(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
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
//                        if (updatedOrder.getStatus().equals(OrderStatus.READY)) {
//                            wsMap.remove(updatedOrder.getId());
//                            wsSession.close();
//                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Bean
    public Consumer<Message<Order>> wsAdvOrdersUserSideConsume() {
        return message -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Map<Long, WebSocketSession> wsMap = orderService.getWsMap();
            Order order = message.getPayload();
            if (wsMap.containsKey(order.getId())) {
                WebSocketSession wsSession = wsMap.get(order.getId());
                Optional<Order> optionalOrder = orderService.getOrderByIdWithCategories(order.getId());
                if (optionalOrder.isPresent()) {
                    Order updatedOrder = optionalOrder.get();
                    if (updatedOrder.getStatus().equals(OrderStatus.READY)) {
                        try {
                            Optional<User> optionalUser = userService.getUserById(updatedOrder.getUser_id());
                            if (optionalUser.isPresent()) {
                                wsSession.sendMessage(new TextMessage(optionalUser.get().getName().concat(", thank you for your order #").concat(updatedOrder.getId().toString()).concat(". Please consider new special offer for you.")));
                                wsMap.remove(updatedOrder.getId());
                                try {
                                    wsSession.close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        };
    }

    @Bean
    public Consumer<ErrorMessage> myErrorHandler() {
        return errorMessage -> {
            log.error("ERROR_HANDLING: {}", errorMessage);
        };
    }

}

