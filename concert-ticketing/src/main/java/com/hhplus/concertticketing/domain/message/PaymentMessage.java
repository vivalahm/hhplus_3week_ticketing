package com.hhplus.concertticketing.domain.message;

import com.hhplus.concertticketing.domain.event.PaidEvent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentMessage {

    private String reservationId;
    private String customerId;
    private String concertOptionId;
    private double price;
    private LocalDateTime eventTime;

    public PaymentMessage(PaidEvent event) {
        this.reservationId = event.getReservation().getId().toString();
        this.customerId = event.getReservation().getCustomerId().toString();
        this.concertOptionId = event.getConcertOption().getId().toString();
        this.price = event.getConcertOption().getPrice();
        this.eventTime = LocalDateTime.now();
    }
}