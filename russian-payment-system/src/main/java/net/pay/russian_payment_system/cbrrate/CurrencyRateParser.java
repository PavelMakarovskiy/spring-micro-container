package net.pay.russian_payment_system.cbrrate;

import java.util.List;

public interface CurrencyRateParser {
    List<CurrencyRate> parseRate(String ratesAsString);
}
