package ru.jb.micro.planner.users.order;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.category.Category;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.users.dto.OrderDTO;
import ru.jb.micro.planner.users.mq.func.MessageFuncActions;
import ru.jb.micro.planner.users.user.User;
import ru.jb.micro.planner.users.user.UserMapper;
import ru.jb.micro.planner.users.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Getter
@Setter
public class OrderServiceImpl implements OrderService {

    private final UserMapper userMapper;

    private final OrderMapper orderMapper;

    private final MessageFuncActions messageFuncActions;

    private final UserService userService;

    List<SubscriptionReadyOrders> subscriptionOrders = new ArrayList<>();


    public OrderServiceImpl(UserMapper userMapper, OrderMapper orderMapper, MessageFuncActions messageFuncActions, UserService userService) {
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.messageFuncActions = messageFuncActions;
        this.userService = userService;
    }

    @Override
    public void createOrder(OrderDTO orderDTO) {
        Long userId = addNewUser(orderDTO);
        Optional<User> user = userMapper.getUserById(userId);
        Long orderId = orderMapper.addOrder(user.get().getId());
        orderDTO.getCategories().stream()
                .map(val -> Category.valueOf(val))
                .forEach(cat -> orderMapper.addOrderCategories(orderId, cat));
        if (getOrderById(orderId).isPresent()) {
            messageFuncActions.sendNewOrder(getOrderById(orderId).get());
        }
    }

    @Override
    public List<SubscriptionReadyOrders> getSubscriptionOrders() {
        return subscriptionOrders;
    }


    public void makeOrderResponse(Order readyOrder) {
        String userName = userService.getUserById(readyOrder.getUser_id()).get().getName();
        StringBuilder sb = new StringBuilder();
        readyOrder.getCategories().forEach(s -> sb.append(s).append(", "));
        String result = "Dear ".concat(userName).concat(" your order №")
                .concat(String.valueOf(readyOrder.getId()))
                .concat(" with categories: ")
                .concat(sb.substring(0, sb.length() - 2))
                .concat(" is ready.");
        ServerSentEvent<String> event = ServerSentEvent.builder(result)
                .build();

        subscriptionOrders.forEach((subscription) ->
                subscription.getFluxSink().next(event));
    }

    @NotNull
    private Optional<Order> getOrderById(Long orderId) {
        Optional<Order> order = orderMapper.getOrderAndUserInfoById(orderId);
        order.get().setCategories(orderMapper.getCategoriesByOrderId(orderId));
        return order;
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
}
