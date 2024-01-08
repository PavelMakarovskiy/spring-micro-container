package ru.jb.micro.planner.todo.mq.func;

import lombok.Getter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;
import ru.jb.micro.planner.entity.order.Order;

@Service
@Getter
public class MessageActionsToDo {

    private MessageToDoProduce messageToDoProduce;

    public MessageActionsToDo(MessageToDoProduce messageToDoProduce) {
        this.messageToDoProduce = messageToDoProduce;
    }

    public void sendReadyOrder(Order order) {
        messageToDoProduce.getOrderReadyBus().emitNext(MessageBuilder.withPayload(order).build(), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
