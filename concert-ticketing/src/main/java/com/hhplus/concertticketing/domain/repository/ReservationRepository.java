package com.hhplus.concertticketing.domain.repository;

import com.hhplus.concertticketing.domain.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation saveReservation(Reservation reservation);
    Optional<Reservation> getReservationById(Long id);
    List<Reservation> getReservationBycustomerId(Long customerId);
    List<Reservation> getExpiredReservations(LocalDateTime currentTime);
    void deleteReservationById(Long id);
}
