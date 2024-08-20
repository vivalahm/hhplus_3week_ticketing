package com.hhplus.concertticketing.domain.service;

import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.domain.model.ReservationStatus;
import com.hhplus.concertticketing.domain.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        reservation.setCustomerId(1L);
        reservation.setConcertOptionId(1L);
        reservation.setSeatId(1L);
        reservation.setStatus(ReservationStatus.RESERVING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        reservationRepository.saveReservation(reservation);
    }

    @Test
    @DisplayName("티켓 예약 통합 테스트")
    void reserveTicket_ShouldReturnSavedReservation() {
        Long customerId = 1L;
        Long concertOptionId = 1L;
        Long seatId = 1L;

        Reservation savedReservation = reservationService.reserveTicket(customerId, concertOptionId, seatId);

        assertNotNull(savedReservation);
        assertEquals(customerId, savedReservation.getCustomerId());
        assertEquals(concertOptionId, savedReservation.getConcertOptionId());
        assertEquals(seatId, savedReservation.getSeatId());
        assertEquals(ReservationStatus.RESERVING, savedReservation.getStatus());
    }

    @Test
    @DisplayName("예약 ID로 조회 통합 테스트")
    void getReservationById_ShouldReturnReservation() {
        Optional<Reservation> foundReservation = reservationService.getReservationById(reservation.getId());

        assertTrue(foundReservation.isPresent());
        assertEquals(reservation.getId(), foundReservation.get().getId());
    }

    @Test
    @DisplayName("예약 ID로 조회 시 예약이 존재하지 않을 때 빈 Optional 반환 통합 테스트")
    void getReservationById_ShouldReturnEmpty_WhenNotFound() {
        Long nonExistentReservationId = 999L;

        Optional<Reservation> foundReservation = reservationService.getReservationById(nonExistentReservationId);

        assertFalse(foundReservation.isPresent());
    }

    @Test
    @DisplayName("고객 ID로 예약 목록 조회 통합 테스트")
    void getReservationsByCustomerId_ShouldReturnReservations() {
        List<Reservation> reservations = reservationService.getReservationsByCustomerId(reservation.getCustomerId());

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        assertEquals(reservation.getCustomerId(), reservations.get(0).getCustomerId());
    }

    @Test
    @DisplayName("예약 상태 업데이트 통합 테스트")
    void updateReservationStatus_ShouldUpdateReservation() {
        reservation.setStatus(ReservationStatus.PAID);

        reservationService.updateReservationStatus(reservation);

        Optional<Reservation> updatedReservation = reservationRepository.getReservationById(reservation.getId());
        assertTrue(updatedReservation.isPresent());
        assertEquals(ReservationStatus.PAID, updatedReservation.get().getStatus());
    }

    @Test
    @DisplayName("만료된 예약 목록 조회 통합 테스트")
    void getExpiredReservations_ShouldReturnExpiredReservations() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(6);
        List<Reservation> expiredReservations = reservationService.getExpiredReservations(now);

        assertNotNull(expiredReservations);
        assertEquals(1, expiredReservations.size());
        assertEquals(reservation.getId(), expiredReservations.get(0).getId());
    }
}