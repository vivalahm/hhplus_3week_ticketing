package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.model.SeatStatus;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private static final Logger logger = LoggerFactory.getLogger(SeatService.class);

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Transactional
    public Seat lockSeat(Long concertOptionId, Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.getAvailableSeat(concertOptionId, seatId);
        if (optionalSeat.isPresent()) {
            Seat seat = optionalSeat.get();
            if (seat.getStatus() == SeatStatus.AVAILABLE) {
                seat.setStatus(SeatStatus.LOCKED);
                try {
                    return seatRepository.saveSeat(seat);
                } catch (OptimisticLockException e) {
                    logger.error("동시에 좌석을 잠그는 중입니다. ConcertOptionId: {}, SeatId: {}", concertOptionId, seatId, e);
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 좌석을 잠그는 중입니다. 잠시 후 다시 시도해주세요.");
                }
            } else {
                throw new CustomException(ErrorCode.BAD_REQUEST, "좌석이 사용할 수 없습니다.");
            }
        } else {
            throw new CustomException(ErrorCode.NOT_FOUND, "좌석이 존재하지 않습니다.");
        }
    }

    @Transactional
    public void unlockSeat(Long seatId) {
        Seat seat = seatRepository.getSeatById(seatId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "좌석이 존재하지 않습니다."));
        seat.setStatus(SeatStatus.AVAILABLE);
        try {
            seatRepository.saveSeat(seat);
        } catch (OptimisticLockException e) {
            logger.error("동시에 좌석 잠금을 해제하는 중입니다. SeatId: {}", seatId, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 좌석 잠금을 해제하는 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Transactional
    public void reserveSeat(Long seatId) {
        Seat seat = seatRepository.getSeatById(seatId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "좌석이 존재하지 않습니다."));
        seat.setStatus(SeatStatus.RESERVED);
        try {
            seatRepository.saveSeat(seat);
        } catch (OptimisticLockException e) {
            logger.error("동시에 좌석을 예약하는 중입니다. SeatId: {}", seatId, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 좌석을 예약하는 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }

    public List<Seat> getAvailableSeats(Long concertOptionId) {
        List<Seat> seatList = seatRepository.getAvailableSeats(concertOptionId);
        if (seatList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "가능한 좌석이 없습니다.");
        }
        return seatList;
    }

    public Seat getAvailableSeat(Long concertOptionId, Long seatId) {
        Optional<Seat> seatOptional = seatRepository.getAvailableSeat(concertOptionId, seatId);
        if (seatOptional.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "좌석이 존재하지 않습니다.");
        }
        return seatOptional.get();
    }
}