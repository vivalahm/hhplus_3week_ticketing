package com.hhplus.concertticketing.domain.service;

import com.hhplus.concertticketing.domain.model.Token;
import com.hhplus.concertticketing.domain.model.TokenStatus;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String WAITING_TOKEN_KEY = "queue:waiting";
    private static final String ACTIVE_TOKEN_KEY = "queue:active";

    @Autowired
    public TokenService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Token issueToken(Long customerId, Long concertId, int maxActiveTokens) {
        // 고유 키를 생성하여 중복 발급을 방지
        String uniqueKey = "issuedToken:" + concertId + ":" + customerId;
        Boolean isTokenAlreadyIssued = redisTemplate.hasKey(uniqueKey);

        // 만약 이미 해당 concertId와 customerId에 대한 토큰이 발급되었다면 중복 발급을 막음
        if (Boolean.TRUE.equals(isTokenAlreadyIssued)) {
            throw new IllegalStateException("이미 토큰이 발급되었습니다.");
        }

        long activeTokenCount = Optional.ofNullable(redisTemplate.opsForSet().size(ACTIVE_TOKEN_KEY + ":" + concertId)).orElse(0L);
        boolean hasWaitingTokens = Optional.ofNullable(redisTemplate.opsForZSet().size(WAITING_TOKEN_KEY + ":" + concertId)).orElse(0L) > 0;

        Token token = new Token();
        token.setCustomerId(customerId);
        token.setConcertId(concertId);
        token.setTokenValue(generateTokenValue());

        String tokenKey = "token:" + token.getTokenValue();

        if (activeTokenCount < maxActiveTokens && !hasWaitingTokens) {
            token.setStatus(TokenStatus.ACTIVE);
            redisTemplate.opsForSet().add(ACTIVE_TOKEN_KEY + ":" + concertId, token.getTokenValue());
            redisTemplate.opsForValue().set(tokenKey, token);
            redisTemplate.expire(tokenKey, 10, TimeUnit.MINUTES); // TTL을 10분으로 설정
        } else {
            token.setStatus(TokenStatus.WAITING);
            redisTemplate.opsForZSet().add(WAITING_TOKEN_KEY + ":" + concertId, token.getTokenValue(), (double) System.currentTimeMillis());
            redisTemplate.opsForValue().set(tokenKey, token);
        }

        // uniqueKey를 사용하여 발급 기록을 남기고, TTL 설정
        redisTemplate.opsForValue().set(uniqueKey, true);
        redisTemplate.expire(uniqueKey, 10, TimeUnit.MINUTES); // 10분 동안 중복 발급 방지

        return token;
    }


    public Set<Object> getActiveTokens(Long concertId) {
        return redisTemplate.opsForSet().members(ACTIVE_TOKEN_KEY + ":" + concertId);
    }


    public void removeActiveToken(Long concertId, String tokenValue) {
        redisTemplate.opsForSet().remove(ACTIVE_TOKEN_KEY + ":" + concertId, tokenValue);
    }

    public void removeToken(String tokenValue) {
        redisTemplate.delete("token:" + tokenValue);
    }

    public Token getTokenByTokenValue(String tokenValue) {
        return (Token) redisTemplate.opsForValue().get("token:" + tokenValue);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Token> getTokenByConcertIdAndCustomerId(Long concertId, Long customerId) {
        Set<Object> activeTokens = redisTemplate.opsForSet().members(ACTIVE_TOKEN_KEY + ":" + concertId);
        if (activeTokens != null) {
            for (Object tokenValue : activeTokens) {
                Token token = getTokenByTokenValue(tokenValue.toString());

                if (token.getCustomerId().equals(customerId)) {
                    return Optional.of(token);
                }
            }
        }
        throw new CustomException(ErrorCode.NOT_FOUND, "유효하지 않은 토큰 값입니다.");
    }

    public Optional<String> getNextWaitingToken(Long concertId) {
        Set<Object> waitingTokens = redisTemplate.opsForZSet().range(WAITING_TOKEN_KEY + ":" + concertId, 0, 1);
        if (waitingTokens != null && !waitingTokens.isEmpty()) {
            String tokenValue = waitingTokens.iterator().next().toString();
            Token token = getTokenByTokenValue(tokenValue);
            token.setStatus(TokenStatus.ACTIVE);
            redisTemplate.opsForSet().add(ACTIVE_TOKEN_KEY + ":" + concertId, token.getTokenValue());
            redisTemplate.opsForZSet().remove(WAITING_TOKEN_KEY + ":" + concertId, tokenValue);
            redisTemplate.opsForValue().set("token:" + tokenValue, token); // 상태 업데이트
            redisTemplate.expire("token:" + tokenValue, 10, TimeUnit.MINUTES); // 새로운 TTL 설정
            return Optional.of(tokenValue);
        }
        return Optional.empty();
    }

    public Long getWaitingTokenPosition(Long concertId, String tokenValue) {
        return redisTemplate.opsForZSet().rank(WAITING_TOKEN_KEY + ":" + concertId, tokenValue);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void updateToken(Token token) {
        redisTemplate.opsForValue().set("token:" + token.getTokenValue(), token);
        redisTemplate.expire("token:" + token.getTokenValue(), 10, TimeUnit.MINUTES); // TTL 업데이트
    }

    private String generateTokenValue() {
        return UUID.randomUUID().toString();
    }
}