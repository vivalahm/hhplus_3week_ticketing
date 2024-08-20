package com.hhplus.concertticketing.domain.repository;

import com.hhplus.concertticketing.domain.model.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {
    Token saveToken(Token token);
    Optional<Token> getTokenById(Long id);
    List<Token> getAllTokens();
    Long getCountActiveTokens(Long concertId);
    Boolean getExistWaitingTokens(Long concertId);
    Optional<Token> getNextWaitingToken(Long concertId);
    Optional<Token> getTokenByTokenValue(String tokenValue);
    Optional<Token> getTokenByConcertIdAndCustomerId(Long concertId, Long customerId);
    void deleteById(Long id);
}
