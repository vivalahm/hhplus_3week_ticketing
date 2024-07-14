package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.Reservation;
import com.hhplus.concertticketing.business.model.ReservationStatus;
import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.service.ConcertService;
import com.hhplus.concertticketing.business.service.ReservationService;
import com.hhplus.concertticketing.business.service.TokenService;
import com.hhplus.concertticketing.presentation.dto.request.ReservationRequest;
import com.hhplus.concertticketing.presentation.dto.response.ReservationResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationUseCase {
    private final ReservationService reservationService;
    private final ConcertService concertService;
    private final TokenService tokenService;

    public ReservationUseCase(ReservationService reservationService, ConcertService concertService, TokenService tokenService){
        this.reservationService = reservationService;
        this.concertService = concertService;
        this.tokenService = tokenService;
    }

    @Transactional
    public void checkAndUpdateExpiredReservations(){
        List<Reservation> expiredReservations = reservationService.getExpiredReservations(LocalDateTime.now());
        for(Reservation reservation : expiredReservations){
            reservation.setStatus(ReservationStatus.CANCLED);
            reservationService.updateReservationStatus(reservation);
            concertService.unlockSeat(reservation.getSeatId());
        }
    }

    @Transactional
    public ReservationResponse reserveTicket(ReservationRequest reservationRequest){

        Token token = tokenService.getTokenByTokenValue(reservationRequest.getTokenValue());

        concertService.lockSeat(reservationRequest.getConcertOptionId(),reservationRequest.getSeatId());

        Reservation reservation = reservationService.reserveTicket(token.getCustomerId(),reservationRequest.getConcertOptionId(),reservationRequest.getSeatId());

        return new ReservationResponse(reservation.getId(), reservation.getStatus(),reservation.getExpiresAt());
    }
}
