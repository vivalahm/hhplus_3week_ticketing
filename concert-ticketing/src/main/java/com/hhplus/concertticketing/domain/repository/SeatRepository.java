package com.hhplus.concertticketing.domain.repository;

import com.hhplus.concertticketing.domain.model.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    Seat saveSeat(Seat seat);
    Optional<Seat> getSeatById(Long id);
    List<Seat> getAvailableSeats(Long concertOptionId);
    Optional<Seat> getAvailableSeat(Long concertOptionId, Long seatId);
}
