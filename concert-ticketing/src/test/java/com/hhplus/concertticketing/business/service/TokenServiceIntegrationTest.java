package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.model.TokenStatus;
import com.hhplus.concertticketing.business.repository.TokenRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TokenServiceIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    private Token token;

    @BeforeEach
    void setUp() {
        token = new Token();
        token.setCustomerId(1L);
        token.setConcertId(1L);
        token.setTokenValue(UUID.randomUUID().toString());
        token.setStatus(TokenStatus.ACTIVE);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        tokenRepository.saveToken(token);
    }

    @Test
    @DisplayName("토큰 발급 통합 테스트")
    void issueToken_ShouldReturnToken() {
        Long customerId = 2L;
        Long concertId = 1L;
        int maxActiveTokens = 5;

        Token issuedToken = tokenService.issueToken(customerId, concertId, maxActiveTokens);

        assertNotNull(issuedToken);
        assertEquals(customerId, issuedToken.getCustomerId());
        assertEquals(concertId, issuedToken.getConcertId());
        assertNotNull(issuedToken.getTokenValue());
    }

    @Test
    @DisplayName("토큰 값으로 조회 통합 테스트")
    void getTokenByTokenValue_ShouldReturnToken() {
        Token foundToken = tokenService.getTokenByTokenValue(token.getTokenValue());

        assertNotNull(foundToken);
        assertEquals(token.getTokenValue(), foundToken.getTokenValue());
    }

    @Test
    @DisplayName("토큰 값으로 조회 시 토큰이 존재하지 않을 때 예외 발생 통합 테스트")
    void getTokenByTokenValue_ShouldThrowException_WhenTokenDoesNotExist() {
        String nonExistentTokenValue = UUID.randomUUID().toString();

        CustomException exception = assertThrows(CustomException.class, () -> {
            tokenService.getTokenByTokenValue(nonExistentTokenValue);
        });

        assertEquals(ErrorCode.NOT_FOUND.getCode(), exception.getErrorCode().getCode());
    }

    @Test
    @DisplayName("콘서트 ID와 고객 ID로 조회 통합 테스트")
    void getTokenByConcertIdAndCustomerId_ShouldReturnToken() {
        Optional<Token> foundToken = tokenService.getTokenByConcertIdAndCustomerId(token.getConcertId(), token.getCustomerId());

        assertNotNull(foundToken);
        assertEquals(token.getConcertId(), foundToken.get().getConcertId());
        assertEquals(token.getCustomerId(), foundToken.get().getCustomerId());
    }

    @Test
    @DisplayName("콘서트 ID와 고객 ID로 조회 시 토큰이 존재하지 않을 때 예외 발생 통합 테스트")
    void getTokenByConcertIdAndCustomerId_ShouldThrowException_WhenTokenDoesNotExist() {
        Long nonExistentConcertId = 999L;
        Long nonExistentCustomerId = 999L;

        CustomException exception = assertThrows(CustomException.class, () -> {
            tokenService.getTokenByConcertIdAndCustomerId(nonExistentConcertId, nonExistentCustomerId);
        });

        assertEquals(ErrorCode.NOT_FOUND.getCode(), exception.getErrorCode().getCode());
    }

    @Test
    @DisplayName("현재 시간 이전에 만료된 활성 토큰 목록 조회 통합 테스트")
    void getActiveExpiredTokens_ShouldReturnExpiredTokens() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(11);
        List<Token> expiredTokens = tokenService.getActiveExpiredTokens(now);

        assertNotNull(expiredTokens);
        assertEquals(1, expiredTokens.size());
        assertEquals(token.getTokenValue(), expiredTokens.get(0).getTokenValue());
    }

    @Test
    @DisplayName("다음 WAITING 상태의 토큰 조회 통합 테스트")
    void getNextWaitingToken_ShouldReturnNextWaitingToken() {
        token.setStatus(TokenStatus.WAITING);
        tokenRepository.saveToken(token);

        Optional<Token> nextWaitingToken = tokenService.getNextWaitingToken(token.getConcertId());

        assertTrue(nextWaitingToken.isPresent());
        assertEquals(token.getTokenValue(), nextWaitingToken.get().getTokenValue());
    }

    @Test
    @DisplayName("다음 WAITING 상태의 토큰이 존재하지 않을 때 빈 Optional 반환 통합 테스트")
    void getNextWaitingToken_ShouldReturnEmpty_WhenNotExists() {
        Optional<Token> nextWaitingToken = tokenService.getNextWaitingToken(token.getConcertId());

        assertFalse(nextWaitingToken.isPresent());
    }

    @Test
    @DisplayName("토큰 업데이트 통합 테스트")
    void updateToken_ShouldSaveToken() {
        token.setStatus(TokenStatus.EXPIRED);

        tokenService.updateToken(token);

        Optional<Token> updatedToken = tokenRepository.getTokenByTokenValue(token.getTokenValue());
        assertTrue(updatedToken.isPresent());
        assertEquals(TokenStatus.EXPIRED, updatedToken.get().getStatus());
    }
}