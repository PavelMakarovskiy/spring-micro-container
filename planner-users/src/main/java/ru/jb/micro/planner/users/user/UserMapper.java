package ru.jb.micro.planner.users.user;

import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USERS WHERE id = #{id}")
    Optional<User> getUserById(@Param("id") Long id);

    @Select("INSERT INTO users (name) VALUES (#{name}) returning id")
    Long addUser(User user);
}
