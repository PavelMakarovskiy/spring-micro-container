package net.pay.russian_payment_system.service;

import lombok.extern.slf4j.Slf4j;
import net.pay.russian_payment_system.mapper.TransferMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.ps.Transfer;
import ru.jb.micro.planner.entity.ps.TransferStatus;

@Service
@Slf4j
public class FeedbackTransferHandler {

    private final TransferMapper transferMapper;

    public FeedbackTransferHandler(TransferMapper transferMapper) {
        this.transferMapper = transferMapper;
    }

    @KafkaListener(topics = "feedback_topic")
    public void listenFeedback(Transfer transfer) {
        String msg = "";
        if (transfer != null && transfer.getId() != null) {
            transferMapper.updateTransferStatus(transfer.getId(), transfer.getStatus(), transfer.getComment());
            if (transfer.getStatus().equals(TransferStatus.APPROVED)) {
                msg = "Successfully approved payment with id: ".concat(transfer.getId().toString());
                log.info(msg);
            } else {
                msg = "Payment with id: ".concat(transfer.getId().toString())
                        .concat(" is ").concat(transfer.getStatus().toString());
                log.warn(msg);
            }
        } else {
            msg = "No correct transfer entity";
            log.error(msg);
        }
    }
}
