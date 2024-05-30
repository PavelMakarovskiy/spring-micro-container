package net.pay.russian_payment_system.controller;

import lombok.extern.slf4j.Slf4j;
import net.pay.russian_payment_system.exception.CurrencyNotFoundException;
import net.pay.russian_payment_system.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jb.micro.planner.entity.ps.Account;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.UnknownCurrencyException;

@RestController
@Slf4j
@RequestMapping(path = "/rus/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{country}/{currency}/{reserve}")
    public ResponseEntity<String> createAccount(@PathVariable("country") String country, @PathVariable("currency") String currency,
                                                @PathVariable("reserve") long reserve) throws CurrencyNotFoundException {
        String response = "";
        try {
            CurrencyUnit currencyUnit = Monetary.getCurrency(currency.toUpperCase());
            Account account = accountService.createAccount(country, currencyUnit.getCurrencyCode(), reserve);
            response = String.format("Successfully added account: %s, currency: %s, reserve: %d",
                    account.getId(), account.getCurrency(), account.getReserve());
        } catch (UnknownCurrencyException e) {
            log.error(e.getMessage());
            throw new CurrencyNotFoundException(e.getMessage());
        }
        return ResponseEntity.ok().body(response);
    }
}
