package ru.jb.micro.planner.users.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.FluxSink;
import ru.jb.micro.planner.entity.order.Order;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionReadyOrder {
    private Map<Long, FluxSink<String>> mapFluxSinkReadyOrders;
}
