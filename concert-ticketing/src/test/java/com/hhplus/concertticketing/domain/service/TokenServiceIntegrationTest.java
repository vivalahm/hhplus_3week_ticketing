package com.hhplus.concertticketing.domain.service;

import com.hhplus.concertticketing.domain.model.Token;
import com.hhplus.concertticketing.domain.model.TokenStatus;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TokenServiceIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Token token;
    private SetOperations<String, Object> setOperations;
    private ZSetOperations<String, Object> zSetOperations;

    @BeforeEach
    void setUp() {
        setOperations = redisTemplate.opsForSet();
        zSetOperations = redisTemplate.opsForZSet();

        token = new Token();
        token.setCustomerId(1L);
        token.setConcertId(1L);
        token.setTokenValue(UUID.randomUUID().toString());
        token.setStatus(TokenStatus.ACTIVE);
        // 토큰을 Redis에 저장
        redisTemplate.opsForValue().set("token:" + token.getTokenValue(), token);
        redisTemplate.expire("token:" + token.getTokenValue(), 10, TimeUnit.MINUTES);
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

        assertTrue(foundToken.isPresent());
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
    @DisplayName("다음 WAITING 상태의 토큰 조회 통합 테스트")
    void getNextWaitingToken_ShouldReturnNextWaitingToken() {
        // WAITING 상태의 토큰을 추가
        token.setStatus(TokenStatus.WAITING);
        zSetOperations.add("queue:waiting:" + token.getConcertId(), token.getTokenValue(), 0);
        redisTemplate.opsForValue().set("token:" + token.getTokenValue(), token);

        Optional<String> nextWaitingToken = tokenService.getNextWaitingToken(token.getConcertId());

        assertTrue(nextWaitingToken.isPresent());
        assertEquals(token.getTokenValue(), nextWaitingToken.get());
    }

    @Test
    @DisplayName("다음 WAITING 상태의 토큰이 존재하지 않을 때 빈 Optional 반환 통합 테스트")
    void getNextWaitingToken_ShouldReturnEmpty_WhenNotExists() {
        Optional<String> nextWaitingToken = tokenService.getNextWaitingToken(token.getConcertId());

        assertFalse(nextWaitingToken.isPresent());
    }

    @Test
    @DisplayName("토큰 업데이트 통합 테스트")
    void updateToken_ShouldSaveToken() {
        token.setStatus(TokenStatus.EXPIRED);
        tokenService.updateToken(token);

        Token updatedToken = (Token) redisTemplate.opsForValue().get("token:" + token.getTokenValue());
        assertNotNull(updatedToken);
        assertEquals(TokenStatus.EXPIRED, updatedToken.getStatus());
    }
}