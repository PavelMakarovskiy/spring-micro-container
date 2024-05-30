package net.pay.russian_payment_system.exception;

public class CurrencyNotFoundException extends Exception {

    public CurrencyNotFoundException(String message) {
        super(message);
    }
}
