package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.model.TokenStatus;
import com.hhplus.concertticketing.business.service.TokenService;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.TokenRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenResponse;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenStatusResponse;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TokenUseCase {
    private final TokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(TokenUseCase.class);

    public TokenUseCase(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Transactional
    public void checkAndUpdateExpiredTokens() {
        List<Token> expiredTokens = tokenService.getActiveExpiredTokens(LocalDateTime.now());
        for (Token token : expiredTokens) {
            token.setStatus(TokenStatus.EXPIRED);
            tokenService.updateToken(token);
            Optional<Token> nextWaitingTokenOptional = tokenService.getNextWaitingToken(token.getConcertId());
            if (nextWaitingTokenOptional.isPresent()) {
                Token nextWaitingToken = nextWaitingTokenOptional.get();
                nextWaitingToken.setStatus(TokenStatus.ACTIVE);
                nextWaitingToken.setExpiresAt(LocalDateTime.now().plusMinutes(10));
                tokenService.updateToken(nextWaitingToken);
            }
        }
    }

    public TokenResponse issueToken(TokenRequest tokenRequest, Integer maxActiveTokens) {
        Token token = tokenService.issueToken(tokenRequest.getCustomerId(), tokenRequest.getConcertId(), maxActiveTokens);
        return new TokenResponse(token.getTokenValue(), token.getStatus(), token.getExpiresAt());
    }

    public TokenStatusResponse getTokenStatus(String tokenValue) {
        Token token = tokenService.getTokenByTokenValue(tokenValue);
        TokenStatus status = token.getStatus();
        if (!status.equals(TokenStatus.WAITING)) {
            return new TokenStatusResponse(status, 0L);
        }

        Long currentPositon = 1L;
        Optional<Token> firstCandidateToken = tokenService.getNextWaitingToken(token.getConcertId());
        if (firstCandidateToken.isPresent()) {
            currentPositon = token.getId() - firstCandidateToken.get().getId();
        }

        return new TokenStatusResponse(status, currentPositon);
    }
}