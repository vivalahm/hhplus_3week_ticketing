package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.*;
import com.hhplus.concertticketing.business.repository.*;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.ReservationRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.ReservationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReservationUseCaseIntegrationTest {

    @Autowired
    private ReservationUseCase reservationUseCase;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ConcertOptionRepository concertOptionRepository;

    private Customer customer;
    private ConcertOption concertOption;
    private Seat seat;
    private Token token;

    @BeforeEach
    void setUp() {
        // Initialize Customer
        customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(100.0);
        customer = customerRepository.saveCustomer(customer);

        // Initialize ConcertOption
        concertOption = new ConcertOption();
        concertOption.setConcertId(1L);
        concertOption.setConcertDate(LocalDateTime.now().plusDays(1));
        concertOption.setIsAvailable(true);
        concertOption.setPrice(50.0);
        concertOption = concertOptionRepository.saveConcertOption(concertOption);

        // Initialize Seat
        seat = new Seat();
        seat.setConcertOptionId(concertOption.getId());
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.AVAILABLE);
        seat = seatRepository.saveSeat(seat);

        // Initialize Token
        token = new Token();
        token.setConcertId(concertOption.getConcertId());
        token.setCustomerId(customer.getId());
        token.setTokenValue("token-value");
        token.setStatus(TokenStatus.ACTIVE);
        token = tokenRepository.saveToken(token);
    }

    @Test
    @DisplayName("티켓 예약 테스트")
    void reserveTicket_ShouldSucceed() {
        ReservationRequest request = new ReservationRequest();
        request.setTokenValue(token.getTokenValue());
        request.setConcertOptionId(concertOption.getId());
        request.setSeatId(seat.getId());

        ReservationResponse response = reservationUseCase.reserveTicket(request);

        assertNotNull(response);
        assertEquals(ReservationStatus.RESERVING, response.getStatus());

        Reservation reservation = reservationRepository.getReservationById(response.getReservationId()).orElseThrow();
        assertEquals(ReservationStatus.RESERVING, reservation.getStatus());
        assertEquals(seat.getId(), reservation.getSeatId());

        Seat updatedSeat = seatRepository.getSeatById(seat.getId()).orElseThrow();
        assertEquals(SeatStatus.LOCKED, updatedSeat.getStatus());
    }

    @Test
    @DisplayName("만료된 예약 확인 및 업데이트 테스트")
    void checkAndUpdateExpiredReservations_ShouldUpdateExpiredReservations() {
        Reservation reservation = new Reservation();
        reservation.setConcertOptionId(concertOption.getId());
        reservation.setCustomerId(customer.getId());
        reservation.setSeatId(seat.getId());
        reservation.setStatus(ReservationStatus.RESERVING);
        reservation.setCreatedAt(LocalDateTime.now().minusHours(2));
        reservation.setExpiresAt(LocalDateTime.now().minusHours(1));
        reservation = reservationRepository.saveReservation(reservation);

        reservationUseCase.checkAndUpdateExpiredReservations();

        Reservation updatedReservation = reservationRepository.getReservationById(reservation.getId()).orElseThrow();
        assertEquals(ReservationStatus.CANCLED, updatedReservation.getStatus());

        Seat updatedSeat = seatRepository.getSeatById(seat.getId()).orElseThrow();
        assertEquals(SeatStatus.AVAILABLE, updatedSeat.getStatus());
    }
}
