package com.hhplus.concertticketing.business.repository;

import com.hhplus.concertticketing.business.model.Token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRepository {
    Token saveToken(Token token);
    Optional<Token> getTokenById(Long id);
    List<Token> getAllTokens();
    Long getCountActiveTokens(Long concertId);
    Boolean getExistWaitingTokens(Long concertId);
    List<Token> getActiveExpiredTokens(Long concertId, LocalDateTime currentDateTime);
    Optional<Token> getNextWaitingToken(Long concertId);
    Optional<Token> getTokenByTokenValue(String tokenValue);
    void deleteById(Long id);
}
