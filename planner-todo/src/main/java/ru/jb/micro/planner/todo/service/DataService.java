package ru.jb.micro.planner.todo.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import ru.jb.micro.planner.todo.mq.func.SubscriptionData;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
@Setter
public class DataService {

    String result;
    Map<UUID, SubscriptionData> subscriptions = new ConcurrentHashMap<>();


    public DataService() {
    }

    public void initData(Long userId) {
        setResult("we got user id: ".concat(userId.toString()));
        ServerSentEvent<String> event = ServerSentEvent
                .builder(getResult())
                .build();

        // 2
        subscriptions.forEach((uuid, subscriptionData) ->
                subscriptionData.getFluxSink().next(event)
        );
    }
}
