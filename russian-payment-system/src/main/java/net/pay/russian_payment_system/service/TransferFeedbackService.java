package net.pay.russian_payment_system.service;

import net.pay.russian_payment_system.mapper.TransferMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.ps.Transfer;

import java.util.Optional;

@Service
public class TransferFeedbackService {

    private final TransferMapper transferMapper;

    public TransferFeedbackService(TransferMapper transferMapper) {
        this.transferMapper = transferMapper;
    }

    @KafkaListener(topics = "feedback_topic")
    public void listenTransferFeedback(Transfer transfer) {
        String msg = "";
        if (transfer != null && transfer.getId() != null) {
            Optional<Transfer> optionalTransfer = transferMapper.getTransferById(transfer.getId().toString());
            if (optionalTransfer.isPresent()) {
                transferMapper.updateTransferStatus(transfer.getId().toString(), transfer.getStatus(), transfer.getComment());
            }
        }
    }
}
