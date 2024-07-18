package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.model.SeatStatus;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SeatServiceIntegrationTest {

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    private Seat seat;

    @BeforeEach
    void setUp() {
        seat = new Seat();
        seat.setConcertOptionId(1L);
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.saveSeat(seat);
    }

    @Test
    @DisplayName("좌석 잠금 통합 테스트")
    void lockSeat_ShouldLockSeat() {
        Seat lockedSeat = seatService.lockSeat(seat.getConcertOptionId(), seat.getId());

        assertNotNull(lockedSeat);
        assertEquals(SeatStatus.LOCKED, lockedSeat.getStatus());
    }

    @Test
    @DisplayName("좌석 잠금 해제 통합 테스트")
    void unlockSeat_ShouldUnlockSeat() {
        seatService.lockSeat(seat.getConcertOptionId(), seat.getId());
        seatService.unlockSeat(seat.getId());

        Optional<Seat> unlockedSeat = seatRepository.getSeatById(seat.getId());
        assertTrue(unlockedSeat.isPresent());
        assertEquals(SeatStatus.AVAILABLE, unlockedSeat.get().getStatus());
    }

    @Test
    @DisplayName("좌석 예약 통합 테스트")
    void reserveSeat_ShouldReserveSeat() {
        seatService.lockSeat(seat.getConcertOptionId(), seat.getId());
        seatService.reserveSeat(seat.getId());

        Optional<Seat> reservedSeat = seatRepository.getSeatById(seat.getId());
        assertTrue(reservedSeat.isPresent());
        assertEquals(SeatStatus.RESERVED, reservedSeat.get().getStatus());
    }

    @Test
    @DisplayName("낙관적 락을 이용한 동시성 좌석 잠금 테스트")
    void lockSeat_ShouldHandleOptimisticLocking() throws InterruptedException {
        Long concertOptionId = seat.getConcertOptionId();
        Long seatId = seat.getId();

        Runnable task1 = () -> {
            try {
                seatService.lockSeat(concertOptionId, seatId);
                Thread.sleep(500); // 인위적으로 지연시켜 동시성 문제를 유발
            } catch (InterruptedException | CustomException e) {
                System.out.println("Expected exception in task1: " + e.getMessage());
            }
        };

        Runnable task2 = () -> {
            try {
                seatService.lockSeat(concertOptionId, seatId);
            } catch (CustomException e) {
                System.out.println("Expected exception in task2: " + e.getMessage());
            }
        };

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);

        thread1.start();
        Thread.sleep(100); // 잠시 대기하여 첫 번째 트랜잭션이 우선 실행되도록 함
        thread2.start();

        thread1.join();
        thread2.join();

        Optional<Seat> lockedSeat = seatRepository.getSeatById(seatId);
        assertTrue(lockedSeat.isPresent());
        assertEquals(SeatStatus.LOCKED, lockedSeat.get().getStatus());
    }
}