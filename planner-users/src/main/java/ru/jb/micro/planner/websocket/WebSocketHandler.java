package ru.jb.micro.planner.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.jb.micro.planner.users.mq.func.MessageFuncConsume;
import ru.jb.micro.planner.users.order.OrderService;

import java.io.IOException;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final OrderService orderService;

    private final MessageFuncConsume messageFuncConsume;

    public WebSocketHandler(OrderService orderService, MessageFuncConsume messageFuncConsume) {
        this.orderService = orderService;
        this.messageFuncConsume = messageFuncConsume;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        orderService.createWsFakeOrder(session);
        messageFuncConsume.wsOrderConsume();
    }
}
