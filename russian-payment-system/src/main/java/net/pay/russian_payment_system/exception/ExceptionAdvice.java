package net.pay.russian_payment_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.money.UnknownCurrencyException;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler({AccountHandleException.class, CurrencyNotFoundException.class,
            CurrencyRateNotFoundException.class, ReserveException.class, TransferHandleException.class, UnknownCurrencyException.class})
    public ResponseEntity<String> handleException(Exception ex) {
        String response = ex.getMessage();
        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }
}
