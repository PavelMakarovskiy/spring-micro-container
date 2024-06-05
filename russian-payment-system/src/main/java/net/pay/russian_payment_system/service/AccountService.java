package net.pay.russian_payment_system.service;

import ru.jb.micro.planner.entity.ps.Account;

import javax.money.CurrencyUnit;

public interface AccountService {

    boolean updateReserve(String id, Long reserve);

    Account createAccount(String country, String currency, long reserve);

    Account topUpAccount(String accountId, long amount, String currency);
}
