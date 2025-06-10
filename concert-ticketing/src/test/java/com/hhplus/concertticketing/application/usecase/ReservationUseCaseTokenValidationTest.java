package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.domain.model.Token;
import com.hhplus.concertticketing.domain.model.TokenStatus;
import com.hhplus.concertticketing.domain.service.ConcertService;
import com.hhplus.concertticketing.domain.service.ReservationService;
import com.hhplus.concertticketing.domain.service.TokenService;
import com.hhplus.concertticketing.Interfaces.presentation.dto.request.ReservationRequest;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationUseCaseTokenValidationTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private ConcertService concertService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ReservationUseCase reservationUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("토큰이 존재하지 않으면 UNAUTHORIZED 예외 발생")
    void reserveTicket_ShouldThrowException_WhenTokenMissing() {
        ReservationRequest request = new ReservationRequest();
        request.setTokenValue("invalid");
        request.setConcertOptionId(1L);
        request.setSeatId(1L);

        when(tokenService.getTokenByTokenValue("invalid")).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> reservationUseCase.reserveTicket(request));

        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
        verify(tokenService).getTokenByTokenValue("invalid");
        verifyNoInteractions(concertService, reservationService);
    }

    @Test
    @DisplayName("토큰 상태가 ACTIVE가 아니면 UNAUTHORIZED 예외 발생")
    void reserveTicket_ShouldThrowException_WhenTokenNotActive() {
        ReservationRequest request = new ReservationRequest();
        request.setTokenValue("token");
        request.setConcertOptionId(1L);
        request.setSeatId(1L);

        Token token = new Token();
        token.setTokenValue("token");
        token.setStatus(TokenStatus.WAITING);

        when(tokenService.getTokenByTokenValue("token")).thenReturn(token);

        CustomException exception = assertThrows(CustomException.class, () -> reservationUseCase.reserveTicket(request));

        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
        verify(tokenService).getTokenByTokenValue("token");
        verifyNoInteractions(concertService, reservationService);
    }
}
