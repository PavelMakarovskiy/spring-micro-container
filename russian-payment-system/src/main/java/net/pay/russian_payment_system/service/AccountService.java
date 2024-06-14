package net.pay.russian_payment_system.service;

import net.pay.russian_payment_system.exception.AccountHandleException;
import net.pay.russian_payment_system.exception.ReserveException;
import ru.jb.micro.planner.entity.ps.Account;

import javax.money.CurrencyUnit;

public interface AccountService {

    boolean takeFromReserve(String id, Long amount);

    Account createAccount(String country, String currency, long reserve);

    Account topUpAccount(String accountId, long amount, String currency) throws AccountHandleException;

    Long getReserveById(String accountId) throws ReserveException;
}
