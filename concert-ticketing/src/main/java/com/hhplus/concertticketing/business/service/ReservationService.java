package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Reservation;
import com.hhplus.concertticketing.business.repository.ReservationRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);


    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation reserveTicket(Long customerId, Long concertOptionId, Long seatId) {
        Reservation reservation = new Reservation();
        reservation.reserveTicket(customerId, concertOptionId, seatId);
        try {
            return reservationRepository.saveReservation(reservation);
        } catch (OptimisticLockException e) {
            logger.error("동시에 예약을 시도하는 중입니다. CustomerId: {}, ConcertOptionId: {}, SeatId: {}", customerId, concertOptionId, seatId, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 예약을 시도하는 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Reservation> getReservationById(Long reservationId) {
        return reservationRepository.getReservationById(reservationId);
    }

    @Transactional
    public List<Reservation> getReservationsByCustomerId(Long customerId) {
        return reservationRepository.getReservationBycustomerId(customerId);
    }

    @Transactional
    public void updateReservationStatus(Reservation reservation) {
        try {
            reservationRepository.saveReservation(reservation);
        } catch (OptimisticLockException e) {
            logger.error("동시에 예약 상태를 업데이트하는 중입니다. ReservationId: {}", reservation.getId(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 예약 상태를 업데이트하는 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Transactional
    public List<Reservation> getExpiredReservations(LocalDateTime currentTime) {
        return reservationRepository.getExpiredReservations(currentTime);
    }
}
