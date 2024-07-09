package com.hhplus.concertticketing.business.repository;

import com.hhplus.concertticketing.business.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    List<Reservation> getReservationByCustomerId(Long customerId);
    Optional<Reservation> getReservationByReservationId(Long ReservationId);
}
