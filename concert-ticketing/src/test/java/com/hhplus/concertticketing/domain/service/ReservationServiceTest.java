package com.hhplus.concertticketing.domain.service;

import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.domain.model.ReservationStatus;
import com.hhplus.concertticketing.domain.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("티켓 예약 시 저장된 예약 객체 반환 테스트")
    void reserveTicket_ShouldReturnSavedReservation() {
        Long customerId = 1L;
        Long concertOptionId = 1L;
        Long seatId = 1L;
        Reservation reservation = new Reservation();
        reservation.setCustomerId(customerId);
        reservation.setConcertOptionId(concertOptionId);
        reservation.setSeatId(seatId);
        reservation.setStatus(ReservationStatus.RESERVING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        when(reservationRepository.saveReservation(any(Reservation.class))).thenReturn(reservation);

        Reservation savedReservation = reservationService.reserveTicket(customerId, concertOptionId, seatId);

        assertNotNull(savedReservation);
        assertEquals(customerId, savedReservation.getCustomerId());
        assertEquals(concertOptionId, savedReservation.getConcertOptionId());
        assertEquals(seatId, savedReservation.getSeatId());
        assertEquals(ReservationStatus.RESERVING, savedReservation.getStatus());
        verify(reservationRepository, times(1)).saveReservation(any(Reservation.class));
    }

    @Test
    @DisplayName("예약 ID로 조회하여 예약 객체가 존재할 때 반환 테스트")
    void getReservationById_ShouldReturnReservation_WhenFound() {
        Long reservationId = 1L;
        Reservation reservation = new Reservation();
        when(reservationRepository.getReservationById(reservationId)).thenReturn(Optional.of(reservation));

        Optional<Reservation> foundReservation = reservationService.getReservationById(reservationId);

        assertTrue(foundReservation.isPresent());
        verify(reservationRepository, times(1)).getReservationById(reservationId);
    }

    @Test
    @DisplayName("예약 ID로 조회할 때 예약 객체가 존재하지 않으면 빈 Optional 반환 테스트")
    void getReservationById_ShouldReturnEmpty_WhenNotFound() {
        Long reservationId = 1L;
        when(reservationRepository.getReservationById(reservationId)).thenReturn(Optional.empty());

        Optional<Reservation> foundReservation = reservationService.getReservationById(reservationId);

        assertFalse(foundReservation.isPresent());
        verify(reservationRepository, times(1)).getReservationById(reservationId);
    }

    @Test
    @DisplayName("고객 ID로 예약 목록 조회 시 예약 목록 반환 테스트")
    void getReservationsByCustomerId_ShouldReturnReservations() {
        Long customerId = 1L;
        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);

        when(reservationRepository.getReservationBycustomerId(customerId)).thenReturn(reservations);

        List<Reservation> foundReservations = reservationService.getReservationsByCustomerId(customerId);

        assertNotNull(foundReservations);
        assertEquals(2, foundReservations.size());
        verify(reservationRepository, times(1)).getReservationBycustomerId(customerId);
    }

    @Test
    @DisplayName("예약 상태 업데이트 시 저장 메서드 호출 테스트")
    void updateReservationStatus_ShouldUpdateReservation() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.PAID);

        reservationService.updateReservationStatus(reservation);

        verify(reservationRepository, times(1)).saveReservation(reservation);
    }

    @Test
    @DisplayName("현재 시간 이후 만료된 예약 목록 조회 테스트")
    void getExpiredReservations_ShouldReturnExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);

        when(reservationRepository.getExpiredReservations(now)).thenReturn(reservations);

        List<Reservation> expiredReservations = reservationService.getExpiredReservations(now);

        assertNotNull(expiredReservations);
        assertEquals(2, expiredReservations.size());
        verify(reservationRepository, times(1)).getExpiredReservations(now);
    }
}