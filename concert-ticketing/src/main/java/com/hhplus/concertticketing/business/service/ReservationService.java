package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Reservation;
import com.hhplus.concertticketing.business.repository.ReservationRepository;
import com.hhplus.concertticketing.presentation.dto.request.ReservationRequestDto;
import com.hhplus.concertticketing.presentation.dto.response.ReservationResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation reserveTicket(Long customerId, Long concertOptionId,Long seatId){
        Reservation reservation = new Reservation();
        reservation.setCustomerId(customerId);
        reservation.setConcertOptionId(concertOptionId);
        reservation.setSeatId(seatId);
        reservation.setStatus("RESERVING");
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        return reservationRepository.saveReservation(reservation);
    }

    public Optional<Reservation> getReservationById(Long reservationId){
        return reservationRepository.getReservationById(reservationId);
    }

    public List<Reservation> getReservationsByCustomerId(Long customerId){
        return reservationRepository.getReservationBycustomerId(customerId);
    }

    public void updateReservationStatus(Reservation reservation, String status){
        reservation.setStatus(status);
        reservationRepository.saveReservation(reservation);
    }

    public List<Reservation> getExpiredReservations(LocalDateTime currentTime){
        return reservationRepository.getExpiredReservations(currentTime);
    }

}