package net.pay.russian_payment_system.service;

import lombok.extern.slf4j.Slf4j;
import net.pay.russian_payment_system.exception.AccountHandleException;
import net.pay.russian_payment_system.exception.ReserveException;
import net.pay.russian_payment_system.mapper.AccountMapper;
import org.springframework.stereotype.Service;
import ru.jb.micro.planner.entity.ps.Account;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
public class AccountIntService implements AccountService {

    private final AccountMapper accountMapper;

    public AccountIntService(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public boolean updateReserve(String id, Long reserve) {
        Optional<Account> optionalAccount = accountMapper.getAccountById(id);
        String message = "Error while updating reserve for account id: ".concat(id).concat(". ");
        String message2 = "Account with id: ".concat(id).concat(" is absent.");
        if (optionalAccount.isPresent()) {
            accountMapper.updateReserve(id, reserve);
            Optional<Long> optionalCheckReserve = accountMapper.getReserveById(id);
            if (optionalCheckReserve.isPresent()) {
                if (optionalCheckReserve.get().equals(reserve)) {
                    log.info("Successfully updated reserve for account id: {}", id);
                    return true;
                } else {
                    String message3 = "Required reserve is: ".concat(String.valueOf(reserve))
                            .concat(", but current reserve is: ".concat(String.valueOf(optionalCheckReserve.get())));
                    log.error(message.concat(message3));
                    try {
                        throw new ReserveException(message.concat(message3));
                    } catch (ReserveException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            log.error(message.concat(message2));
            try {
                throw new AccountHandleException(message);
            } catch (AccountHandleException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public Account createAccount(String country, String currency, long reserve) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String number = date.format(formatter);
        String newId = country.concat(number).concat(currency);
        String checkedId = checkAndIncrementAccountNumber(newId);
        accountMapper.createAccount(checkedId, currency, country, reserve);
        Optional<Account> optionalAccount = accountMapper.getAccountById(checkedId);
        if (optionalAccount.isPresent()) {
            log.info("Successfully created account id: {}", checkedId);
            return optionalAccount.get();
        } else {
            String msg = "Error while creating account with country/currency/amount: "
                    .concat(country).concat("/").concat(currency).concat("/").concat(String.valueOf(reserve));
            log.error(msg);
            try {
                throw new AccountHandleException(msg);
            } catch (AccountHandleException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String checkAndIncrementAccountNumber(String newId) {
        Optional<Account> optionalAccount = accountMapper.getAccountById(newId);
        if (optionalAccount.isPresent()) {
            int num = 1;
            String id = optionalAccount.get().getId();
            String body = id;
            if (id.contains("-")) {
                body = id.split("-")[0];
                int exNum = Integer.parseInt(id.split("-")[1]);
                num = exNum + 1;
            }
            newId = body.concat("-").concat(String.valueOf(num));
            return checkAndIncrementAccountNumber(newId);
        }
        return newId;
    }

}
