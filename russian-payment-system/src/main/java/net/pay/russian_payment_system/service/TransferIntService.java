package net.pay.russian_payment_system.service;

import lombok.extern.slf4j.Slf4j;
import net.pay.russian_payment_system.exception.CurrencyNotFoundException;
import net.pay.russian_payment_system.exception.ReserveException;
import net.pay.russian_payment_system.exception.TransferHandleException;
import net.pay.russian_payment_system.mapper.AccountMapper;
import net.pay.russian_payment_system.mapper.TransferMapper;
import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.ps.Account;
import ru.jb.micro.planner.entity.ps.Transfer;
import ru.jb.micro.planner.entity.ps.TransferStatus;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.security.auth.login.AccountNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Override
    public Transfer handleTransfer(String recipient_id, String currency, long amount, String purpose) throws TransferHandleException {
        Optional<Account> optionalRecipientAccount = accountMapper.getAccountById(recipient_id);
        if (optionalRecipientAccount.isPresent()) {
            CurrencyUnit currencyUnit = Monetary.getCurrency(currency);
            if (currencyUnit != null) {
                Optional<Account> optionalSenderAccount = accountMapper.getRusAccountByCurrency(currency);
                if (optionalSenderAccount.isPresent()) {
                    Transfer transfer = createTransfer(optionalSenderAccount.get(), optionalRecipientAccount.get(), amount, purpose);
                    Long withdraw = withdrawMoney(optionalSenderAccount.get(), amount, transfer);
                    if (withdraw != null) {
                        String id = addTransfer(transfer);
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
                        throw new AccountNotFoundException(message.concat(currency));
                    } catch (AccountNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                try {
                    String message = "No such currency: ";
                    log.info(message.concat("{}"), currency);
                    throw new CurrencyNotFoundException(message.concat(currency));
                } catch (CurrencyNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                String message = "No recipient's account with #";
                log.info(message.concat("{}"), recipient_id);
                throw new AccountNotFoundException(message.concat(currency));
            } catch (AccountNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Transfer createTransfer(Account senderAccount, Account recipientAccount, long amount, String purpose) {
        Transfer transfer = new Transfer();
        UUID uuid = UUID.randomUUID();
        while (isIdExist(uuid)) {
            uuid = UUID.randomUUID();
        }
        transfer.setId(uuid);
        transfer.setCurrency(senderAccount.getCurrency());
        transfer.setPayment(amount);
        transfer.setDate(LocalDateTime.now());
        transfer.setSender_id(senderAccount.getId());
        transfer.setRecipient_id(recipientAccount.getId());
        transfer.setPurpose(purpose);
        transfer.setStatus(TransferStatus.IN_PROGRESS);
        return transfer;
    }

    public boolean isIdExist(UUID uuid) {
        if (transferMapper.getTransferById(uuid.toString()).isPresent()) {
            return true;
        }
        return false;
    }

    public Long withdrawMoney(Account account, long amount, Transfer transfer) {
        long reserve = account.getReserve();
        Long withdraw = null;
        long updatedReserve = 0;
        if (reserve >= amount) {
            updatedReserve = reserve - amount;
            boolean successReserveUpdate = accountService.updateReserve(account.getId(), updatedReserve);
            if (successReserveUpdate) {
                withdraw = reserve - updatedReserve;
                return withdraw;
            }
        } else {
            String message = "Not enough money to withdraw";
            transfer.setStatus(TransferStatus.CANCELLED);
            transfer.setComment(message);
            String id = addTransfer(transfer);
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
            String message2 = " for transfer id: ".concat(transfer.getId().toString());
            log.error(message.concat(message2));
            try {
                throw new ReserveException(message.concat(message2));
            } catch (ReserveException e) {
                throw new RuntimeException(e);
            }
        }
        return withdraw;
    }

    public String addTransfer(Transfer tr) {
        return transferMapper.addTransfer(tr.getId().toString(), tr.getCurrency(), tr.getPayment(), Timestamp.valueOf(tr.getDate()),
                tr.getSender_id(), tr.getRecipient_id(), tr.getPurpose(), tr.getStatus(), tr.getComment());
    }
}
