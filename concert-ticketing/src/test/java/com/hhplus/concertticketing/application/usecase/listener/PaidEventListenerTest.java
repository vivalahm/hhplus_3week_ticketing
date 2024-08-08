package com.hhplus.concertticketing.application.usecase.listener;

import com.hhplus.concertticketing.application.usecase.event.PaidEvent;
import com.hhplus.concertticketing.application.usecase.event.TokenExpireEvent;
import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.model.Reservation;
import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.service.TokenService;
import com.hhplus.concertticketing.infrastructure.persistance.DataPlatformMockApiClient;
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

class PaidEventListenerTest {

    @Mock
    private DataPlatformMockApiClient dataPlatformMockApiClient;

    @Mock
    private TokenService tokenService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaidEventListener paidEventListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handlePaidEvent_success() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCustomerId(2L);

        ConcertOption concertOption = new ConcertOption();
        concertOption.setId(3L);
        concertOption.setConcertId(4L);

        Token token = new Token();
        token.setTokenValue("token_value");

        PaidEvent paidEvent = new PaidEvent(this, reservation, concertOption);

        when(tokenService.getTokenByConcertIdAndCustomerId(4L, 2L)).thenReturn(Optional.of(token));

        paidEventListener.handlePaidEvent(paidEvent);

        verify(dataPlatformMockApiClient).sendReservationInfo(reservation);

        ArgumentCaptor<TokenExpireEvent> eventCaptor = ArgumentCaptor.forClass(TokenExpireEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        TokenExpireEvent capturedEvent = eventCaptor.getValue();

        assertNotNull(capturedEvent);
        assertEquals(token, capturedEvent.getToken());
    }

    @Test
    void handlePaidEvent_tokenNotFound() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCustomerId(2L);

        ConcertOption concertOption = new ConcertOption();
        concertOption.setId(3L);
        concertOption.setConcertId(4L);

        PaidEvent paidEvent = new PaidEvent(this, reservation, concertOption);

        when(tokenService.getTokenByConcertIdAndCustomerId(4L, 2L)).thenReturn(Optional.empty());

        paidEventListener.handlePaidEvent(paidEvent);

        verify(dataPlatformMockApiClient).sendReservationInfo(reservation);
        verify(eventPublisher, never()).publishEvent(any(TokenExpireEvent.class));
    }
}