package ru.jb.micro.planner.users.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USERS WHERE user_id = #{id}")
    Optional<User> getUserById(@Param("id") Long id);
}
