package ru.jb.micro.planner.todo.mq.func;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionData {

    private FluxSink<ServerSentEvent> fluxSink;

}
