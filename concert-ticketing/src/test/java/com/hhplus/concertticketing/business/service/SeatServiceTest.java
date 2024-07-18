package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.model.SeatStatus;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("좌석 잠금 시 잠금된 좌석 객체 반환 테스트")
    void lockSeat_ShouldReturnLockedSeat_WhenSeatIsAvailable() {
        Long concertOptionId = 1L;
        Long seatId = 1L;
        Seat seat = new Seat();
        seat.setId(seatId);
        seat.setConcertOptionId(concertOptionId);
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.AVAILABLE);

        when(seatRepository.getAvailableSeat(concertOptionId, seatId)).thenReturn(Optional.of(seat));
        when(seatRepository.saveSeat(seat)).thenReturn(seat);

        Seat lockedSeat = seatService.lockSeat(concertOptionId, seatId);

        assertNotNull(lockedSeat);
        assertEquals(SeatStatus.LOCKED, lockedSeat.getStatus());
        verify(seatRepository, times(1)).getAvailableSeat(concertOptionId, seatId);
        verify(seatRepository, times(1)).saveSeat(seat);
    }

    @Test
    @DisplayName("좌석이 존재하지 않을 때 예외 발생 테스트")
    void lockSeat_ShouldThrowException_WhenSeatIsNotAvailable() {
        Long concertOptionId = 1L;
        Long seatId = 1L;

        when(seatRepository.getAvailableSeat(concertOptionId, seatId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            seatService.lockSeat(concertOptionId, seatId);
        });

        assertEquals("좌석이 존재하지 않습니다.", exception.getMessage());
        verify(seatRepository, times(1)).getAvailableSeat(concertOptionId, seatId);
        verify(seatRepository, times(0)).saveSeat(any(Seat.class));
    }

    @Test
    @DisplayName("좌석 해제 시 좌석 상태가 AVAILABLE로 업데이트되는지 테스트")
    void unlockSeat_ShouldUpdateSeatStatusToAvailable() {
        Long seatId = 1L;
        Seat seat = new Seat();
        seat.setStatus(SeatStatus.LOCKED);

        when(seatRepository.getSeatById(seatId)).thenReturn(Optional.of(seat));

        seatService.unlockSeat(seatId);

        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
        verify(seatRepository, times(1)).getSeatById(seatId);
        verify(seatRepository, times(1)).saveSeat(seat);
    }

    @Test
    @DisplayName("좌석이 존재하지 않을 때 예외 발생 테스트")
    void unlockSeat_ShouldThrowException_WhenSeatDoesNotExist() {
        Long seatId = 1L;

        when(seatRepository.getSeatById(seatId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            seatService.unlockSeat(seatId);
        });

        assertEquals("좌석이 존재하지 않습니다.", exception.getMessage());
        verify(seatRepository, times(1)).getSeatById(seatId);
        verify(seatRepository, times(0)).saveSeat(any(Seat.class));
    }

    @Test
    @DisplayName("좌석 예약 시 좌석 상태가 RESERVED로 업데이트되는지 테스트")
    void reserveSeat_ShouldUpdateSeatStatusToReserved() {
        Long seatId = 1L;
        Seat seat = new Seat();
        seat.setStatus(SeatStatus.LOCKED);

        when(seatRepository.getSeatById(seatId)).thenReturn(Optional.of(seat));

        seatService.reserveSeat(seatId);

        assertEquals(SeatStatus.RESERVED, seat.getStatus());
        verify(seatRepository, times(1)).getSeatById(seatId);
        verify(seatRepository, times(1)).saveSeat(seat);
    }

    @Test
    @DisplayName("좌석이 존재하지 않을 때 예외 발생 테스트")
    void reserveSeat_ShouldThrowException_WhenSeatDoesNotExist() {
        Long seatId = 1L;

        when(seatRepository.getSeatById(seatId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            seatService.reserveSeat(seatId);
        });

        assertEquals("좌석이 존재하지 않습니다.", exception.getMessage());
        verify(seatRepository, times(1)).getSeatById(seatId);
        verify(seatRepository, times(0)).saveSeat(any(Seat.class));
    }

    @Test
    @DisplayName("예약 가능한 좌석 목록 조회 시 좌석 목록 반환 테스트")
    void getAvailableSeats_ShouldReturnAvailableSeats() {
        Long concertOptionId = 1L;
        Seat seat1 = new Seat();
        Seat seat2 = new Seat();
        List<Seat> seats = Arrays.asList(seat1, seat2);

        when(seatRepository.getAvailableSeats(concertOptionId)).thenReturn(seats);

        List<Seat> availableSeats = seatService.getAvailableSeats(concertOptionId);

        assertNotNull(availableSeats);
        assertEquals(2, availableSeats.size());
        verify(seatRepository, times(1)).getAvailableSeats(concertOptionId);
    }

    @Test
    @DisplayName("예약 가능한 좌석이 없을 때 예외 발생 테스트")
    void getAvailableSeats_ShouldThrowException_WhenNoAvailableSeats() {
        Long concertOptionId = 1L;

        when(seatRepository.getAvailableSeats(concertOptionId)).thenReturn(Arrays.asList());

        CustomException exception = assertThrows(CustomException.class, () -> {
            seatService.getAvailableSeats(concertOptionId);
        });

        assertEquals("가능한 좌석이 없습니다.", exception.getMessage());
        verify(seatRepository, times(1)).getAvailableSeats(concertOptionId);
    }

    @Test
    @DisplayName("예약 가능한 특정 좌석 조회 시 좌석 객체 반환 테스트")
    void getAvailableSeat_ShouldReturnAvailableSeat() {
        Long concertOptionId = 1L;
        Long seatId = 1L;
        Seat seat = new Seat();
        seat.setId(seatId);
        seat.setConcertOptionId(concertOptionId);
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.AVAILABLE);

        when(seatRepository.getAvailableSeat(concertOptionId, seatId)).thenReturn(Optional.of(seat));

        Seat availableSeat = seatService.getAvailableSeat(concertOptionId, seatId);

        assertEquals(seat, availableSeat);
        verify(seatRepository, times(1)).getAvailableSeat(concertOptionId, seatId);
    }

    @Test
    @DisplayName("특정 좌석이 존재하지 않을 때 예외 발생 테스트")
    void getAvailableSeat_ShouldReturnEmpty_WhenSeatIsNotAvailable() {
        Long concertOptionId = 1L;
        Long seatId = 1L;

        when(seatRepository.getAvailableSeat(concertOptionId, seatId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            seatService.getAvailableSeat(concertOptionId, seatId);
        });

        assertEquals("좌석이 존재하지 않습니다.", exception.getMessage());
        verify(seatRepository, times(1)).getAvailableSeat(concertOptionId, seatId);
    }
}