package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.business.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s WHERE s.concertOption.id = :concertOptionId AND s.status='AVAILABLE'")
    List<Seat> findAvailableSeats(@Param("concertOptionId") Long concertOptionId);

    @Query("SELECT s FROM Seat s WHERE s.concertOption.id = :concertOptionId AND s.id = :seatId AND s.status = 'AVAILABLE'")
    Optional<Seat> findAvailableSeat(@Param("concertOptionId") Long concertOptionId, @Param("seatId") long seatId);
}
