package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Seat;
import com.hhplus.concertticketing.business.model.SeatStatus;
import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import com.hhplus.concertticketing.business.repository.ConcertOptionRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
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

public class ConcertServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ConcertOptionRepository concertOptionRepository;

    @InjectMocks
    private ConcertService concertService;

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

        Seat lockedSeat = concertService.lockSeat(concertOptionId, seatId);

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
            concertService.lockSeat(concertOptionId, seatId);
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

        concertService.unlockSeat(seatId);

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
            concertService.unlockSeat(seatId);
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

        concertService.reserveSeat(seatId);

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
            concertService.reserveSeat(seatId);
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

        List<Seat> availableSeats = concertService.getAvailableSeats(concertOptionId);

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
            concertService.getAvailableSeats(concertOptionId);
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

        Seat availableSeat = concertService.getAvailableSeat(concertOptionId, seatId);

        assertEquals(seat, availableSeat);
        verify(seatRepository, times(1)).getAvailableSeat(concertOptionId, seatId);
    }

    @Test
    @DisplayName("특정 좌석이 존재하지 않을 때 예외 발생 테스트")
    void getAvailableSeat_ShouldReturnEmpty_WhenSeatIsNotAvailable() {
        Long concertOptionId = 1L;
        Long seatId = 1L;

        when(seatRepository.getAvailableSeat(concertOptionId, seatId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> {
            concertService.getAvailableSeat(concertOptionId, seatId);
        });
        verify(seatRepository, times(1)).getAvailableSeat(concertOptionId, seatId);
    }

    // ConcertOptionServiceTest methods

    @Test
    @DisplayName("콘서트 옵션 저장 시 반환된 객체가 저장된 객체인지 확인")
    void saveConcertOption_ShouldReturnSavedConcertOption() {
        ConcertOption concertOption = new ConcertOption();
        when(concertOptionRepository.saveConcertOption(concertOption)).thenReturn(concertOption);

        ConcertOption savedConcertOption = concertService.saveConcertOption(concertOption);

        assertNotNull(savedConcertOption);
        verify(concertOptionRepository, times(1)).saveConcertOption(concertOption);
    }

    @Test
    @DisplayName("ID로 콘서트 옵션을 조회하여 옵션이 존재할 때 반환된 객체 확인")
    void getConcertOptionById_ShouldReturnConcertOption_WhenFound() {
        ConcertOption concertOption = new ConcertOption();
        when(concertOptionRepository.getConcertOptionById(1L)).thenReturn(Optional.of(concertOption));

        ConcertOption foundConcertOption = concertService.getConcertOptionById(1L);

        assertNotNull(foundConcertOption);
        verify(concertOptionRepository, times(1)).getConcertOptionById(1L);
    }

    @Test
    @DisplayName("ID로 콘서트 옵션을 조회할 때 옵션이 존재하지 않으면 예외 발생 확인")
    void getConcertOptionById_ShouldThrowException_WhenNotFound() {
        when(concertOptionRepository.getConcertOptionById(1L)).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, () -> {
            concertService.getConcertOptionById(1L);
        });

        assertEquals("해당 콘서트 옵션을 발견하지 못했습니다.", exception.getMessage());
        verify(concertOptionRepository, times(1)).getConcertOptionById(1L);
    }

    @Test
    @DisplayName("예약 가능한 콘서트 옵션들을 조회하여 옵션이 존재할 때 반환된 객체 목록 확인")
    void getAvailableConcertOptions_ShouldReturnConcertOptions_WhenAvailable() {
        LocalDateTime now = LocalDateTime.now();
        ConcertOption concertOption1 = new ConcertOption();
        ConcertOption concertOption2 = new ConcertOption();
        List<ConcertOption> concertOptions = Arrays.asList(concertOption1, concertOption2);

        when(concertOptionRepository.getAllAvailableDatesByConcertId(1L, now)).thenReturn(concertOptions);

        List<ConcertOption> foundConcertOptions = concertService.getAvailableConcertOptions(1L, now);

        assertNotNull(foundConcertOptions);
        assertFalse(foundConcertOptions.isEmpty());
        verify(concertOptionRepository, times(1)).getAllAvailableDatesByConcertId(1L, now);
    }

    @Test
    @DisplayName("예약 가능한 콘서트 옵션을 조회할 때 옵션이 존재하지 않으면 예외 발생 확인")
    void getAvailableConcertOptions_ShouldThrowException_WhenNotAvailable() {
        LocalDateTime now = LocalDateTime.now();

        when(concertOptionRepository.getAllAvailableDatesByConcertId(1L, now)).thenReturn(Arrays.asList());

        CustomException exception = assertThrows(CustomException.class, () -> {
            concertService.getAvailableConcertOptions(1L, now);
        });

        assertEquals("예약 가능한 콘서트 옵션이 없습니다.", exception.getMessage());
        verify(concertOptionRepository, times(1)).getAllAvailableDatesByConcertId(1L, now);
    }

    @Test
    @DisplayName("ID로 콘서트 옵션 삭제 확인")
    void deleteConcertOptionById_ShouldDeleteConcertOption() {
        concertService.deleteConcertOptionById(1L);

        verify(concertOptionRepository, times(1)).deleteConcertOption(1L);
    }
}