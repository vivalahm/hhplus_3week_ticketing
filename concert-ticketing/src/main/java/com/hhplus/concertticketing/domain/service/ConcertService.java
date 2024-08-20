package com.hhplus.concertticketing.domain.service;

import com.hhplus.concertticketing.Interfaces.presentation.dto.request.ConcertRequest;
import com.hhplus.concertticketing.domain.model.*;
import com.hhplus.concertticketing.domain.repository.ConcertOptionRepository;
import com.hhplus.concertticketing.domain.repository.ConcertRepository;
import com.hhplus.concertticketing.domain.repository.SeatRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import jakarta.persistence.OptimisticLockException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
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

    public List<Concert> getAvailableConcerts(){
        return concertRepository.getAvailableConcerts();
    }

    public Concert saveConcert(ConcertRequest concertRequest) {
        Concert concert = new Concert(concertRequest.getConcertName());
        return concertRepository.saveConcert(concert);
    }

    public Concert getConcertInfo(Long concertId) {
        return concertRepository.getConcertById(concertId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "콘서트가 존재하지 않습니다."));
    }

    @Transactional
    public void markAsSoldOutIfSeatsNotAvailable(Long concertId) {
        boolean allSoldOut = concertOptionRepository.getByConcertIdAndIsAvailable(concertId, true).isEmpty();

        if (allSoldOut) {
            Concert concert = concertRepository.getConcertById(concertId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "콘서트를 찾을 수 없습니다."));
            concert.soldOut();
            concertRepository.saveConcert(concert);
        }
    }

    @Transactional
    public void checkAndReopenConcertSales(Long concertId) {
        boolean anyAvailable = !(concertOptionRepository.getByConcertIdAndIsAvailable(concertId, true).isEmpty());

        if (anyAvailable) {
            Concert concert = concertRepository.getConcertById(concertId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "콘서트를 찾을 수 없습니다."));
            concert.reopenSales();
            concertRepository.saveConcert(concert);
        }
    }

    @Transactional
    public void markConcertAsFinished(Long concertId) {
        List<ConcertOption> concertOptions = concertOptionRepository.getAllByConcertId(concertId);

        LocalDateTime latestOptionDate = concertOptions.stream()
                .map(ConcertOption::getConcertDate)
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "콘서트 옵션이 존재하지 않습니다."));

        if (latestOptionDate.isBefore(LocalDateTime.now())) {
            Concert concert = concertRepository.getConcertById(concertId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "콘서트를 찾을 수 없습니다."));
            concert.finishConcert();
            concertRepository.saveConcert(concert);
        }
    }

    public void markConcertOptionAsNotAvailableIfNoSeatsExist(Long concertOptionId) {
        Optional<ConcertOption> concertOption = concertOptionRepository.getConcertOptionById(concertOptionId);
        if (concertOption.isPresent() && concertOption.get().getIsAvailable()) {
            List<Seat> seats = seatRepository.getAvailableSeats(concertOptionId);
            if (seats.isEmpty()) {
                concertOption.get().makeNotAvailable();
            }
        }
    }

    public void markConcertOptionAsAvailableIfSeatsExist(Long concertOptionId) {
        Optional<ConcertOption> concertOption = concertOptionRepository.getConcertOptionById(concertOptionId);
        if (concertOption.isPresent() && !concertOption.get().getIsAvailable()) {
            List<Seat> seats = seatRepository.getAvailableSeats(concertOptionId);
            if (!seats.isEmpty()) {
                concertOption.get().makeAvailable();
            }
        }
    }

    public ConcertOption saveConcertOption(ConcertOption concertOption) {
        return concertOptionRepository.saveConcertOption(concertOption);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ConcertOption getConcertOptionById(Long concertOptionId) {
        return concertOptionRepository.getConcertOptionById(concertOptionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 콘서트 옵션을 발견하지 못했습니다."));
    }

    @Cacheable(value = "availableConcertOptions", key="#p0", condition="#p0!=null")
    public List<ConcertOption> getAvailableConcertOptions(Long concertId){
        Optional<Concert> concert = concertRepository.getConcertById(concertId);
        if(concert.isPresent()){
            if(concert.get().getIsFinished()){
                throw new CustomException(ErrorCode.BAD_REQUEST, "종료된 콘서트 입니다.");
            }
            if(concert.get().getIsSoldOut()){
                throw new CustomException(ErrorCode.BAD_REQUEST, "품절된 콘서트 입니다.");
            }
            List<ConcertOption> concertOptionList = concertOptionRepository.getAllAvailableDatesByConcertId(concertId, LocalDateTime.now());
            if(concertOptionList.isEmpty()){
                concert.get().soldOut();
                throw new CustomException(ErrorCode.NOT_FOUND, "예약 가능한 콘서트 옵션이 없습니다.");
            }
            return concertOptionList;
        }
        throw new CustomException(ErrorCode.NOT_FOUND, "콘서트가 존재하지 않습니다.");
    }


    public void deleteConcertOptionById(Long concertOptionId) {
        concertOptionRepository.deleteConcertOption(concertOptionId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Seat lockSeat(Long concertOptionId, Long seatId) {
        Seat seat = seatRepository.getAvailableSeat(concertOptionId, seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "좌석이 존재하지 않습니다."));

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
    }

    @Transactional
    public void unlockSeat(Long seatId) {
        Seat seat = seatRepository.getSeatById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "좌석이 존재하지 않습니다."));
        seat.setStatus(SeatStatus.AVAILABLE);
        try {
            seatRepository.saveSeat(seat);
        } catch (OptimisticLockException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 좌석 잠금을 해제하는 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void reserveSeat(Long seatId) {
        Seat seat = seatRepository.getSeatById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "좌석이 존재하지 않습니다."));
        seat.setStatus(SeatStatus.RESERVED);
        try {
            seatRepository.saveSeat(seat);
        } catch (OptimisticLockException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 좌석을 예약하는 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }

    public List<Seat> getAvailableSeats(Long concertOptionId) {
        ConcertOption concertOption = concertOptionRepository.getConcertOptionById(concertOptionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "예약 가능한 콘서트 옵션이 없습니다."));

        List<Seat> seatList = seatRepository.getAvailableSeats(concertOptionId);
        if (seatList.isEmpty()) {
            concertOption.makeNotAvailable();
            throw new CustomException(ErrorCode.NOT_FOUND, "가능한 좌석이 없습니다.");
        }
        return seatList;
    }

    public Seat getAvailableSeat(Long concertOptionId, Long seatId) {
        return seatRepository.getAvailableSeat(concertOptionId, seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "좌석이 존재하지 않습니다."));
    }
}