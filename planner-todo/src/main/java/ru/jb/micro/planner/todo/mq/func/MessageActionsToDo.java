package ru.jb.micro.planner.todo.mq.func;

import lombok.Getter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Sinks;
import ru.jb.micro.planner.entity.order.Order;

import java.util.Map;

@Service
@Getter
public class MessageActionsToDo {

    private final MessageToDoProduce messageToDoProduce;

    public MessageActionsToDo(MessageToDoProduce messageToDoProduce) {
        this.messageToDoProduce = messageToDoProduce;
    }

    public void sendReadyOrderToDoSide(Order order) {
        messageToDoProduce.getOrderReadyBusToDoSide().emitNext(MessageBuilder.withPayload(order).build(), Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public void sendReadyWsOrderToDoSide(Order order) {
        messageToDoProduce.getWsOrderBusToDoSide().emitNext(MessageBuilder.withPayload(order).build(), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
