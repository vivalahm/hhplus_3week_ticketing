package com.hhplus.concertticketing.application.usecase;


import com.hhplus.concertticketing.application.usecase.event.PaidEvent;
import com.hhplus.concertticketing.business.model.*;
import com.hhplus.concertticketing.business.service.*;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.PaymentRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.PaymentResponse;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentUseCaseEventPublishTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private ConcertService concertService;

    @Mock
    private CustomerService customerService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentUseCase paymentUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processPayment_success() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setReservationId(1L);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setConcertOptionId(2L);
        reservation.setCustomerId(3L);
        reservation.setSeatId(4L);

        ConcertOption concertOption = new ConcertOption();
        concertOption.setId(2L);
        concertOption.setPrice(100.0);
        concertOption.setConcertId(5L);

        Customer customer = new Customer();
        customer.setId(3L);
        customer.setPoint(500.0);

        when(reservationService.getReservationById(1L)).thenReturn(Optional.of(reservation));
        when(concertService.getConcertOptionById(2L)).thenReturn(concertOption);
        when(customerService.usePoint(3L, 100.0)).thenReturn(customer); // 수정된 부분
        doNothing().when(reservationService).updateReservationStatus(any());
        doNothing().when(concertService).reserveSeat(4L);

        PaymentResponse response = paymentUseCase.processPayment(paymentRequest);

        assertEquals("SUCCESS", response.getStatus());

        verify(reservationService).getReservationById(1L);
        verify(concertService).getConcertOptionById(2L);
        verify(customerService).usePoint(3L, 100.0);
        verify(reservationService).updateReservationStatus(reservation);
        verify(concertService).reserveSeat(4L);

        ArgumentCaptor<PaidEvent> eventCaptor = ArgumentCaptor.forClass(PaidEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        PaidEvent event = eventCaptor.getValue();
        assertEquals(reservation, event.getReservation());
        assertEquals(concertOption, event.getConcertOption());
    }

    @Test
    void processPayment_reservationNotFound() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setReservationId(1L);

        when(reservationService.getReservationById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> paymentUseCase.processPayment(paymentRequest));

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("예약이 존재하지 않습니다.", exception.getMessage());

        verify(reservationService).getReservationById(1L);
        verifyNoMoreInteractions(reservationService, concertService, customerService, eventPublisher);
    }
}