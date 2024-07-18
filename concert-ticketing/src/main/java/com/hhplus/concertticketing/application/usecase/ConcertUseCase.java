package com.hhplus.concertticketing.application.usecase;

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
    private final TokenService tokenService;

    public ConcertUseCase(ConcertService concertService, TokenService tokenService) {
        this.concertService = concertService;
        this.tokenService = tokenService;
    }

    public List<ConcertOption> getAvailableOptions(Long concertId){
        return concertService.getAvailableConcertOptions(concertId, LocalDateTime.now());
    }

    public List<Seat> getAvailableSeats(Long concertOptionId){
        return concertService.getAvailableSeats(concertOptionId);
    }
}
