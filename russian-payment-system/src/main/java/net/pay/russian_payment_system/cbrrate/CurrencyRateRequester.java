package net.pay.russian_payment_system.cbrrate;

public interface CurrencyRateRequester {

    String getCurrencyRateAsXml(String url);
}
