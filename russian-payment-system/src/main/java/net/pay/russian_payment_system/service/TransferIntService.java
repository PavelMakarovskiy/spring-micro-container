package net.pay.russian_payment_system.service;

import lombok.extern.slf4j.Slf4j;
import net.pay.russian_payment_system.exception.AccountHandleException;
import net.pay.russian_payment_system.exception.ReserveException;
import net.pay.russian_payment_system.exception.TransferHandleException;
import net.pay.russian_payment_system.mapper.AccountMapper;
import net.pay.russian_payment_system.mapper.TransferMapper;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.jb.micro.planner.entity.ps.Account;
import ru.jb.micro.planner.entity.ps.Transfer;
import ru.jb.micro.planner.entity.ps.TransferStatus;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TransferIntService implements TransferService {

    private final AccountMapper accountMapper;
    private final TransferMapper transferMapper;
    private final AccountService accountService;

    public TransferIntService(AccountMapper accountMapper, TransferMapper transferMapper, AccountService accountService) {
        this.accountMapper = accountMapper;
        this.transferMapper = transferMapper;
        this.accountService = accountService;
    }

    @Retryable(value = {CannotSerializeTransactionException.class}, maxAttempts = 100, backoff = @Backoff(delay = 100))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public Transfer handleTransfer(String recipient_id, String currency, long amount, String purpose) throws TransferHandleException, ReserveException, CannotSerializeTransactionException {
        Optional<Account> optionalRecipientAccount = accountMapper.getAccountById(recipient_id);
        if (optionalRecipientAccount.isPresent()) {
            CurrencyUnit currencyUnit = Monetary.getCurrency(currency);
                Optional<Account> optionalSenderAccount = accountMapper.getRusAccountByCurrency(currency);
                if (optionalSenderAccount.isPresent()) {
                    Transfer transfer = createTransfer(optionalSenderAccount.get(), optionalRecipientAccount.get(), amount, purpose);
                    Long withdraw = withdrawMoney(optionalSenderAccount.get(), amount, transfer);
                    if (withdraw != null) {
                        UUID id = addTransfer(transfer);
                        Optional<Transfer> transferOptional = transferMapper.getTransferById(id);
                        if (transferOptional.isPresent()) {
                            return transferOptional.get();
                        } else {
                            String msg = "Error to save/find transfer with id: ".concat(String.valueOf(transfer.getId()))
                                    .concat("and payment: ")
                                    .concat(String.valueOf(transfer.getPayment()));
                            log.error(msg);
                            throw new TransferHandleException(msg);
                        }
                    } else {
                        String msg = "Withdraw is null for transfer id: ".concat(String.valueOf(transfer.getId())).concat(" is failed.");
                        log.error(msg);
                        try {
                            throw new TransferHandleException(msg);
                        } catch (TransferHandleException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    try {
                        String message = "No appropriate sender's account for currency: ";
                        log.info(message.concat("{}"), currency);
                        throw new AccountHandleException(message.concat(currency));
                    } catch (AccountHandleException e) {
                        throw new RuntimeException(e);
                    }
                }
        } else {
            try {
                String message = "No recipient's account with #";
                log.info(message.concat("{}"), recipient_id);
                throw new AccountHandleException(message.concat(recipient_id));
            } catch (AccountHandleException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Transfer getTransfer(String transferId) throws TransferHandleException, CannotSerializeTransactionException {
        Optional<Transfer> optionalTransfer = transferMapper.getTransferById(UUID.fromString(transferId));
        if (optionalTransfer.isPresent()) {
            return optionalTransfer.get();
        } else {
            String msg = "No transfer with id: ".concat(transferId);
            log.error(msg);
            throw new TransferHandleException(msg);
        }
    }

    private Transfer createTransfer(Account senderAccount, Account recipientAccount, long amount, String purpose) {
        Transfer transfer = new Transfer();
        transfer.setCurrency(senderAccount.getCurrency());
        transfer.setPayment(amount);
        transfer.setDate(LocalDateTime.now());
        transfer.setSender_id(senderAccount.getId());
        transfer.setRecipient_id(recipientAccount.getId());
        transfer.setPurpose(purpose);
        transfer.setStatus(TransferStatus.IN_PROGRESS);
        return transfer;
    }

    public Long withdrawMoney(Account account, long amount, Transfer transfer) throws ReserveException, CannotSerializeTransactionException {
        Long withdraw = null;
        UUID id;
        long reserve = accountService.getReserveById(account.getId());
        if (reserve >= amount) {
            boolean successReserveUpdate = accountService.takeFromReserve(account.getId(), amount);
            if (successReserveUpdate) {
                return amount;
            }
        } else {
            String message = "Not enough money to arrange payment with "
                    .concat(String.valueOf(amount))
                    .concat(" ")
                    .concat(transfer.getCurrency());
            log.error(message);
            transfer.setStatus(TransferStatus.CANCELLED);
            transfer.setComment(message);
            id = addTransfer(transfer);
            if (id != null && transferMapper.getTransferById(id).isPresent()) {
                log.info("Transfer with id: {} added in DB", id);
            } else {
                String messageTransfer = "Error to add in DB transfer for account/amount: ";
                log.error(messageTransfer.concat("{}/{} "), account.getId(), amount);
                try {
                    throw new TransferHandleException(messageTransfer.concat(account.getId())
                            .concat("/").concat(String.valueOf(amount)));
                } catch (TransferHandleException e) {
                    throw new RuntimeException(e);
                }
            }
            String message2 = " for transfer id: ".concat(id.toString());
            log.error(message.concat(message2));
            try {
                throw new ReserveException(message.concat(message2));
            } catch (ReserveException e) {
                throw new RuntimeException(e);
            }
        }
        return withdraw;
    }

    public UUID addTransfer(Transfer tr) {
        return UUID.fromString(transferMapper.addTransfer(tr.getCurrency(), tr.getPayment(), Timestamp.valueOf(tr.getDate()),
                tr.getSender_id(), tr.getRecipient_id(), tr.getPurpose(), tr.getStatus(), tr.getComment()));
    }
}
