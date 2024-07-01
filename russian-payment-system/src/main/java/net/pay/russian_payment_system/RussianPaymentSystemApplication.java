package net.pay.russian_payment_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry
public class RussianPaymentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RussianPaymentSystemApplication.class, args);
    }

}
