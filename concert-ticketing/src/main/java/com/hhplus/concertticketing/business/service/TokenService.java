package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.model.TokenStatus;
import com.hhplus.concertticketing.business.repository.TokenRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token issueToken(Long customerId, Long concertId, int maxActiveTokens) {
        long activeTokenCount = tokenRepository.getCountActiveTokens(concertId);
        boolean hasWaitingTokens = tokenRepository.getExistWaitingTokens(concertId);
        Token token = new Token();
        token.setCustomerId(customerId);
        token.setConcertId(concertId);
        token.setCreatedAt(LocalDateTime.now());
        token.setTokenValue(generateTokenValue());

        if (activeTokenCount <= maxActiveTokens && !hasWaitingTokens) {
            token.setStatus(TokenStatus.ACTIVE);
            token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        } else {
            token.setStatus(TokenStatus.WAITING);
        }

        return tokenRepository.saveToken(token);
    }

    public Token getTokenByTokenValue(String tokenValue) {
        return tokenRepository.getTokenByTokenValue(tokenValue)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "유효하지 않은 토큰 값입니다."));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Token> getTokenByConcertIdAndCustomerId(Long concertId, Long customerId) {
        Optional<Token> tokenOptional = tokenRepository.getTokenByConcertIdAndCustomerId(concertId, customerId);
        if (!tokenOptional.isPresent()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "주어진 콘서트 ID와 고객 ID에 대한 토큰이 존재하지 않습니다.");
        }
        return tokenOptional;
    }

    public List<Token> getActiveExpiredTokens(LocalDateTime currentDateTime) {
        return tokenRepository.getActiveExpiredTokens(currentDateTime);
    }

    public Optional<Token> getNextWaitingToken(Long concertId) {
        return tokenRepository.getNextWaitingToken(concertId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateToken(Token token) {
        tokenRepository.saveToken(token);
    }

    private String generateTokenValue() {
        return UUID.randomUUID().toString();
    }
}