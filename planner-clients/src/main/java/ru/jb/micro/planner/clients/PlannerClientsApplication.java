package ru.jb.micro.planner.clients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"ru.jb.micro.planner"})
public class PlannerClientsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlannerClientsApplication.class, args);
    }

}
