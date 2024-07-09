package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.presentation.dto.ConcertOptionDto;
import com.hhplus.concertticketing.presentation.dto.SeatDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConcertService {
    public List<ConcertOptionDto> getAvailableDates(Long concertId, String token) {
        // Mock 구현
        return List.of(
                new ConcertOptionDto(1L, "2024-07-04"),
                new ConcertOptionDto(2L, "2024-07-05")
        );
    }

    public List<SeatDto> getAvailableSeats(Long concertOptionId, String token) {
        // Mock 구현
        return List.of(
                new SeatDto(1L, "A1", "열림"),
                new SeatDto(2L, "A2", "열림")
        );
    }
}
