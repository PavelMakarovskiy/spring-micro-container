package ru.jb.micro.planner.todo.mq.func;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;
import ru.jb.micro.planner.entity.order.Order;

import java.util.Map;
import java.util.function.Supplier;

@Configuration
@Getter
public class MessageToDoProduce {

    private final Sinks.Many<Message<Order>> orderReadyBusToDoSide = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    private final Sinks.Many<Message<Order>> wsOrderBusToDoSide = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    @Bean
    public Supplier<Flux<Message<Order>>> orderReadyProduceToDoSide() {
        return () -> orderReadyBusToDoSide.asFlux();
    }

    @Bean
    public Supplier<Flux<Message<Order>>> wsOrdersProduceToDoSide() {
        return () -> wsOrderBusToDoSide.asFlux();
    }
}
