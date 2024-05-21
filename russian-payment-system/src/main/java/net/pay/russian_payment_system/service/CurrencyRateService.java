package net.pay.russian_payment_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pay.russian_payment_system.cbrrate.CachedCurrencyRates;
import net.pay.russian_payment_system.cbrrate.CurrencyRate;
import net.pay.russian_payment_system.cbrrate.CurrencyRateParser;
import net.pay.russian_payment_system.cbrrate.CurrencyRateRequester;
import net.pay.russian_payment_system.config.CbrConfig;
import net.pay.russian_payment_system.exception.CurrencyRateNotFoundException;
import org.ehcache.Cache;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyRateService {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final CurrencyRateRequester currencyRateRequester;
    private final CurrencyRateParser currencyRateParser;
    private final Cache<LocalDate, CachedCurrencyRates> currencyRateCache;
    private final CbrConfig cbrConfig;

    public CurrencyRate getCurrencyRate(String currency, LocalDate date) {
        log.info("Get currency rate for {} on date {}", currency, date);
        List<CurrencyRate> rates;
        CachedCurrencyRates cachedCurrencyRates = currencyRateCache.get(date);
        if (cachedCurrencyRates == null) {
            String urlWithParams = String.format("%s?date_req=%s", cbrConfig.getUrl(), DATE_FORMATTER.format(date));
            String ratesAsXml = currencyRateRequester.getCurrencyRateAsXml(urlWithParams);
            rates = currencyRateParser.parseRate(ratesAsXml);
            currencyRateCache.put(date, new CachedCurrencyRates(rates));
        } else {
            rates = cachedCurrencyRates.getCurrencyRates();
        }

        try {
            return rates.stream().filter(rate -> currency.equals(rate.getCharCode()))
                    .findFirst()
                    .orElseThrow(() -> new CurrencyRateNotFoundException("Currency rate is not found."));
        } catch (CurrencyRateNotFoundException e) {
            log.error("Currency rate is not found. Currency: {}, date: {}", currency, date);
            throw new RuntimeException(e);
        }
    }
}
