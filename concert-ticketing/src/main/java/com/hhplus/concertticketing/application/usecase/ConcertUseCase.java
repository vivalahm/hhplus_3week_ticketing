package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.Concert;
import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.service.ConcertService;
import com.hhplus.concertticketing.business.service.TokenService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ConcertUseCase {
    private final ConcertService concertService;

    public ConcertUseCase(ConcertService concertService) {
        this.concertService = concertService;
    }

    public List<Concert> getAvailableConcerts() {
        return concertService.getAvailableConcerts();
    }

    public Concert getConcertInfo(Long concertId){
        return concertService.getConcertInfo(concertId);
    }

    public List<ConcertOption> getAvailableOptions(Long concertId){
        return concertService.getAvailableConcertOptions(concertId, LocalDateTime.now());
    }

    public List<Seat> getAvailableSeats(Long concertOptionId){
        return concertService.getAvailableSeats(concertOptionId);
    }
}
