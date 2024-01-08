package ru.jb.micro.planner.todo.mq.func;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;
import ru.jb.micro.planner.entity.order.Order;

import java.util.function.Supplier;

@Configuration
@Getter
public class MessageToDoProduce {

    private Sinks.Many<Message<Order>> orderReadyBus = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    @Bean
    public Supplier<Flux<Message<Order>>> orderReadyProduce() {
        return () -> orderReadyBus.asFlux();
    }
}
