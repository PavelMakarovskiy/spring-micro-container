package ru.jb.micro.planner.users.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionReadyOrders {

    private FluxSink<ServerSentEvent> fluxSink;
}
