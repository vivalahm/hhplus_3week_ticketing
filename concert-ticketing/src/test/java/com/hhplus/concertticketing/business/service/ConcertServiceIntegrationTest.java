package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Concert;
import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.model.SeatStatus;
import com.hhplus.concertticketing.business.repository.ConcertOptionRepository;
import com.hhplus.concertticketing.business.repository.ConcertRepository;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({ConcertService.class, SeatService.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ConcertServiceIntegrationTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ConcertOptionRepository concertOptionRepository;

    @BeforeEach
    void setUp() {
        // 초기 설정이 필요한 경우 여기서 수행
    }

    @Test
    @DisplayName("좌석 잠금 시 잠금된 좌석 객체 반환 통합 테스트")
    void lockSeat_ShouldReturnLockedSeat_WhenSeatIsAvailable() {
        ConcertOption concertOption = new ConcertOption();
        concertOption = concertOptionRepository.saveConcertOption(concertOption);

        Seat seat = new Seat();
        seat.setConcertOptionId(concertOption.getId());
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.AVAILABLE);
        seat = seatRepository.saveSeat(seat);

        Seat lockedSeat = concertService.lockSeat(concertOption.getId(), seat.getId());

        assertNotNull(lockedSeat);
        assertEquals(SeatStatus.LOCKED, lockedSeat.getStatus());
    }

    @Test
    @DisplayName("좌석 해제 시 좌석 상태가 AVAILABLE로 업데이트되는지 통합 테스트")
    void unlockSeat_ShouldUpdateSeatStatusToAvailable() {
        Seat seat = new Seat();
        seat.setStatus(SeatStatus.LOCKED);
        seat = seatRepository.saveSeat(seat);

        concertService.unlockSeat(seat.getId());

        Seat updatedSeat = seatRepository.getSeatById(seat.getId()).orElseThrow();
        assertEquals(SeatStatus.AVAILABLE, updatedSeat.getStatus());
    }

    @Test
    @DisplayName("좌석 예약 시 좌석 상태가 RESERVED로 업데이트되는지 통합 테스트")
    void reserveSeat_ShouldUpdateSeatStatusToReserved() {
        Seat seat = new Seat();
        seat.setStatus(SeatStatus.LOCKED);
        seat = seatRepository.saveSeat(seat);

        concertService.reserveSeat(seat.getId());

        Seat updatedSeat = seatRepository.getSeatById(seat.getId()).orElseThrow();
        assertEquals(SeatStatus.RESERVED, updatedSeat.getStatus());
    }

    @Test
    @DisplayName("예약 가능한 좌석 목록 조회 시 좌석 목록 반환 통합 테스트")
    void getAvailableSeats_ShouldReturnAvailableSeats() {
        ConcertOption concertOption = new ConcertOption();
        concertOption = concertOptionRepository.saveConcertOption(concertOption);

        Seat seat1 = new Seat();
        seat1.setConcertOptionId(concertOption.getId());
        seat1.setStatus(SeatStatus.AVAILABLE);
        Seat seat2 = new Seat();
        seat2.setConcertOptionId(concertOption.getId());
        seat2.setStatus(SeatStatus.AVAILABLE);

        seatRepository.saveSeat(seat1);
        seatRepository.saveSeat(seat2);

        List<Seat> availableSeats = concertService.getAvailableSeats(concertOption.getId());

        assertNotNull(availableSeats);
        assertEquals(2, availableSeats.size());
    }

    @Test
    @DisplayName("콘서트 옵션 저장 시 반환된 객체가 저장된 객체인지 통합 테스트")
    void saveConcertOption_ShouldReturnSavedConcertOption() {
        ConcertOption concertOption = new ConcertOption();
        ConcertOption savedConcertOption = concertService.saveConcertOption(concertOption);

        assertNotNull(savedConcertOption);
    }

    @Test
    @DisplayName("ID로 콘서트 옵션을 조회하여 옵션이 존재할 때 반환된 객체 통합 테스트")
    void getConcertOptionById_ShouldReturnConcertOption_WhenFound() {
        ConcertOption concertOption = new ConcertOption();
        concertOption = concertOptionRepository.saveConcertOption(concertOption);

        ConcertOption foundConcertOption = concertService.getConcertOptionById(concertOption.getId());

        assertNotNull(foundConcertOption);
    }

    @Test
    @DisplayName("예약 가능한 콘서트 옵션들을 조회하여 옵션이 존재할 때 반환된 객체 목록 통합 테스트")
    void getAvailableConcertOptions_ShouldReturnConcertOptions_WhenAvailable() {
        LocalDateTime now = LocalDateTime.now();
        Concert concert = new Concert("Concert Title");
        concert = concertRepository.saveConcert(concert);

        ConcertOption concertOption1 = new ConcertOption();
        concertOption1.setConcertId(concert.getId());
        concertOption1.setConcertDate(now.plusDays(1));
        concertOption1.setIsAvailable(true);
        concertOption1.setPrice(100.0);

        ConcertOption concertOption2 = new ConcertOption();
        concertOption2.setConcertId(concert.getId());
        concertOption2.setConcertDate(now.plusDays(2));
        concertOption2.setIsAvailable(true);
        concertOption2.setPrice(150.0);

        concertOptionRepository.saveConcertOption(concertOption1);
        concertOptionRepository.saveConcertOption(concertOption2);

        List<ConcertOption> foundConcertOptions = concertService.getAvailableConcertOptions(concert.getId(), now);

        assertNotNull(foundConcertOptions);
        assertFalse(foundConcertOptions.isEmpty());
        assertEquals(2, foundConcertOptions.size());
    }
}