package ru.jb.micro.planner.todo.controller;

import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.jb.micro.planner.todo.mq.func.SubscriptionData;
import ru.jb.micro.planner.todo.service.DataService;

import java.util.UUID;

@Getter
@RestController
@RequestMapping("/category")
public class ToDoController {

    private final DataService dataService;


    public ToDoController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/get")
    public ResponseEntity<String> getCategory() {
        return ResponseEntity.ok(dataService.getResult());
    }

    @GetMapping(path = "/stream-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> sse() {
        return Flux.create(fluxSink -> {
            UUID uuid = UUID.randomUUID();

            fluxSink.onCancel( // 4
                    () -> {
                        dataService.getSubscriptions().remove(uuid);
                    }

            );

            SubscriptionData subscriptionData = new SubscriptionData(fluxSink);
            dataService.getSubscriptions().put(uuid, subscriptionData);
        });
    }


}
