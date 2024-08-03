package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.model.TokenStatus;
import com.hhplus.concertticketing.business.model.SeatStatus;
import com.hhplus.concertticketing.business.repository.ConcertOptionRepository;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import com.hhplus.concertticketing.business.repository.TokenRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ConcertUseCaseIntegrationTest {

    @Autowired
    private ConcertUseCase concertUseCase;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ConcertOptionRepository concertOptionRepository;

    @Autowired
    private SeatRepository seatRepository;

    private Token token;
    private ConcertOption concertOption;

    @BeforeEach
    void setUp() {
        // Initialize token
        token = new Token();
        token.setTokenValue("validToken");
        token.setStatus(TokenStatus.ACTIVE);
        tokenRepository.saveToken(token);

        // Initialize concert option
        concertOption = new ConcertOption();
        concertOption.setConcertId(1L); // Set the concert ID
        concertOption.setIsAvailable(true);
        concertOption.setPrice(100.0);
        concertOption.setConcertDate(LocalDateTime.now().plusDays(1));
        concertOptionRepository.saveConcertOption(concertOption);

        // Initialize seat
        Seat seat = new Seat();
        seat.setConcertOptionId(concertOption.getId());
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.saveSeat(seat);
    }

    @Test
    @DisplayName("유효한 토큰으로 사용 가능한 콘서트 옵션 가져오기")
    void getAvailableOptions_WithValidToken_ShouldReturnOptions() {
        Long concertId = 1L;
        List<ConcertOption> concertOptions = concertUseCase.getAvailableOptions(concertId);

        assertNotNull(concertOptions);
        assertFalse(concertOptions.isEmpty());
    }


    @Test
    @DisplayName("유효한 토큰으로 사용 가능한 좌석 가져오기")
    void getAvailableSeats_WithValidToken_ShouldReturnSeats() {
        Long concertOptionId = concertOption.getId();
        List<Seat> seats = concertUseCase.getAvailableSeats(concertOptionId);

        assertNotNull(seats);
        assertFalse(seats.isEmpty());
    }

}
