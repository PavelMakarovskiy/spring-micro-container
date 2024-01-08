package ru.jb.micro.planner.todo.mq.func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.todo.service.DataService;
import ru.jb.micro.planner.todo.service.OrderHandlerService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

// spring считывает бины и создает соотв. каналы
// описываются все каналы с помощью функциональных методов
// этот способ - рекомендуемый, вместо старого способа (@Binding, интерфейсы)
@Configuration
public class MessageFuncToDoConsume {

    private final DataService dataService;

    private final OrderHandlerService orderHandlerService;

    Logger log = LoggerFactory.getLogger(OrderHandlerService.class);
    static ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final MessageActionsToDo messageActionsToDo;

    public MessageFuncToDoConsume(DataService dataService, OrderHandlerService orderHandlerService, MessageActionsToDo messageActionsToDo) {
        this.dataService = dataService;
        this.orderHandlerService = orderHandlerService;
        this.messageActionsToDo = messageActionsToDo;
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
              //  orderHandlerService.executeOrder(message.getPayload());
                CompletableFuture<Order> completableFuture = CompletableFuture.supplyAsync(() ->
                {
                    Order order = message.getPayload();
                    int waitQty = order.getCategories().size();
                    try {
                        Thread.sleep(4000L * waitQty);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("Order № {} is in process", order.getId());
                    return order;
                }, executorService);
                try {
                    messageActionsToDo.sendReadyOrder(completableFuture.get());
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
