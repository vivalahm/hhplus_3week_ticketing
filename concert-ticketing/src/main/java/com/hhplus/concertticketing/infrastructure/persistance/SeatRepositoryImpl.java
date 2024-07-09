package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;
    public SeatRepositoryImpl(SeatJpaRepository seatJpaRepository) {
        this.seatJpaRepository = seatJpaRepository;
    }

    @Override
    public List<Seat> getAvailableSeats(Long concertOptionId){
        return seatJpaRepository.findAvailableSeats(concertOptionId);
    }

    @Override
    public Optional<Seat> getAvailableSeat(Long concertOptionId, String seatNumber){
        return seatJpaRepository.findAvailableSeat(concertOptionId, seatNumber);
    }
}
