package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.model.TokenStatus;
import com.hhplus.concertticketing.business.repository.TokenRepository;
import org.springframework.stereotype.Service;

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

    public Token issueToken(Long customerId, Long concertId, int maxActiveTokens){
        long activeTokenCount = tokenRepository.getCountActiveTokens(concertId);
        boolean hasWaitingTokens = tokenRepository.getExistWaitingTokens(concertId);
        Token token = new Token();
        token.setCustomerId(customerId);
        token.setConcertId(concertId);
        token.setCreatedAt(LocalDateTime.now());
        token.setTokenValue(generateTokenValue());

        if(activeTokenCount <= maxActiveTokens && !hasWaitingTokens){
            token.setStatus(TokenStatus.ACTIVE);
            token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        }
        else {
            token.setStatus(TokenStatus.WAITING);
        }

        return tokenRepository.saveToken(token);
    }

    public Token getTokenByTokenValue(String tokenValue){
        Optional<Token> tokenOptional = tokenRepository.getTokenByTokenValue(tokenValue);
        if(tokenOptional.isEmpty()){
            throw new IllegalStateException("Invalid token value");
        }
        Token token = tokenOptional.get();
        return token;
    }

    public Token getTokenByConcertIdAndCustomerId(Long concertId, Long customerId){
        Optional<Token> optionalToken = tokenRepository.getTokenByConcertIdAndCustomerId(concertId,customerId);
        if(optionalToken.isEmpty()){
            throw new IllegalStateException("Invalid token value");
        }
        return optionalToken.get();
    }

    public List<Token> getActiveExpiredTokens(LocalDateTime currentDateTime){
        return tokenRepository.getActiveExpiredTokens(currentDateTime);
    }

    public Optional<Token> getNextWaitingToken(Long concertId){
        return tokenRepository.getNextWaitingToken(concertId);
    }

    public void updateToken(Token token){
        tokenRepository.saveToken(token);
    }

    private String generateTokenValue(){
        return UUID.randomUUID().toString();
    }
}
