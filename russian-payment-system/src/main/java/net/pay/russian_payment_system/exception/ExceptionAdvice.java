package net.pay.russian_payment_system.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.money.UnknownCurrencyException;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler({AccountHandleException.class, CurrencyNotFoundException.class,
            CurrencyRateNotFoundException.class, ReserveException.class, TransferHandleException.class, UnknownCurrencyException.class})
    public ResponseEntity<String> handleException(Exception ex) {
        String response = ex.getMessage();
        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler({CannotSerializeTransactionException.class})
    public ResponseEntity<String> handleTransactionException(Exception ex) {
        log.error("CannotSerializeTransactionException");
        String response = "Ошибка при совершении транзакции. Повторите попытку.";
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
