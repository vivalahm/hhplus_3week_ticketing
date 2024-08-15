package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.domain.model.Token;
import com.hhplus.concertticketing.domain.model.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<Token, Long> {
    @Query("SELECT COUNT(t) FROM Token t WHERE t.status = 'ACTIVE' AND t.concertId = :concertId")
    Long countActiveToken(@Param("concertId") Long concertId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Token t WHERE t.status = 'WAITING' AND t.concertId = :concertId")
    Boolean existsWaitingToken(@Param("concertId") Long concertId);


    Optional<Token> findFirstByConcertIdAndStatus(Long concertId, TokenStatus status);


    Optional<Token> findByTokenValue(String tokenValue);

    Optional<Token> findByConcertIdAndCustomerId(Long concertId, Long customerId);
}
