package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.*;
import com.hhplus.concertticketing.business.service.*;
import com.hhplus.concertticketing.presentation.dto.request.PaymentRequest;
import com.hhplus.concertticketing.presentation.dto.response.PaymentResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PaymentUseCase {
    private final ReservationService reservationService;
    private final SeatService seatService;
    private final TokenService tokenService;
    private final CustomerService customerService;
    private final ConcertOptionService concertOptionService;

    public PaymentUseCase(ReservationService reservationService, SeatService seatService, TokenService tokenService, CustomerService customerService, ReservationService reservationService1, SeatService seatService1, TokenService tokenService1, CustomerService customerService1, ConcertOptionService concertOptionService) {
        this.reservationService = reservationService1;
        this.seatService = seatService1;
        this.tokenService = tokenService1;
        this.customerService = customerService1;
        this.concertOptionService = concertOptionService;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        //예약을 찾아온다.
        Reservation reservation = reservationService.getReservationById(paymentRequest.getReservationId()).orElseThrow(()->new IllegalArgumentException("예약이 존재하지 않습니다."));
        //예약이 있는 경우 해당 콘서트에 대한 비용 정보를 가져오기 위해 concertOption을 가져온다.
        ConcertOption concertOption = concertOptionService.getConcertOptionById(reservation.getConcertOptionId());

        //예약 정보의 사용자 아이디와 콘서트 옵션의 가격을 통해 사용자의 포인트에서 차감한다.
        customerService.usePoint(reservation.getCustomerId(), concertOption.getPrice());

        //예약의 상태를 결제됨으로 표시한다.
        reservation.setStatus(ReservationStatus.PAID);
        reservationService.updateReservationStatus(reservation);

        seatService.reserveSeat(reservation.getSeatId());
        Token token = tokenService.getTokenByConcertIdAndCustomerId(concertOption.getConcertId(), reservation.getCustomerId());

        token.setStatus(TokenStatus.EXPIRED);
        tokenService.updateToken(token);

        return new PaymentResponse("SUCCESS", LocalDateTime.now());
    }
}
