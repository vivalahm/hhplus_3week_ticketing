package com.hhplus.concertticketing.application.usecase.event;

import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.model.Reservation;
import org.springframework.context.ApplicationEvent;

public class PaidEvent extends ApplicationEvent {

    private final Reservation reservation;
    private final ConcertOption concertOption;

    public PaidEvent(Object source, Reservation reservation, ConcertOption concertOption) {
        super(source);
        this.reservation = reservation;
        this.concertOption = concertOption;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public ConcertOption getConcertOption() {
        return concertOption;
    }
}
