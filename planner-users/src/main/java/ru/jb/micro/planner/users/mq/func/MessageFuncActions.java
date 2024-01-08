package ru.jb.micro.planner.users.mq.func;

import lombok.Getter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;
import ru.jb.micro.planner.entity.order.Order;

// работа с каналами
@Service
@Getter
// помогает реализовать отправку сообщения с помощью функц. кода - по требованию (только после вызова соотв. метода)
public class MessageFuncActions {

    // каналы для обмена сообщениями
    private MessageFuncProduce messageFunc;

    public MessageFuncActions(MessageFuncProduce messageFunc) {
        this.messageFunc = messageFunc;
    }

    // отправка сообщения
    public void sendNewUserMessage(Long id) {
        // добавляем в слушатель новое сообщение
        messageFunc.getInnerBus().emitNext(MessageBuilder.withPayload(id).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        System.out.println("Message sent: " + id);
    }

    public void sendNewOrder(Order order) {
        messageFunc.getInnerOrderBus().emitNext(MessageBuilder.withPayload(order).build(), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
