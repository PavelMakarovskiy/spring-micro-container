package net.pay.russian_payment_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RussianPaymentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RussianPaymentSystemApplication.class, args);
    }

}
