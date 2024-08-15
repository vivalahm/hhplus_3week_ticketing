package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.domain.model.Seat;
import com.hhplus.concertticketing.domain.repository.SeatRepository;
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
    public Seat saveSeat(Seat seat) {
        return seatJpaRepository.save(seat);
    }

    @Override
    public Optional<Seat> getSeatById(Long id) {
        return seatJpaRepository.findById(id);
    }

    @Override
    public List<Seat> getAvailableSeats(Long concertOptionId){
        return seatJpaRepository.findAvailableSeats(concertOptionId);
    }

    @Override
    public Optional<Seat> getAvailableSeat(Long concertOptionId, Long seatId) {
        return seatJpaRepository.findAvailableSeat(concertOptionId,seatId);
    }

}
