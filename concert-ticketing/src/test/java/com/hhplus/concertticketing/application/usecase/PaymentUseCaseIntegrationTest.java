package com.hhplus.concertticketing.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hhplus.concertticketing.domain.model.*;
import com.hhplus.concertticketing.domain.repository.*;
import com.hhplus.concertticketing.Interfaces.presentation.dto.request.PaymentRequest;
import com.hhplus.concertticketing.Interfaces.presentation.dto.response.PaymentResponse;
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
public class PaymentUseCaseIntegrationTest {

    @Autowired
    private PaymentUseCase paymentUseCase;

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

    private Reservation reservation;
    private Customer customer;
    private Seat seat;
    private Token token;
    private ConcertOption concertOption;

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

        // Initialize Reservation
        reservation = new Reservation();
        reservation.setConcertOptionId(concertOption.getId());
        reservation.setCustomerId(customer.getId());
        reservation.setSeatId(seat.getId());
        reservation.setStatus(ReservationStatus.RESERVING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusHours(1));
        reservation = reservationRepository.saveReservation(reservation);

        // Initialize Token
        token = new Token();
        token.setConcertId(concertOption.getConcertId());
        token.setCustomerId(customer.getId());
        token.setTokenValue("token-value");
        token.setStatus(TokenStatus.ACTIVE);
        token = tokenRepository.saveToken(token);
    }

    @Test
    @DisplayName("결제 처리 테스트")
    void processPayment_ShouldSucceed() throws JsonProcessingException {
        PaymentRequest request = new PaymentRequest();
        request.setReservationId(reservation.getId());

        PaymentResponse response = paymentUseCase.processPayment(request);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());

        Reservation updatedReservation = reservationRepository.getReservationById(reservation.getId()).orElseThrow();
        assertEquals(ReservationStatus.PAID, updatedReservation.getStatus());

        Customer updatedCustomer = customerRepository.getCustomerById(customer.getId()).orElseThrow();
        assertEquals(50.0, updatedCustomer.getPoint());

        Seat updatedSeat = seatRepository.getSeatById(seat.getId()).orElseThrow();
        assertEquals(SeatStatus.RESERVED, updatedSeat.getStatus());

        Token updatedToken = tokenRepository.getTokenById(token.getId()).orElseThrow();
        assertEquals(TokenStatus.EXPIRED, updatedToken.getStatus());
    }
}
