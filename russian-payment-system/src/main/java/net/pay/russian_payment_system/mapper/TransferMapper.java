package net.pay.russian_payment_system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import ru.jb.micro.planner.entity.ps.Transfer;
import ru.jb.micro.planner.entity.ps.TransferStatus;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface TransferMapper {

    @Select("SELECT * FROM transfer WHERE id = '${id}'")
    Optional<Transfer> getTransferById(@Param("id") UUID id);

    @Select("INSERT INTO transfer (currency, payment, date, sender_id, recipient_id, purpose, status, comment) " +
            "VALUES (#{currencyCode}, #{payment}, #{date}, #{senderId}, #{recipientId}, #{purpose}, #{status}, #{comment}) returning id")
    String addTransfer(@Param("currencyCode") String currencyCode, @Param("payment") long payment, @Param("date") Timestamp date,
                       @Param("senderId") String senderId, @Param("recipientId") String recipientId, @Param("purpose") String purpose, @Param("status") TransferStatus status, @Param("comment") String comment);

    @Update("UPDATE transfer SET status = #{status}, comment = #{comment} WHERE id = '${id}'")
    void updateTransferStatus(@Param("id") UUID id, @Param("status") TransferStatus status, @Param("comment") String comment);

}
