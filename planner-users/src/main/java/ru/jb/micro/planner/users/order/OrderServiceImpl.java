package ru.jb.micro.planner.users.order;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import ru.jb.micro.planner.entity.category.Category;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.entity.order.OrderStatus;
import ru.jb.micro.planner.users.dto.OrderDTO;
import ru.jb.micro.planner.users.mq.func.MessageFuncActions;
import ru.jb.micro.planner.users.user.User;
import ru.jb.micro.planner.users.user.UserMapper;
import ru.jb.micro.planner.users.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Getter
@Setter
@Log
public class OrderServiceImpl implements OrderService {

    private final UserMapper userMapper;

    private final OrderMapper orderMapper;

    private final MessageFuncActions messageFuncActions;

    private final UserService userService;

    List<SubscriptionReadySSEOrder> subscriptionSSEReadyOrders = new ArrayList<>();

    List<SubscriptionReadyOrder> subscriptionReadyOrders = new ArrayList<>();

    Map<Long, WebSocketSession> wsMap = new HashMap<>();


    public OrderServiceImpl(UserMapper userMapper, OrderMapper orderMapper, MessageFuncActions messageFuncActions, UserService userService) {
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.messageFuncActions = messageFuncActions;
        this.userService = userService;
    }

    @Override
    public void createOrder(OrderDTO orderDTO) {
        Long userId = addNewUser(orderDTO);
        Optional<User> optionalUser = userMapper.getUserById(userId);
        if (optionalUser.isPresent()) {
            Long orderId = orderMapper.addOrder(optionalUser.get().getId(), OrderStatus.IN_PROGRESS);
            createOrderCategoriesRelation(orderDTO, orderId);
            Optional<Order> optionalOrder = getOrderByIdWithCategories(orderId);
            if (optionalOrder.isPresent()) {
                Order currentOrder = optionalOrder.get();
                log.info("Order " + currentOrder.getId() + " for " + "user " + optionalUser.get().getName() + " created.");
                messageFuncActions.sendNewOrderUserSide(currentOrder);
                log.info("Order " + currentOrder.getId() + " for " + "user " + optionalUser.get().getName() + " has sent for handling.");
            }
        }
    }

    @Override
    public List<SubscriptionReadySSEOrder> getSubscriptionSSEOrders() {
        return subscriptionSSEReadyOrders;
    }

    @Override
    public Long createFakeOrder() {
        Faker faker = new Faker();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUser_name(faker.name().fullName());
        orderDTO.setCategories(generateRandomCategories());
        Long userId = addNewUser(orderDTO);
        Long orderId = orderMapper.addOrder(userId, OrderStatus.IN_PROGRESS);
        createOrderCategoriesRelation(orderDTO, orderId);
        return orderId;
    }

    @Override
    public Flux<String> createPersonalFakeOrder() {
        Long orderId = createFakeOrder();
        return Flux.create(fluxSink -> {
            Optional<Order> optionalOrder = getOrderByIdWithCategories(orderId);
            if (optionalOrder.isPresent()) {
                Order currentOrder = optionalOrder.get();
                Optional<User> optionalUser = userMapper.getUserById(currentOrder.getUser_id());
                if (optionalUser.isPresent()) {
                    User currentUser = optionalUser.get();
                    log.info("Order " + currentOrder.getId() + " for " + "user " + currentUser.getName() + " created.");
                    Map<Long, FluxSink<String>> map = new HashMap<>();
                    map.put(orderId, fluxSink);
                    SubscriptionReadyOrder subscriptionReadyOrder = new SubscriptionReadyOrder(map);
                    subscriptionReadyOrders.add(subscriptionReadyOrder);
                    log.info("Subscription for order id #: " + orderId);
                    messageFuncActions.sendNewOrderUserSide(optionalOrder.get());
                    log.info("Order " + currentOrder.getId() + " for " + "user " + currentUser.getName() + " has sent for handling.");
                }
            }
        });
    }

    @Override
    public void createWsFakeOrder(WebSocketSession wsSession) {
        Long orderId = createFakeOrder();
        Optional<Order> optionalOrder = getOrderByIdWithCategories(orderId);
        if (optionalOrder.isPresent()) {
            Order currentOrder = optionalOrder.get();
            wsMap.put(currentOrder.getId(), wsSession);
            Optional<User> optionalUser = userMapper.getUserById(currentOrder.getUser_id());
            if (optionalUser.isPresent()) {
                User currentUser = optionalUser.get();
                log.info("Order " + currentOrder.getId() + " for " + "user " + currentUser.getName() + " created.");
                messageFuncActions.sendNewWsOrderUserSide(currentOrder);
            }
        }
    }

    private void createOrderCategoriesRelation(OrderDTO orderDTO, Long orderId) {
        orderDTO.getCategories().stream().map(val -> Category.valueOf(val)).forEach(cat -> orderMapper.addOrderCategories(orderId, cat));
    }

    private List<String> generateRandomCategories() {
        Set<String> categories = new HashSet<>();
        int catMaxIdx = Category.values().length - 1;
        int randomSum = (int) (Math.random() * (catMaxIdx - 1)) + 1;
        for (int i = 0; i <= randomSum; i++) {
            int randomValue = (int) (Math.random() * catMaxIdx);
            categories.add(Category.values()[randomValue].name());
        }
        return categories.stream().toList();
    }

    public void makeOrderResponse(Order readyOrder) {
        Optional<User> optionalUser = userService.getUserById(readyOrder.getUser_id());
        if (optionalUser.isPresent()) {
            String userName = optionalUser.get().getName();
            StringBuilder sb = new StringBuilder();
            readyOrder.getCategories().forEach(s -> sb.append(s).append(", "));
            String result = "Dear ".concat(userName).concat(", your order №")
                    .concat(String.valueOf(readyOrder.getId())).concat(" with categories: ")
                    .concat(sb.substring(0, sb.length() - 2)).concat(" is ready.");

            ServerSentEvent<String> event = ServerSentEvent.builder(result).build();

            subscriptionReadyOrders.forEach((subscription) -> {
                Map<Long, FluxSink<String>> map = subscription.getMapFluxSinkReadyOrders();
                if (map != null) {
                    FluxSink<String> fluxSink = map.get(readyOrder.getId());
                    if (fluxSink != null) {
                        fluxSink.next(result);
                    }
                }
            });

            log.info("Order " + readyOrder.getId() + " for " + "user " + userName + " is ready and sent as personal order.");
            subscriptionSSEReadyOrders.forEach((subscription) -> subscription.getFluxSinkSSEOrder().next(event));
            log.info("Order " + readyOrder.getId() + " for " + "user " + userName + " is ready and sent as event to subscription orders.");
        }
    }

    @NotNull
    public Optional<Order> getOrderByIdWithCategories(Long orderId) {
        Optional<Order> optionalOrder = orderMapper.getOrderAndUserAndStatusInfoById(orderId);
        optionalOrder.ifPresent(order -> order.setCategories(orderMapper.getCategoriesByOrderId(orderId)));
        return optionalOrder;
    }

    public Long addNewUser(OrderDTO orderDTO) {
        if (orderDTO.getUser_id() != null) {
            Optional<User> user = userMapper.getUserById(orderDTO.getUser_id());
            if (!user.isPresent()) {
                return userMapper.addUser(new User(orderDTO.getUser_name()));
            }
        } else {
            return userMapper.addUser(new User(orderDTO.getUser_name()));
        }
        return orderDTO.getUser_id();
    }

    @Override
    public void updateOrderStatus(Order order) {
        orderMapper.updateOrderStatus(order.getId(), order.getStatus());
    }

}
