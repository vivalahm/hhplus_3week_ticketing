package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.service.TokenService;
import com.hhplus.concertticketing.presentation.dto.request.TokenRequest;
import com.hhplus.concertticketing.presentation.dto.response.TokenResponse;
import com.hhplus.concertticketing.presentation.dto.response.TokenStatusResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TokenUseCase {
    private final TokenService tokenService;
    public TokenUseCase(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Transactional
    public void checkAndUpdateExpiredTokens(TokenRequest tokenRequest){
        List<Token> expiredTokens = tokenService.getActiveExpiredTokens(tokenRequest.getConcertId(), LocalDateTime.now());
        for(Token token : expiredTokens){
            tokenService.UpdateTokenStatus(token, "EXPIRED");
        }
    }

    public TokenResponse issueToken(TokenRequest tokenRequest, Integer maxActiveTokens){
        boolean hasWaitingTokens = tokenService.existWaitingTokens(tokenRequest.getConcertId());
        Token token = tokenService.issueToken(tokenRequest.getCustomerId(),tokenRequest.getConcertId(),maxActiveTokens,hasWaitingTokens);
        return new TokenResponse(token.getTokenValue(),token.getStatus(), token.getExpiresAt());
    }

    public TokenStatusResponse getTokenStatus(String tokenValue){
        String status = tokenService.getTokenByTokenValue(tokenValue).getStatus();
        return new TokenStatusResponse(status);
    }
}
