package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.service.ConcertOptionService;
import com.hhplus.concertticketing.business.service.SeatService;
import com.hhplus.concertticketing.business.service.TokenService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ConcertUseCase {
    private final ConcertOptionService concertOptionService;
    private final SeatService seatService;
    private final TokenService tokenService;

    public ConcertUseCase( ConcertOptionService concertOptionService, SeatService seatService, TokenService tokenService) {
        this.concertOptionService = concertOptionService;
        this.seatService = seatService;
        this.tokenService = tokenService;
    }

    public List<ConcertOption> getAvailableOptions(Long concertId, String tokenValue){
        tokenService.getTokenByTokenValue(tokenValue);
        return concertOptionService.getAvailableConcertOptions(concertId, LocalDateTime.now());
    }

    public List<Seat> getAvailableSeats(Long concertOptionId, String tokenValue){
        tokenService.getTokenByTokenValue(tokenValue);
        return seatService.getAvailableSeats(concertOptionId);
    }
}
