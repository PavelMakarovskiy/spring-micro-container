package net.pay.russian_payment_system.service;

import lombok.extern.slf4j.Slf4j;
import net.pay.russian_payment_system.exception.AccountHandleException;
import net.pay.russian_payment_system.exception.ReserveException;
import net.pay.russian_payment_system.mapper.AccountMapper;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.jb.micro.planner.entity.ps.Account;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
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

    // @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public boolean takeFromReserve(String id, Long amount) throws CannotSerializeTransactionException {
        Optional<Account> optionalAccount = accountMapper.getAccountById(id);
        String message = "Error while updating reserve for account id: ".concat(id).concat(". ");
        String message2 = "Account with id: ".concat(id).concat(" is absent.");
        if (optionalAccount.isPresent()) {
            long exReserve = optionalAccount.get().getReserve();
       //     try {
                accountMapper.takeFromReserve(id, amount);
       //     } catch (CannotSerializeTransactionException e) {
              //  log.error("CannotSerializeTransactionException, repeat request.");
        //        return this.takeFromReserve(id, amount);
       //     }
            Optional<Long> optionalCheckReserve = accountMapper.getReserveById(id);
            if (optionalCheckReserve.isPresent()) {
                if (optionalCheckReserve.get() >= 0) {
                    log.info("Successfully updated reserve for account id: {}", id);
                    return true;
                } else {
//                    String message3 = "Required reserve is: ".concat(String.valueOf(exReserve - amount))
//                            .concat(", but current reserve is: ".concat(String.valueOf(optionalCheckReserve.get())));
                    String message3 = "Not enough reserve for account id: ".concat(id).concat(". ");
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
        CurrencyUnit currencyUnit = Monetary.getCurrency(currency);
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

    //  @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public Account topUpAccount(String accountId, long amount, String currency) throws AccountHandleException, CannotSerializeTransactionException {
        CurrencyUnit currencyUnit = Monetary.getCurrency(currency);
        Optional<Account> optionalAccount = accountMapper.getAccountById(accountId);
        String message = "Error while updating reserve for account id: ".concat(accountId).concat(". ");
        String message2 = "Account with id: ".concat(accountId).concat(" is absent.");
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (account.getCurrency().equals(currency)) {
                long exReserve = account.getReserve();
                accountMapper.topUpReserve(accountId, amount);
                Optional<Long> optionalReserve = accountMapper.getReserveById(accountId);
                if (optionalReserve.isPresent()) {
                    if (optionalReserve.get().equals(exReserve + amount)) {
                        log.info("Successfully top up account id: {}", accountId);
                        Optional<Account> result = accountMapper.getAccountById(accountId);
                        if (result.isPresent()) {
                            return result.get();
                        } else {
                            log.error(message.concat(message2));
                            try {
                                throw new AccountHandleException(message);
                            } catch (AccountHandleException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        String message3 = "Required reserve is: ".concat(String.valueOf(exReserve + amount))
                                .concat(", but current reserve is: ".concat(String.valueOf(optionalReserve.get())));
                        log.error(message.concat(message3));
                        try {
                            throw new ReserveException(message.concat(message3));
                        } catch (ReserveException e) {
                            throw new RuntimeException(e);
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
            } else {
                String msg = String.format("Not proper currency. Currency of amount is %s, but currency of account is %s", currency, account.getCurrency());
                log.error(msg);
                throw new AccountHandleException(msg);
            }
        } else {
            log.error(message.concat(message2));
            try {
                throw new AccountHandleException(message);
            } catch (AccountHandleException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Long getReserveById(String accountId) throws ReserveException {
        Optional<Long> optionalReserve = accountMapper.getReserveById(accountId);
        if (optionalReserve.isPresent()) {
            return optionalReserve.get();
        } else {
            throw new ReserveException("Reserve is absent");
        }
    }

    // @Transactional(isolation = Isolation.SERIALIZABLE)
    public String checkAndIncrementAccountNumber(String newId) throws CannotSerializeTransactionException {
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
