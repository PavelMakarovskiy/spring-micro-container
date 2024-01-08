package ru.jb.micro.planner.todo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.jb.micro.planner.entity.order.Order;

import java.util.concurrent.Callable;

public class OrderHandlerThread implements Callable<Order> {

    Logger log = LoggerFactory.getLogger(OrderHandlerThread.class);

    private Order order;

    public OrderHandlerThread(Order order) {
        this.order = order;
    }

    @Override
    public Order call() throws Exception {
        int waitQty = order.getCategories().size();
        Thread.sleep(4000L * waitQty);
        log.info("Order № {} is in process", order.getId());
        return order;
    }
}
