package ru.jb.micro.planner.entity.ps;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Transfer {
    private UUID id;
    private String currency;
    private long payment;
    private LocalDateTime date;
    private String sender_id;
    private String recipient_id;
    private String purpose;
    private TransferStatus status;
    private String comment;
}
