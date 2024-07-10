package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Token;
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

    public Token issueToken(Long customerId, Long concertId, int maxActiveTokens, boolean hasWaitingTokens){
        long activeTokenCount = tokenRepository.getCountActiveTokens(concertId);

        Token token = new Token();
        token.setCustomerId(customerId);
        token.setConcertId(customerId);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusHours(2));
        token.setTokenValue(generateTokenValue());

        if(activeTokenCount >= maxActiveTokens && !hasWaitingTokens){
            token.setStatus("ACTIVE");
        }
        else {
            token.setStatus("WAITING");
        }

        return tokenRepository.saveToken(token);
    }

    public Token getTokenByTokenValue(String tokenValue){
        Optional<Token> tokenOptional = tokenRepository.getTokenByTokenValue(tokenValue);
        if(tokenOptional.isEmpty()){
            throw new IllegalStateException("Invalid token value");
        }
        Token token = tokenOptional.get();
        if(!token.getStatus().equals("ACTIVE")){
            throw new IllegalStateException("Invalid token value");
        }
        return token;
    }

    public Long countActiveTokens(Long concertId){
        return tokenRepository.getCountActiveTokens(concertId);
    }

    public Boolean existWaitingTokens(Long concertId){
        return tokenRepository.getExistWaitingTokens(concertId);
    }

    public List<Token> getActiveExpiredTokens(Long concertId,LocalDateTime currentDateTime){
        return tokenRepository.getActiveExpiredTokens(concertId, currentDateTime);
    }

    public Optional<Token> getNextWaitingToken(Long concertId){
        return tokenRepository.getNextWaitingToken(concertId);
    }

    public void UpdateTokenStatus(Token token, String status){
        token.setStatus(status);
        tokenRepository.saveToken(token);
    }

    private String generateTokenValue(){
        return UUID.randomUUID().toString();
    }
}
