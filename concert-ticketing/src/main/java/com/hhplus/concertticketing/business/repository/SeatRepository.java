package com.hhplus.concertticketing.business.repository;

import com.hhplus.concertticketing.business.model.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    List<Seat> getAvailableSeats(Long concertOptionId);
    Optional<Seat> getAvailableSeat(Long concertOptionId, String seatNumber);
}
