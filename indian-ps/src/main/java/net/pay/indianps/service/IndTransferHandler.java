package net.pay.indianps.service;

import lombok.extern.slf4j.Slf4j;
import net.pay.indianps.mapper.IndAccountMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.ps.Account;
import ru.jb.micro.planner.entity.ps.Transfer;
import ru.jb.micro.planner.entity.ps.TransferStatus;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class IndTransferHandler {

    private final static String FEEDBACK_TOPIC = "feedback_topic";

    private final IndAccountMapper accountMapper;
    private KafkaTemplate<String, Transfer> kafkaTemplate;

    public IndTransferHandler(IndAccountMapper accountMapper, KafkaTemplate<String, Transfer> kafkaTemplate) {
        this.accountMapper = accountMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "work_topic")
    public void listenTransfers(Transfer transfer) {
        String msg = "";
        if (transfer != null && transfer.getId() != null) {
            Optional<Account> optionalRecipientAccount = accountMapper.getAccountById(transfer.getRecipient_id());
            if (optionalRecipientAccount.isPresent()) {
                Account recipientAccount = optionalRecipientAccount.get();
                if (Objects.equals(recipientAccount.getCurrency(), transfer.getCurrency())) {
                    long updateReserve = recipientAccount.getReserve() + transfer.getPayment();
                    accountMapper.updateReserve(recipientAccount.getId(), updateReserve);
                    transfer.setStatus(TransferStatus.APPROVED);
                    kafkaTemplate.send(FEEDBACK_TOPIC, transfer);
                } else {
                    msg = "Not appropriate currency.";
                    respondRejectStatusWithComment(transfer, msg);
                }
            } else {
                msg = "Recipient's account has not found.";
                respondRejectStatusWithComment(transfer, msg);
            }
        } else {
            msg = "No correct transfer entity";
            log.error(msg);
            // вернуть ошибку или в брокере не подтвердить доставку
        }
    }

    private void respondRejectStatusWithComment(Transfer transfer, String msg) {
        log.error(msg);
        transfer.setStatus(TransferStatus.REJECTED);
        transfer.setComment(msg);
        kafkaTemplate.send(FEEDBACK_TOPIC, transfer);
    }
}
