package net.pay.russian_payment_system.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pay.russian_payment_system.cbrrate.CurrencyRate;
import net.pay.russian_payment_system.service.CurrencyRateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api")
public class CurrencyRateController {

    public final CurrencyRateService currencyRateService;

    @GetMapping("/currency_rate/{currency}/{date}")
    public CurrencyRate getCurrencyRate(@PathVariable("currency") String currency,
                                        @DateTimeFormat(pattern = "dd-MM-yyyy") @PathVariable("date") LocalDate date) {
        log.info("getCurrencyRate, currency: {}, date:{}", currency, date);

        CurrencyRate rate = currencyRateService.getCurrencyRate(currency, date);
        log.info("rate:{}", rate);
        return rate;
    }
}
