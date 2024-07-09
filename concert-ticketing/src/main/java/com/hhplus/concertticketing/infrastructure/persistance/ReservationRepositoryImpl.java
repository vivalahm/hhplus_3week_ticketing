package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.business.model.Reservation;
import com.hhplus.concertticketing.business.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationJpaRepository;
    public ReservationRepositoryImpl(ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public List<Reservation> getReservationByCustomerId(Long customerId) {
        return reservationJpaRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<Reservation> getReservationByReservationId(Long ReservationId) {
        return reservationJpaRepository.findById(ReservationId);
    }
}
