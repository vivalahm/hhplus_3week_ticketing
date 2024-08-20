package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.customerId =:customerId")
    List<Reservation> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT r FROM Reservation r WHERE r.expiresAt < :currentDateTime AND r.status ='RESERVING'")
    List<Reservation> findExpiredReservations(@Param("currentDateTime") LocalDateTime currentDateTime);

}
