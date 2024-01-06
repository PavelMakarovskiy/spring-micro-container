package ru.jb.micro.planner.users.order;

import org.apache.ibatis.annotations.*;
import ru.jb.micro.planner.entity.category.Category;
import ru.jb.micro.planner.entity.order.Order;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {

    @Select("SELECT * FROM ORDERS WHERE id = #{id}")
    Optional<Order> getOrderAndUserInfoById(@Param("id") Long id);

    @Select("select oc.category as category from orders o inner join orders_categories oc on o.id = oc.order_id where o.id = #{id}")
    List<Category> getCategoriesByOrderId(@Param("id") Long id);

    @Select("INSERT INTO orders (user_id) VALUES (#{user_id}) returning id")
    Long addOrder(@Param("user_id") Long user_id);

    @Insert("Insert into orders_categories (order_id, category) values (#{order_id}, #{category})")
    void addOrderCategories(@Param("order_id") Long order_id, @Param("category") Category category);
}
