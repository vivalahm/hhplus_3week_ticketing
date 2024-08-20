package com.hhplus.concertticketing.Interfaces;

import com.hhplus.concertticketing.Interfaces.presentation.dto.response.TokenStatusResponse;
import com.hhplus.concertticketing.application.usecase.TokenUseCase;
import com.hhplus.concertticketing.domain.model.TokenStatus;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TokenInterceptorTest {

    @Mock
    private TokenUseCase tokenUseCase;

    @InjectMocks
    private TokenInterceptor tokenInterceptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유효한 토큰인 경우 200 상태 코드 반환")
    void preHandle_ShouldReturnTrue_WhenTokenIsValid() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/some-endpoint");
        request.addHeader("Authorization", "validToken");

        when(tokenUseCase.getTokenStatus("validToken")).thenReturn(new TokenStatusResponse(TokenStatus.ACTIVE, 1L));

        // when
        boolean result = tokenInterceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result);
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("유효하지 않은 토큰인 경우 401 상태 코드 반환")
    void preHandle_ShouldReturnFalse_WhenTokenIsInvalid() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/some-endpoint");
        request.addHeader("Authorization", "invalidToken");

        // when & then
        CustomException thrownException = assertThrows(CustomException.class, () -> {
            tokenInterceptor.preHandle(request, response, new Object());
        });

        assertEquals(ErrorCode.UNAUTHORIZED, thrownException.getErrorCode());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    @DisplayName("토큰이 없는 경우 CustomException을 던지고 401 상태 코드 반환")
    void preHandle_ShouldThrowCustomException_WhenTokenIsMissing() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/some-endpoint");

        // when & then
        CustomException thrownException = assertThrows(CustomException.class, () -> {
            tokenInterceptor.preHandle(request, response, new Object());
        });

        assertEquals(ErrorCode.UNAUTHORIZED, thrownException.getErrorCode());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    @DisplayName("예외 경로인 경우 true 반환")
    void preHandle_ShouldReturnTrue_ForExcludedPaths() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/point/charge");

        // when
        boolean result = tokenInterceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result);
    }
}