package ru.jb.micro.planner.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.jb.micro.planner.users.order.OrderService;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    private final OrderService orderService;

    public WebSocketHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        orderService.createWsFakeOrder(session);
        log.info(message.getPayload().toString());
        log.info("Requested ws fake order within session {}", session.getId());
    }
}
