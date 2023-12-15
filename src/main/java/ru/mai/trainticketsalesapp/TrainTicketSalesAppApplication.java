package ru.mai.trainticketsalesapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TrainTicketSalesAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainTicketSalesAppApplication.class, args);
    }

}
