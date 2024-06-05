package net.pay.russian_payment_system.controller;

import lombok.extern.slf4j.Slf4j;
import net.pay.russian_payment_system.exception.TransferHandleException;
import net.pay.russian_payment_system.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jb.micro.planner.entity.ps.Transfer;

@RestController
@Slf4j
@RequestMapping(path = "/rus/transfer")
public class TransferController {

    private final static String TOPIC_NAME = "work_topic";

    private KafkaTemplate<String, Transfer> kafkaTemplate;
    private final TransferService transferService;

    public TransferController(KafkaTemplate<String, Transfer> kafkaTemplate, TransferService transferService) {
        this.kafkaTemplate = kafkaTemplate;
        this.transferService = transferService;
    }

    @PostMapping("/{recipient_id}/{currency}/{amount}/{purpose}")
    public ResponseEntity<String> sendFunds(@PathVariable("recipient_id") String recipient_id, @PathVariable("currency") String currency,
                                            @PathVariable("amount") long amount, @PathVariable("purpose") String purpose) throws TransferHandleException {
        log.info("Request to send {} {} to account: {}", amount, currency, recipient_id);
        Transfer transfer = transferService.handleTransfer(recipient_id, currency, amount, purpose);
        kafkaTemplate.send(TOPIC_NAME, transfer);
        String response = String.format("Payment id: %s, with amount %d %s is %s. Recipient's account: %s",
                transfer.getId(), transfer.getPayment(), transfer.getCurrency(), transfer.getStatus(), transfer.getRecipient_id());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/check_status/{id}")
    public ResponseEntity<String> getTransferStatus(@PathVariable("id") String id) throws TransferHandleException {
        log.info("Request to get transfer status: {}", id);
        Transfer transfer = transferService.getTransfer(id);
        String response = String.format("Payment id: %s, with amount %d %s is %s. Recipient's account: %s",
                transfer.getId(), transfer.getPayment(), transfer.getCurrency(), transfer.getStatus(), transfer.getRecipient_id());
        return ResponseEntity.ok().body(response);
    }
}
