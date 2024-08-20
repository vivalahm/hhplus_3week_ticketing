package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.domain.model.Token;
import com.hhplus.concertticketing.domain.model.TokenStatus;
import com.hhplus.concertticketing.domain.repository.TokenRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TokenRepositoryImpl implements TokenRepository {
    private final TokenJpaRepository tokenJpaRepository;
    public TokenRepositoryImpl(TokenJpaRepository tokenJpaRepository){
        this.tokenJpaRepository = tokenJpaRepository;
    }

    @Override
    public Long getCountActiveTokens(Long concertId){
        return tokenJpaRepository.countActiveToken(concertId);
    }

    @Override
    public Boolean getExistWaitingTokens(Long concertId) {
        return tokenJpaRepository.existsWaitingToken(concertId);
    }


    @Override
    public Optional<Token> getNextWaitingToken(Long concertId) {
        return tokenJpaRepository.findFirstByConcertIdAndStatus(concertId, TokenStatus.WAITING);
    }

    @Override
    public Optional<Token> getTokenByTokenValue(String tokenValue) {
        return tokenJpaRepository.findByTokenValue(tokenValue);
    }

    @Override
    public Optional<Token> getTokenByConcertIdAndCustomerId(Long concertId, Long customerId) {
        return tokenJpaRepository.findByConcertIdAndCustomerId(concertId,customerId);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Token saveToken(Token token){
        return tokenJpaRepository.save(token);
    }

    @Override
    public Optional<Token> getTokenById(Long id) {
        return tokenJpaRepository.findById(id);
    }

    @Override
    public List<Token> getAllTokens() {
        return tokenJpaRepository.findAll();
    }
}
