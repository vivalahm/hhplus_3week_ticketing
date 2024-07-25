package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Concert;
import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.model.SeatStatus;
import com.hhplus.concertticketing.business.repository.ConcertOptionRepository;
import com.hhplus.concertticketing.business.repository.ConcertRepository;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;

import jakarta.persistence.OptimisticLockException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public Concert getConcertInfo(Long concertId) {
        Optional<Concert> concert = concertRepository.getConcertById(concertId);
        if(concert.isEmpty()){
            throw new CustomException(ErrorCode.NOT_FOUND, "콘서트가 존재하지 않습니다.");
        }
        return concert.get();
    }

    @Transactional
    public void markAsSoldOutIfSeatsNotAvailable(Long concertId) {
        List<ConcertOption> availableConcertOptions = concertOptionRepository.getByConcertIdAndIsAvailable(concertId, true);
        boolean allSoldOut = availableConcertOptions.isEmpty();

        if (allSoldOut) {
            Concert concert = concertRepository.getConcertById(concertId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "콘서트를 찾을 수 없습니다."));
            concert.soldOut();
            concertRepository.saveConcert(concert);
        }
    }

    //취소시 필요한 처리
    @Transactional
    public void checkAndReopenConcertSales(Long concertId) {
        boolean anyAvailable = !(concertOptionRepository.getByConcertIdAndIsAvailable(concertId, true).isEmpty());

        if (anyAvailable) {
            Concert concert = concertRepository.getConcertById(concertId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "콘서트를 찾을 수 없습니다."));
            concert.reopenSales();
            concertRepository.saveConcert(concert);
        }
    }

    //별도 배치 필요할듯 (하루에 한번?)
    @Transactional
    public void markConcertAsFinished(Long concertId) {
        List<ConcertOption> concertOptions = concertOptionRepository.getAllByConcertId(concertId);
        //1.concertOptionRepository를 통해 주어진 concertId에 해당하는 모든 콘서트 옵션을 조회.
        //2.조회된 콘서트 옵션들 중 getConcertDate 메서드를 사용하여 각 옵션의 날짜를 가져온다.
        //3.stream을 사용하여 모든 날짜를 비교하고, max 함수를 통해 가장 늦은 날짜를 찾는다.
        //4.찾아낸 가장 늦은 날짜가 현재 날짜보다 이전인 경우, 해당 콘서트가 종료되었다고 판단
        LocalDateTime latestOptionDate = concertOptions.stream()
                .map(ConcertOption::getConcertDate)
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "콘서트 옵션이 존재하지 않습니다."));

        if (latestOptionDate.isBefore(LocalDateTime.now())) {
            Concert concert = concertRepository.getConcertById(concertId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "콘서트를 찾을 수 없습니다."));
            concert.finishConcert();
            concertRepository.saveConcert(concert);
        }
    }

    //좌석없으면 콘서트 옵션 매진 처리
    public void markConcertOptionAsNotAvailableIfNoSeatsExist(Long concertOptionId) {
        Optional<ConcertOption> concertOption = concertOptionRepository.getConcertOptionById(concertOptionId);
        if(concertOption.isPresent()){
            if(concertOption.get().getIsAvailable()) {
                List<Seat> seats = seatRepository.getAvailableSeats(concertOptionId);
                if (seats.isEmpty()) {
                    concertOption.get().makeNotAvailable();
                }
            }
        }
    }

    //좌석있으면 콘서트 옵션 매진풀기
    public void markConcertOptionAsAvailableIfSeatsExist(Long concertOptionId) {
        Optional<ConcertOption> concertOption = concertOptionRepository.getConcertOptionById(concertOptionId);
        if(concertOption.isPresent()){
            if(!concertOption.get().getIsAvailable()) {
                List<Seat> seats = seatRepository.getAvailableSeats(concertOptionId);
                if (!seats.isEmpty()) {
                    concertOption.get().makeAvailable();
                }
            }
        }
    }

    public ConcertOption saveConcertOption(ConcertOption concertOption) {
        return concertOptionRepository.saveConcertOption(concertOption);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ConcertOption getConcertOptionById(Long concertOptionId) {
        Optional<ConcertOption> optionalConcertOption = concertOptionRepository.getConcertOptionById(concertOptionId);
        if(optionalConcertOption.isEmpty()){
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 콘서트 옵션을 발견하지 못했습니다.");
        }
        return optionalConcertOption.get();
    }

    public List<ConcertOption> getAvailableConcertOptions(Long concertId, LocalDateTime currentDateTime){
        Optional<Concert> concert = concertRepository.getConcertById(concertId);
        if(concert.isPresent()){
            if(concert.get().getIsFinished()){
                throw new CustomException(ErrorCode.BAD_REQUEST, "종료된 콘서트 입니다.");
            }
            if(concert.get().getIsSoldOut()){
                throw new CustomException(ErrorCode.BAD_REQUEST, "품절된 콘서트 입니다.");
            }
        }
        List<ConcertOption> concertOptionList = concertOptionRepository.getAllAvailableDatesByConcertId(concertId, currentDateTime);
        if(concertOptionList.isEmpty()){
            concert.get().soldOut();
            throw new CustomException(ErrorCode.NOT_FOUND, "예약 가능한 콘서트 옵션이 없습니다.");
        }
        return concertOptionList;
    }

    public void deleteConcertOptionById(Long concertOptionId) {
        concertOptionRepository.deleteConcertOption(concertOptionId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
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

    @Transactional(propagation = Propagation.REQUIRED)
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
        Optional<ConcertOption> concertOption = concertOptionRepository.getConcertOptionById(concertOptionId);
        if (concertOption.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "예약 가능한 콘서트 옵션이 없습니다.");
        }
        List<Seat> seatList = seatRepository.getAvailableSeats(concertOptionId);
        if (seatList.isEmpty()) {
            concertOption.get().makeNotAvailable();
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