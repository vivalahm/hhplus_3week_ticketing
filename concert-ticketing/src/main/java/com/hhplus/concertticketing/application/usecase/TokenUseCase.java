package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.model.TokenStatus;
import com.hhplus.concertticketing.business.service.TokenService;
import com.hhplus.concertticketing.presentation.dto.request.TokenRequest;
import com.hhplus.concertticketing.presentation.dto.response.TokenResponse;
import com.hhplus.concertticketing.presentation.dto.response.TokenStatusResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TokenUseCase {
    private final TokenService tokenService;
    public TokenUseCase(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Transactional
    public void checkAndUpdateExpiredTokens(){
        List<Token> expiredTokens = tokenService.getActiveExpiredTokens(LocalDateTime.now()); // ACTIVE 상태이지만 시간이 만료된 토큰 가져온다.
        for(Token token : expiredTokens){
            token.setStatus(TokenStatus.EXPIRED);
            tokenService.updateToken(token);//만료처리함.
            Optional<Token> nextWaitingTokenOptional = tokenService.getNextWaitingToken(token.getConcertId());//만료되면 은행창구 식으로 다음 대기 사용자 가져온다.
            if (nextWaitingTokenOptional.isPresent()) {
                Token nextWaitingToken = nextWaitingTokenOptional.get();
                nextWaitingToken.setStatus(TokenStatus.ACTIVE);
                nextWaitingToken.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // 만료 시간 10분 부여
                tokenService.updateToken(nextWaitingToken);
            }
        }
    }

    public TokenResponse issueToken(TokenRequest tokenRequest, Integer maxActiveTokens){
        Token token = tokenService.issueToken(tokenRequest.getCustomerId(),tokenRequest.getConcertId(),maxActiveTokens);
        return new TokenResponse(token.getTokenValue(),token.getStatus(), token.getExpiresAt());
    }

    public TokenStatusResponse getTokenStatus(String tokenValue){
        Token token = tokenService.getTokenByTokenValue(tokenValue);
        TokenStatus status = token.getStatus();
        Long currentPositon = 1L;

        Optional<Token> firstCandidateToken = tokenService.getNextWaitingToken(token.getConcertId());
        //가장 먼저 기다리는 후보가 없다면 내가 일등
        if (firstCandidateToken.isPresent()) {
            currentPositon = token.getId() - firstCandidateToken.get().getId();
        }

        return new TokenStatusResponse(status,currentPositon);
    }
}
