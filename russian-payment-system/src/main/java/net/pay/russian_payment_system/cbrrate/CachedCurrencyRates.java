package net.pay.russian_payment_system.cbrrate;

import lombok.Value;

import java.util.List;

@Value
public class CachedCurrencyRates {
    List<CurrencyRate> currencyRates;
}

