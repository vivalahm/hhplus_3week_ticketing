package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.domain.event.PaidEvent;
import com.hhplus.concertticketing.domain.model.*;
import com.hhplus.concertticketing.domain.service.*;
import com.hhplus.concertticketing.Interfaces.presentation.dto.request.PaymentRequest;
import com.hhplus.concertticketing.Interfaces.presentation.dto.response.PaymentResponse;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentUseCase {
    private final ReservationService reservationService;
    private final ConcertService concertService;
    private final CustomerService customerService;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentUseCase(ReservationService reservationService, ConcertService concertService, CustomerService customerService, ApplicationEventPublisher eventPublisher) {
        this.reservationService = reservationService;
        this.concertService = concertService;
        this.customerService = customerService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        // 예약을 찾아온다.
        Reservation reservation = reservationService.getReservationById(paymentRequest.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "예약이 존재하지 않습니다."));

        // 예약이 있는 경우 해당 콘서트에 대한 비용 정보를 가져오기 위해 concertOption을 가져온다.
        ConcertOption concertOption = concertService.getConcertOptionById(reservation.getConcertOptionId());

        // 예약 정보의 사용자 아이디와 콘서트 옵션의 가격을 통해 사용자의 포인트에서 차감한다.
        customerService.usePoint(reservation.getCustomerId(), concertOption.getPrice());

        // 예약의 상태를 결제됨으로 표시한다.
        reservation.setStatus(ReservationStatus.PAID);
        reservationService.updateReservationStatus(reservation);

        // 트랜잭션이 커밋된 후 Kafka로 메세지 전송
        eventPublisher.publishEvent(new PaidEvent(this, reservation, concertOption));

        return new PaymentResponse("SUCCESS", LocalDateTime.now());
    }
}