package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.domain.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationJpaRepository;
    public ReservationRepositoryImpl(ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public Reservation saveReservation(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> getReservationById(Long id) {
        return reservationJpaRepository.findById(id);
    }

    @Override
    public List<Reservation> getReservationBycustomerId(Long customerId) {
        return reservationJpaRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Reservation> getExpiredReservations(LocalDateTime currentTime) {
        return reservationJpaRepository.findExpiredReservations(currentTime);
    }

    @Override
    public void deleteReservationById(Long id) {
        reservationJpaRepository.deleteById(id);
    }
}
