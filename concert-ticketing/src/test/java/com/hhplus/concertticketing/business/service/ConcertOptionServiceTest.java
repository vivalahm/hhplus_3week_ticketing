package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.repository.ConcertOptionRepository;
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

public class ConcertOptionServiceTest {

    @Mock
    private ConcertOptionRepository concertOptionRepository;

    @InjectMocks
    private ConcertOptionService concertOptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("콘서트 옵션 저장 시 반환된 객체가 저장된 객체인지 확인")
    void saveConcertOption_ShouldReturnSavedConcertOption() {
        ConcertOption concertOption = new ConcertOption();
        when(concertOptionRepository.saveConcertOption(concertOption)).thenReturn(concertOption);

        ConcertOption savedConcertOption = concertOptionService.saveConcertOption(concertOption);

        assertNotNull(savedConcertOption);
        verify(concertOptionRepository, times(1)).saveConcertOption(concertOption);
    }

    @Test
    @DisplayName("ID로 콘서트 옵션을 조회하여 옵션이 존재할 때 반환된 객체 확인")
    void getConcertOptionById_ShouldReturnConcertOption_WhenFound() {
        ConcertOption concertOption = new ConcertOption();
        when(concertOptionRepository.getConcertOptionById(1L)).thenReturn(Optional.of(concertOption));

        ConcertOption foundConcertOption = concertOptionService.getConcertOptionById(1L);

        assertNotNull(foundConcertOption);
        verify(concertOptionRepository, times(1)).getConcertOptionById(1L);
    }

    @Test
    @DisplayName("ID로 콘서트 옵션을 조회할 때 옵션이 존재하지 않으면 예외 발생 확인")
    void getConcertOptionById_ShouldThrowException_WhenNotFound() {
        when(concertOptionRepository.getConcertOptionById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            concertOptionService.getConcertOptionById(1L);
        });

        assertEquals("해당 콘서트 옵션을 발견못했습니다.", exception.getMessage());
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

        List<ConcertOption> foundConcertOptions = concertOptionService.getAvailableConcertOptions(1L, now);

        assertNotNull(foundConcertOptions);
        assertFalse(foundConcertOptions.isEmpty());
        verify(concertOptionRepository, times(1)).getAllAvailableDatesByConcertId(1L, now);
    }

    @Test
    @DisplayName("예약 가능한 콘서트 옵션을 조회할 때 옵션이 존재하지 않으면 예외 발생 확인")
    void getAvailableConcertOptions_ShouldThrowException_WhenNotAvailable() {
        LocalDateTime now = LocalDateTime.now();

        when(concertOptionRepository.getAllAvailableDatesByConcertId(1L, now)).thenReturn(Arrays.asList());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            concertOptionService.getAvailableConcertOptions(1L, now);
        });

        assertEquals("예약가능한 콘서트 옵션이 없습니다.", exception.getMessage());
        verify(concertOptionRepository, times(1)).getAllAvailableDatesByConcertId(1L, now);
    }

    @Test
    @DisplayName("ID로 콘서트 옵션 삭제 확인")
    void deleteConcertOptionById_ShouldDeleteConcertOption() {
        concertOptionService.deleteConcertOptionById(1L);

        verify(concertOptionRepository, times(1)).deleteConcertOption(1L);
    }
}
