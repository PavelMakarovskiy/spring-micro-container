package net.pay.russian_payment_system.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pay.russian_payment_system.cbrrate.CurrencyRate;
import net.pay.russian_payment_system.service.CurrencyRateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Slf4j
@RequestMapping(path = "/api")
public class CurrencyRateController {

    private final static String TOPIC_NAME = "work_topic";

    private final CurrencyRateService currencyRateService;
    private KafkaTemplate<String, CurrencyRate> kafkaTemplate;

    public CurrencyRateController(CurrencyRateService currencyRateService, KafkaTemplate<String, CurrencyRate> kafkaTemplate) {
        this.currencyRateService = currencyRateService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/currency_rate/{currency}/{date}")
    public CurrencyRate getCurrencyRate(@PathVariable("currency") String currency,
                                        @DateTimeFormat(pattern = "dd-MM-yyyy") @PathVariable("date") LocalDate date) {
        log.info("getCurrencyRate, currency: {}, date:{}", currency, date);

        CurrencyRate rate = currencyRateService.getCurrencyRate(currency, date);
        log.info("rate:{}", rate);
        return rate;
    }

    @GetMapping("/kafka_rate/{currency}/{date}")
    public void getCurrencyKafkaRate(@PathVariable("currency") String currency,
                                     @DateTimeFormat(pattern = "dd-MM-yyyy") @PathVariable("date") LocalDate date) {
        log.info("getCurrencyRate, currency: {}, date:{}", currency, date);
        CurrencyRate rate = currencyRateService.getCurrencyRate(currency, date);
        log.info("rate:{}", rate);
        kafkaTemplate.send(TOPIC_NAME, rate);
    }
}
