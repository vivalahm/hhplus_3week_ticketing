package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.model.TokenStatus;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TokenServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations); // ValueOperations 모킹 설정 추가
    }

    @Test
    @DisplayName("활성 토큰이 최대 개수보다 적을 때 ACTIVE 상태의 토큰 발행 테스트")
    void issueToken_ShouldReturnActiveToken_WhenActiveTokensLessThanMax() {
        Long customerId = 1L;
        Long concertId = 1L;
        int maxActiveTokens = 5;
        when(setOperations.size("queue:active:" + concertId)).thenReturn(4L);
        when(zSetOperations.size("queue:waiting:" + concertId)).thenReturn(0L);

        Token issuedToken = tokenService.issueToken(customerId, concertId, maxActiveTokens);

        assertNotNull(issuedToken);
        assertEquals(TokenStatus.ACTIVE, issuedToken.getStatus());
        verify(setOperations, times(1)).add("queue:active:" + concertId, issuedToken.getTokenValue());
        verify(valueOperations, times(1)).set("token:" + issuedToken.getTokenValue(), issuedToken);
        verify(redisTemplate, times(1)).expire("token:" + issuedToken.getTokenValue(), 10, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("활성 토큰이 최대 개수 이상일 때 WAITING 상태의 토큰 발행 테스트")
    void issueToken_ShouldReturnWaitingToken_WhenActiveTokensGreaterThanOrEqualToMax() {
        Long customerId = 1L;
        Long concertId = 1L;
        int maxActiveTokens = 5;
        when(setOperations.size("queue:active:" + concertId)).thenReturn(5L);
        when(zSetOperations.size("queue:waiting:" + concertId)).thenReturn(1L);

        Token token = tokenService.issueToken(customerId, concertId, maxActiveTokens);

        assertNotNull(token);
        assertEquals(TokenStatus.WAITING, token.getStatus());
        verify(zSetOperations, times(1)).add("queue:waiting:" + concertId, token.getTokenValue(), 0);
        verify(redisTemplate.opsForValue(), times(1)).set("token:" + token.getTokenValue(), token);
    }

    @Test
    @DisplayName("토큰 값으로 조회하여 토큰이 존재할 때 올바른 토큰 객체 반환 테스트")
    void getTokenByTokenValue_ShouldReturnToken_WhenTokenExists() {
        String tokenValue = UUID.randomUUID().toString();
        Token token = new Token();
        token.setTokenValue(tokenValue);

        when(redisTemplate.opsForValue().get("token:" + tokenValue)).thenReturn(token);

        Token foundToken = tokenService.getTokenByTokenValue(tokenValue);

        assertNotNull(foundToken);
        assertEquals(tokenValue, foundToken.getTokenValue());
        verify(redisTemplate.opsForValue(), times(1)).get("token:" + tokenValue);
    }

    @Test
    @DisplayName("토큰 값으로 조회할 때 토큰이 존재하지 않으면 예외 발생 테스트")
    void getTokenByTokenValue_ShouldThrowException_WhenTokenDoesNotExist() {
        String tokenValue = UUID.randomUUID().toString();
        when(redisTemplate.opsForValue().get("token:" + tokenValue)).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            tokenService.getTokenByTokenValue(tokenValue);
        });

        assertEquals(ErrorCode.NOT_FOUND.getCode(), exception.getErrorCode().getCode());
        verify(redisTemplate.opsForValue(), times(1)).get("token:" + tokenValue);
    }

    @Test
    @DisplayName("콘서트 ID와 고객 ID로 조회하여 토큰이 존재할 때 올바른 토큰 객체 반환 테스트")
    void getTokenByConcertIdAndCustomerId_ShouldReturnToken_WhenTokenExists() {
        Long concertId = 1L;
        Long customerId = 1L;
        Token token = new Token();
        token.setConcertId(concertId);
        token.setCustomerId(customerId);

        when(setOperations.members("queue:active:" + concertId)).thenReturn(Set.of(token.getTokenValue()));
        when(redisTemplate.opsForValue().get("token:" + token.getTokenValue())).thenReturn(token);

        Optional<Token> foundToken = tokenService.getTokenByConcertIdAndCustomerId(concertId, customerId);

        assertTrue(foundToken.isPresent());
        assertEquals(concertId, foundToken.get().getConcertId());
        assertEquals(customerId, foundToken.get().getCustomerId());
        verify(setOperations, times(1)).members("queue:active:" + concertId);
        verify(redisTemplate.opsForValue(), times(1)).get("token:" + token.getTokenValue());
    }

    @Test
    @DisplayName("콘서트 ID와 고객 ID로 조회할 때 토큰이 존재하지 않으면 예외 발생 테스트")
    void getTokenByConcertIdAndCustomerId_ShouldThrowException_WhenTokenDoesNotExist() {
        Long concertId = 1L;
        Long customerId = 1L;
        when(setOperations.members("queue:active:" + concertId)).thenReturn(Set.of());

        CustomException exception = assertThrows(CustomException.class, () -> {
            tokenService.getTokenByConcertIdAndCustomerId(concertId, customerId);
        });

        assertEquals(ErrorCode.NOT_FOUND.getCode(), exception.getErrorCode().getCode());
        verify(setOperations, times(1)).members("queue:active:" + concertId);
    }

    @Test
    @DisplayName("다음 WAITING 상태의 토큰이 존재할 때 올바른 토큰 객체 반환 테스트")
    void getNextWaitingToken_ShouldReturnNextWaitingToken_WhenExists() {
        Long concertId = 1L;
        String tokenValue = UUID.randomUUID().toString();
        Token token = new Token();
        token.setTokenValue(tokenValue);
        token.setStatus(TokenStatus.WAITING);

        when(zSetOperations.range("queue:waiting:" + concertId, 0, 1)).thenReturn(Set.of(tokenValue));
        when(redisTemplate.opsForValue().get("token:" + tokenValue)).thenReturn(token);

        Optional<String> nextWaitingToken = tokenService.getNextWaitingToken(concertId);

        assertTrue(nextWaitingToken.isPresent());
        assertEquals(tokenValue, nextWaitingToken.get());
        verify(zSetOperations, times(1)).range("queue:waiting:" + concertId, 0, 1);
        verify(redisTemplate.opsForValue(), times(1)).get("token:"+tokenValue);
    }

    @Test
    @DisplayName("다음 WAITING 상태의 토큰이 존재하지 않을 때 빈 Optional 반환 테스트")
    void getNextWaitingToken_ShouldReturnEmpty_WhenNotExists() {
        Long concertId = 1L;

        when(zSetOperations.range("queue:waiting:" + concertId, 0, 1)).thenReturn(Set.of());

        Optional<String> nextWaitingToken = tokenService.getNextWaitingToken(concertId);

        assertFalse(nextWaitingToken.isPresent());
        verify(zSetOperations, times(1)).range("queue:waiting:" + concertId, 0, 1);
    }

    @Test
    @DisplayName("토큰 객체 저장 테스트")
    void updateToken_ShouldSaveToken() {
        Token token = new Token();
        token.setTokenValue(UUID.randomUUID().toString());

        tokenService.updateToken(token);

        verify(redisTemplate.opsForValue(), times(1)).set("token:" + token.getTokenValue(), token);
        verify(redisTemplate, times(1)).expire("token:" + token.getTokenValue(), 10, TimeUnit.MINUTES); // TTL 업데이트 확인
    }
}