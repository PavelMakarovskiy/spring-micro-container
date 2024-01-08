package ru.jb.micro.planner.todo.mq.func;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.todo.service.DataService;
import ru.jb.micro.planner.todo.service.OrderHandlerService;

import java.util.function.Consumer;

// spring считывает бины и создает соотв. каналы
// описываются все каналы с помощью функциональных методов
// этот способ - рекомендуемый, вместо старого способа (@Binding, интерфейсы)
@Configuration
public class MessageFuncToDoConsume {

    private final DataService dataService;

    private final OrderHandlerService orderHandlerService;

    public MessageFuncToDoConsume(DataService dataService, OrderHandlerService orderHandlerService) {
        this.dataService = dataService;
        this.orderHandlerService = orderHandlerService;
    }

    // название метода должно совпадать с настройками definition и bindings в файлах properties (или yml)
    @Bean
    public Consumer<Message<Long>> newUserActionConsume() {
        return message -> dataService.initData(message.getPayload());
    }

    @Bean
    public Consumer<Message<Order>> ordersConsume() {
        return message -> {
            try {
                orderHandlerService.executeOrder(message.getPayload());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
