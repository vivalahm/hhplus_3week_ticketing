package com.hhplus.concertticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ConcertTicketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConcertTicketingApplication.class, args);
    }

}
