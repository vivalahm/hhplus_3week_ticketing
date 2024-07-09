package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.business.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<Token, Long> {
    @Query("SELECT COUNT(t) FROM Token t WHERE t.status = 'ACTIVE' AND t.concertId = :concertId")
    Long countActiveToken(@Param("concertId") Long concertId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Token t WHERE t.status = 'WAITING' AND t.concertId = :concertId")
    Boolean existsWaitingToken(@Param("concertId") Long concertId);

    @Query("SELECT t FROM Token t WHERE t.concertId = :concertId AND t.expiresAt < :currentDateTime AND t.status = 'ACTIVE'")
    List<Token> findActiveExpiredTokens(@Param("concertId") Long concertId, @Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT t FROM Token t WHERE t.status = 'WAITING' AND t.concertId = :concertId ORDER BY t.createdAt ASC")
    Optional<Token> findNextWaitingToken(@Param("concertId") Long concertId);
}
