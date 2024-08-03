package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.model.TokenStatus;
import com.hhplus.concertticketing.business.service.TokenService;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.TokenRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenResponse;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenStatusResponse;
import com.hhplus.concertticketing.common.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.Set;

@Component
public class TokenUseCase {
    private final TokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(TokenUseCase.class);

    public TokenUseCase(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Transactional
    public void checkAndUpdateExpiredTokens(Long concertId, int maxActiveTokens) {
        Set<Object> activeTokens = tokenService.getActiveTokens(concertId);
        int removedCount = 0;

        for (Object tokenValue : activeTokens) {
            try {
                logger.info("토큰상태 확인: " + tokenValue);
                Token token = tokenService.getTokenByTokenValue(tokenValue.toString());
                if (token == null) {
                    tokenService.removeActiveToken(concertId, tokenValue.toString());
                    removedCount++;
                }
            } catch (CustomException e) {
                // Log the exception for debugging
                logger.error("토큰 상태를 확인하는 도중에러가 발생했습니다.: " + e.getMessage());
                tokenService.removeActiveToken(concertId, tokenValue.toString());
                removedCount++;
            }
        }

        // Move waiting tokens to active
        moveWaitingTokensToActive(concertId, removedCount, maxActiveTokens);
    }

    private void moveWaitingTokensToActive(Long concertId, int count, int maxActiveTokens) {
        for (int i = 0; i < count; i++) {
            Optional<String> nextWaitingTokenValue = tokenService.getNextWaitingToken(concertId);
            nextWaitingTokenValue.ifPresent(tokenValue -> {
                Token nextWaitingToken = tokenService.getTokenByTokenValue(tokenValue);
                nextWaitingToken.setStatus(TokenStatus.ACTIVE);
                tokenService.updateToken(nextWaitingToken);
            });
        }
    }

    public TokenResponse issueToken(TokenRequest tokenRequest, Integer maxActiveTokens) {

        // 토큰 발급
        Token token = tokenService.issueToken(tokenRequest.getCustomerId(), tokenRequest.getConcertId(), maxActiveTokens);
        return new TokenResponse(token.getTokenValue(), token.getStatus()); // TTL을 통해 만료 관리
    }

    public TokenStatusResponse getTokenStatus(String tokenValue) {
        Token token = tokenService.getTokenByTokenValue(tokenValue);
        TokenStatus status = token.getStatus();
        if (status != TokenStatus.WAITING) {
            return new TokenStatusResponse(status, 0L);
        }

        // 대기열에서의 위치 계산
        Long position = calculateQueuePosition(token);

        return new TokenStatusResponse(status, position);
    }

    private Long calculateQueuePosition(Token token) {
        Long position = tokenService.getWaitingTokenPosition(token.getConcertId(), token.getTokenValue());
        return (position != null) ? position + 1 : -1; // 0-based index를 1-based index로 변환
    }
}