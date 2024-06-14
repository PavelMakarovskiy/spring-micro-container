package net.pay.russian_payment_system.service;

import net.pay.russian_payment_system.exception.ReserveException;
import net.pay.russian_payment_system.exception.TransferHandleException;
import org.springframework.dao.CannotSerializeTransactionException;
import ru.jb.micro.planner.entity.ps.Transfer;

public interface TransferService {
    Transfer handleTransfer(String recipient_id, String currency, long amount, String purpose) throws TransferHandleException, ReserveException, CannotSerializeTransactionException;

    Transfer getTransfer(String transferId) throws TransferHandleException;
}
