package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.model.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<Token, Long> {
    @Query("SELECT COUNT(t) FROM Token t WHERE t.status = 'ACTIVE' AND t.concertId = :concertId")
    Long countActiveToken(@Param("concertId") Long concertId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Token t WHERE t.status = 'WAITING' AND t.concertId = :concertId")
    Boolean existsWaitingToken(@Param("concertId") Long concertId);

    @Query("SELECT t FROM Token t WHERE t.status = 'ACTIVE' AND t.expiresAt < :currentDateTime")
    List<Token> findActiveExpiredTokens(@Param("currentDateTime") LocalDateTime currentDateTime);

    Optional<Token> findFirstByConcertIdAndStatusOrderByCreatedAtAsc(Long concertId, TokenStatus status);

    @Query("SELECT t FROM Token t WHERE t.concertId = :concertId AND t.status = 'WAITING' AND t.createdAt < :createdAt ORDER BY t.createdAt DESC")
    Optional<Token> findLastWaitingTokenBefore(@Param("concertId") Long concertId, @Param("createdAt") LocalDateTime createdAt);

    Optional<Token> findByTokenValue(String tokenValue);

    Optional<Token> findByConcertIdAndCustomerId(Long concertId, Long customerId);
}
