package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.model.TokenStatus;
import com.hhplus.concertticketing.business.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("활성 토큰이 최대 개수보다 적을 때 ACTIVE 상태의 토큰 발행 테스트")
    void issueToken_ShouldReturnActiveToken_WhenActiveTokensLessThanMax() {
        Long customerId = 1L;
        Long concertId = 1L;
        int maxActiveTokens = 5;
        when(tokenRepository.getCountActiveTokens(concertId)).thenReturn(4L);
        when(tokenRepository.getExistWaitingTokens(concertId)).thenReturn(false);

        Token token = new Token();
        token.setCustomerId(customerId);
        token.setConcertId(concertId);
        token.setTokenValue(UUID.randomUUID().toString());
        token.setStatus(TokenStatus.ACTIVE);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.saveToken(any(Token.class))).thenReturn(token);

        Token issuedToken = tokenService.issueToken(customerId, concertId, maxActiveTokens);

        assertNotNull(issuedToken);
        assertEquals(TokenStatus.ACTIVE, issuedToken.getStatus());
        verify(tokenRepository, times(1)).saveToken(any(Token.class));
    }

    @Test
    @DisplayName("활성 토큰이 최대 개수 이상일 때 WAITING 상태의 토큰 발행 테스트")
    void issueToken_ShouldReturnWaitingToken_WhenActiveTokensGreaterThanOrEqualToMax() {
        Long customerId = 1L;
        Long concertId = 1L;
        int maxActiveTokens = 5;
        when(tokenRepository.getCountActiveTokens(concertId)).thenReturn(5L);
        when(tokenRepository.getExistWaitingTokens(concertId)).thenReturn(true);

        Token token = new Token();
        token.setCustomerId(customerId);
        token.setConcertId(concertId);
        token.setTokenValue(UUID.randomUUID().toString());
        token.setStatus(TokenStatus.WAITING);

        when(tokenRepository.saveToken(any(Token.class))).thenReturn(token);

        Token issuedToken = tokenService.issueToken(customerId, concertId, maxActiveTokens);

        assertNotNull(issuedToken);
        assertEquals(TokenStatus.WAITING, issuedToken.getStatus());
        verify(tokenRepository, times(1)).saveToken(any(Token.class));
    }

    @Test
    @DisplayName("토큰 값으로 조회하여 토큰이 존재할 때 올바른 토큰 객체 반환 테스트")
    void getTokenByTokenValue_ShouldReturnToken_WhenTokenExists() {
        String tokenValue = UUID.randomUUID().toString();
        Token token = new Token();
        token.setTokenValue(tokenValue);

        when(tokenRepository.getTokenByTokenValue(tokenValue)).thenReturn(Optional.of(token));

        Token foundToken = tokenService.getTokenByTokenValue(tokenValue);

        assertNotNull(foundToken);
        assertEquals(tokenValue, foundToken.getTokenValue());
        verify(tokenRepository, times(1)).getTokenByTokenValue(tokenValue);
    }

    @Test
    @DisplayName("토큰 값으로 조회할 때 토큰이 존재하지 않으면 예외 발생 테스트")
    void getTokenByTokenValue_ShouldThrowException_WhenTokenDoesNotExist() {
        String tokenValue = UUID.randomUUID().toString();
        when(tokenRepository.getTokenByTokenValue(tokenValue)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            tokenService.getTokenByTokenValue(tokenValue);
        });

        assertEquals("Invalid token value", exception.getMessage());
        verify(tokenRepository, times(1)).getTokenByTokenValue(tokenValue);
    }

    @Test
    @DisplayName("콘서트 ID와 고객 ID로 조회하여 토큰이 존재할 때 올바른 토큰 객체 반환 테스트")
    void getTokenByConcertIdAndCustomerId_ShouldReturnToken_WhenTokenExists() {
        Long concertId = 1L;
        Long customerId = 1L;
        Token token = new Token();
        token.setConcertId(concertId);
        token.setCustomerId(customerId);

        when(tokenRepository.getTokenByConcertIdAndCustomerId(concertId, customerId)).thenReturn(Optional.of(token));

        Token foundToken = tokenService.getTokenByConcertIdAndCustomerId(concertId, customerId);

        assertNotNull(foundToken);
        assertEquals(concertId, foundToken.getConcertId());
        assertEquals(customerId, foundToken.getCustomerId());
        verify(tokenRepository, times(1)).getTokenByConcertIdAndCustomerId(concertId, customerId);
    }

    @Test
    @DisplayName("콘서트 ID와 고객 ID로 조회할 때 토큰이 존재하지 않으면 예외 발생 테스트")
    void getTokenByConcertIdAndCustomerId_ShouldThrowException_WhenTokenDoesNotExist() {
        Long concertId = 1L;
        Long customerId = 1L;
        when(tokenRepository.getTokenByConcertIdAndCustomerId(concertId, customerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            tokenService.getTokenByConcertIdAndCustomerId(concertId, customerId);
        });

        assertEquals("Invalid token value", exception.getMessage());
        verify(tokenRepository, times(1)).getTokenByConcertIdAndCustomerId(concertId, customerId);
    }

    @Test
    @DisplayName("현재 시간 이전에 만료된 활성 토큰 목록 조회 테스트")
    void getActiveExpiredTokens_ShouldReturnExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        Token token1 = new Token();
        Token token2 = new Token();
        List<Token> tokens = List.of(token1, token2);

        when(tokenRepository.getActiveExpiredTokens(now)).thenReturn(tokens);

        List<Token> expiredTokens = tokenService.getActiveExpiredTokens(now);

        assertNotNull(expiredTokens);
        assertEquals(2, expiredTokens.size());
        verify(tokenRepository, times(1)).getActiveExpiredTokens(now);
    }

    @Test
    @DisplayName("다음 WAITING 상태의 토큰이 존재할 때 올바른 토큰 객체 반환 테스트")
    void getNextWaitingToken_ShouldReturnNextWaitingToken_WhenExists() {
        Long concertId = 1L;
        Token token = new Token();

        when(tokenRepository.getNextWaitingToken(concertId)).thenReturn(Optional.of(token));

        Optional<Token> nextWaitingToken = tokenService.getNextWaitingToken(concertId);

        assertTrue(nextWaitingToken.isPresent());
        verify(tokenRepository, times(1)).getNextWaitingToken(concertId);
    }

    @Test
    @DisplayName("다음 WAITING 상태의 토큰이 존재하지 않을 때 빈 Optional 반환 테스트")
    void getNextWaitingToken_ShouldReturnEmpty_WhenNotExists() {
        Long concertId = 1L;

        when(tokenRepository.getNextWaitingToken(concertId)).thenReturn(Optional.empty());

        Optional<Token> nextWaitingToken = tokenService.getNextWaitingToken(concertId);

        assertFalse(nextWaitingToken.isPresent());
        verify(tokenRepository, times(1)).getNextWaitingToken(concertId);
    }

    @Test
    @DisplayName("토큰 객체 저장 테스트")
    void updateToken_ShouldSaveToken() {
        Token token = new Token();

        tokenService.updateToken(token);

        verify(tokenRepository, times(1)).saveToken(token);
    }
}
