package ru.jb.micro.planner.todo.mq.func;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import ru.jb.micro.planner.todo.service.DataService;

import java.util.function.Consumer;

// spring считывает бины и создает соотв. каналы
// описываются все каналы с помощью функциональных методов
// этот способ - рекомендуемый, вместо старого способа (@Binding, интерфейсы)
@Configuration
public class MessageFunc {

    private final DataService dataService;

    public MessageFunc(DataService dataService) {
        this.dataService = dataService;
    }

    // название метода должно совпадать с настройками definition и bindings в файлах properties (или yml)
    @Bean
    public Consumer<Message<Long>> newUserActionConsume() {
        return message -> dataService.initData(message.getPayload());
    }

}
