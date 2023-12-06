package ru.jb.micro.plannerentity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PlannerEntityApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlannerEntityApplication.class, args);
    }

}
