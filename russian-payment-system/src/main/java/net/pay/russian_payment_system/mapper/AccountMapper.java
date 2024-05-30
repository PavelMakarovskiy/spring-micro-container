package net.pay.russian_payment_system.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import ru.jb.micro.planner.entity.ps.Account;

import java.util.Optional;

@Mapper
public interface AccountMapper {

    @Select("SELECT * FROM account WHERE country LIKE 'RUS' AND currency ILIKE #{currency} LIMIT 1")
    Optional<Account> getRusAccountByCurrency(@Param("currency") String currency);

    @Select("SELECT * FROM account WHERE id ILIKE #{recipient_id}")
    Optional<Account> getAccountById(@Param("recipient_id") String recipient_id);

    @Select("SELECT reserve FROM account WHERE id = #{id}")
    Optional<Long> getReserveById(@Param("id") String id);

    @Update("UPDATE account SET reserve = #{reserve} WHERE id = #{id}")
    void updateReserve(@Param("id") String id, @Param("reserve") Long reserve);

    @Insert("INSERT INTO account(id, currency, country, reserve) values (#{id}, #{currency}, #{country}, #{reserve})")
    void createAccount(@Param("id") String id, @Param("currency") String currency, @Param("country") String country, @Param("reserve") Long reserve);

}
