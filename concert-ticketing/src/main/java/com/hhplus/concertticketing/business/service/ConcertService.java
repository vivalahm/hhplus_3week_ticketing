package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.model.SeatStatus;
import com.hhplus.concertticketing.business.repository.ConcertOptionRepository;
import com.hhplus.concertticketing.business.repository.ConcertRepository;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertOptionRepository concertOptionRepository;
    private final SeatRepository seatRepository;

    public ConcertService(ConcertRepository concertRepository, ConcertOptionRepository concertOptionRepository, SeatRepository seatRepository){
        this.concertRepository = concertRepository;
        this.concertOptionRepository = concertOptionRepository;
        this.seatRepository = seatRepository;
    }

    public ConcertOption saveConcertOption(ConcertOption concertOption) {
        return concertOptionRepository.saveConcertOption(concertOption);
    }

    public ConcertOption getConcertOptionById(Long concertOptionId) {
        Optional<ConcertOption> optionalConcertOption = concertOptionRepository.getConcertOptionById(concertOptionId);
        if(optionalConcertOption.isEmpty()){
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 콘서트 옵션을 발견하지 못했습니다.");
        }
        return optionalConcertOption.get();
    }

    public List<ConcertOption> getAvailableConcertOptions(Long concertOptionId, LocalDateTime currentDateTime){
        List<ConcertOption> concertOptionList = concertOptionRepository.getAllAvailableDatesByConcertId(concertOptionId, currentDateTime);
        if(concertOptionList.isEmpty()){
            throw new CustomException(ErrorCode.NOT_FOUND, "예약 가능한 콘서트 옵션이 없습니다.");
        }
        return concertOptionList;
    }

    public void deleteConcertOptionById(Long concertOptionId) {
        concertOptionRepository.deleteConcertOption(concertOptionId);
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
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 좌석을 잠그는 중입니다. 잠시 후 다시 시도해주세요.");
                }
            } else {
                throw new CustomException(ErrorCode.BAD_REQUEST, "좌석이 예약 가능하지 않습니다.");
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