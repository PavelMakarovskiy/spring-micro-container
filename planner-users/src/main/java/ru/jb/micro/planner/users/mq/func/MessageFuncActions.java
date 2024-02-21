package ru.jb.micro.planner.users.mq.func;

import lombok.Getter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Sinks;
import ru.jb.micro.planner.entity.order.Order;

import java.util.Map;

// работа с каналами
@Service
@Getter
// помогает реализовать отправку сообщения с помощью функц. кода - по требованию (только после вызова соотв. метода)
public class MessageFuncActions {

    // каналы для обмена сообщениями
    private final MessageFuncProduce messageFuncProduce;

    public MessageFuncActions(MessageFuncProduce messageFunc) {
        this.messageFuncProduce = messageFunc;
    }

    // отправка сообщения
    public void sendNewUserMessage(Long id) {
        // добавляем в слушатель новое сообщение
        messageFuncProduce.getInnerBus().emitNext(MessageBuilder.withPayload(id).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        System.out.println("Message sent: " + id);
    }

    public void sendNewOrder(Order order) {
        messageFuncProduce.getInnerOrderBus().emitNext(MessageBuilder.withPayload(order).build(), Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public void sendNewWsOrder(Map<Order, WebSocketSession> map) {
        messageFuncProduce.getWsOrderBus().emitNext(MessageBuilder.withPayload(map).build(), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
