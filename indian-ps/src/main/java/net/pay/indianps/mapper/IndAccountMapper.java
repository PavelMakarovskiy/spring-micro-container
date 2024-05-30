package net.pay.indianps.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import ru.jb.micro.planner.entity.ps.Account;

import java.util.Optional;

@Mapper
public interface IndAccountMapper {

    @Select("SELECT * FROM account WHERE id ILIKE #{recipient_id}")
    Optional<Account> getAccountById(@Param("recipient_id") String recipient_id);

    @Select("SELECT reserve FROM account WHERE id = #{id}")
    Optional<Long> getReserveById(@Param("id") String id);

    @Update("UPDATE account SET reserve = #{reserve} WHERE id = #{id}")
    void updateReserve(@Param("id") String id, @Param("reserve") Long reserve);

}
