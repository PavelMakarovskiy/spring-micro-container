package ru.jb.micro.planner.users.order;

import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.category.Category;
import ru.jb.micro.planner.entity.order.Order;
import ru.jb.micro.planner.users.dto.OrderDTO;
import ru.jb.micro.planner.users.user.User;
import ru.jb.micro.planner.users.user.UserMapper;

import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserMapper userMapper;

    private final OrderMapper orderMapper;

    public OrderServiceImpl(UserMapper userMapper, OrderMapper orderMapper) {
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public Optional<Order> createOrder(OrderDTO orderDTO) {
        Long userId = addNewUser(orderDTO);
        Optional<User> user = userMapper.getUserById(userId);
        Long orderId = orderMapper.addOrder(user.get().getId());
        orderDTO.getCategories().stream()
                .map(val -> Category.valueOf(val))
                .forEach(cat -> orderMapper.addOrderCategories(orderId, cat));

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
