package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Reservation;
import com.hhplus.concertticketing.business.model.ReservationStatus;
import com.hhplus.concertticketing.business.repository.ReservationRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation reserveTicket(Long customerId, Long concertOptionId, Long seatId) {
        Reservation reservation = new Reservation();
        reservation.setCustomerId(customerId);
        reservation.setConcertOptionId(concertOptionId);
        reservation.setSeatId(seatId);
        reservation.setStatus(ReservationStatus.RESERVING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        try {
            return reservationRepository.saveReservation(reservation);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockException("동시에 예약을 시도하는 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Transactional
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
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockException("동시에 예약 상태를 업데이트하는 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Transactional
    public List<Reservation> getExpiredReservations(LocalDateTime currentTime) {
        return reservationRepository.getExpiredReservations(currentTime);
    }
}
