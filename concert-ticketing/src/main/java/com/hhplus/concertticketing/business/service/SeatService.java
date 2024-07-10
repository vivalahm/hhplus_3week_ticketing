package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public Seat lockSeat(Long concertOptionId, Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.getAvailableSeat(concertOptionId,seatId);
        if (optionalSeat.isPresent()) {
            Seat seat = optionalSeat.get();
            seat.setStatus("LOCKED");
            return seatRepository.saveSeat(seat);
        }else {
            throw new IllegalArgumentException("Seat does not exist");
        }
    }

    public void unlockSeat(Long seatId) {
        Seat seat = seatRepository.getSeatById(seatId).orElseThrow(()->new IllegalArgumentException("Seat does not exist"));
        seat.setStatus("AVAILABLE");
        seatRepository.saveSeat(seat);
    }

    public void resreveSeat(Long seatId) {
        Seat seat = seatRepository.getSeatById(seatId).orElseThrow(()->new IllegalArgumentException("Seat does not exist"));
        seat.setStatus("RESERVED");
        seatRepository.saveSeat(seat);
    }

    public List<Seat> getAvailableSeats(Long concertOptionId) {
        List<Seat> SeatList = seatRepository.getAvailableSeats(concertOptionId);
        if(SeatList.isEmpty()){
            throw new IllegalStateException("가능한 좌석이 없습니다.");
        }
        return SeatList;
    }

    public Optional<Seat> getAvailableSeat(Long concertOptionId, Long seatId) {

        return seatRepository.getAvailableSeat(concertOptionId,seatId);
    }

}
