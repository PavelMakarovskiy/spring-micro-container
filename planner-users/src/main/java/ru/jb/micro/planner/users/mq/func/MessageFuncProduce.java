package ru.jb.micro.planner.users.mq.func;

// описываются все каналы с помощью функциональных методов

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.websocket.WebSocketHandler;

import java.util.function.Supplier;

@Configuration // spring reads beans and create channels
@Getter
public class MessageFuncProduce {

    Logger log = LoggerFactory.getLogger(MessageFuncProduce.class);

    // для того, чтобы считывать данные по требованию (а не постоянно) - создаем поток, откуда данные будут отправляться уже в канал SCS
    // будем исп внутреннюю шину, из которой будут отправляться сообщения в канал SCS (по требованию)
    private final Sinks.Many<Message<Long>> innerBusUserSide = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    private final Sinks.Many<Message<Order>> innerOrderBusUserSide = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    private final Sinks.Many<Message<Order>> wsOrderBusUserSide = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    // отправляет в канал id пользователя, для которого нужно создать тестовые данные
    // название метода должно совпадать с настройками definition и bindings в файлах properties (или yml)
    @Bean
    public Supplier<Flux<Message<Long>>> newUserActionProduce() {
        return () -> innerBusUserSide.asFlux(); // будет считывать данные из потока Flux (как только туда попадают новые сообщения)
    }

    @Bean
    public Supplier<Flux<Message<Order>>> orderUserSideProduce() {
        return () -> innerOrderBusUserSide.asFlux();
    }

    @Bean
    public Supplier<Flux<Message<Order>>> wsOrdersUserSideProduce() {
        return () -> {
            log.info("Called wsOrdersUserSideProduce.");
            return wsOrderBusUserSide.asFlux();
        };
    }
}
