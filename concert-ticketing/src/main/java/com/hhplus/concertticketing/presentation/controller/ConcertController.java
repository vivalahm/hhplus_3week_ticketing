package com.hhplus.concertticketing.presentation.controller;

import com.hhplus.concertticketing.business.service.ConcertService;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.presentation.dto.ConcertOptionDto;
import com.hhplus.concertticketing.presentation.dto.response.ConcertOptionResponseDto;
import com.hhplus.concertticketing.presentation.dto.response.SeatResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConcertController {
    @Autowired
    private ConcertService concertService;

    @GetMapping("/{concertId}/available-dates")
    public ResponseEntity<ConcertOptionResponseDto> getAvailableDates(
            @PathVariable Long concertId, @RequestParam String token) {
        if (token == null || token.isEmpty()) {
            throw new CustomException("401", "Invalid or expired token");
        }
        ConcertOptionResponseDto response = new ConcertOptionResponseDto();
        response.setResult("200");
        response.setMessage("Success");
        response.setConcertOptions(concertService.getAvailableDates(concertId, token));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{concertOptionId}/available-seats")
    public ResponseEntity<SeatResponseDto> getAvailableSeats(
            @PathVariable Long concertOptionId, @RequestParam String token) {
        if (token == null || token.isEmpty()) {
            throw new CustomException("401", "Invalid or expired token");
        }
        SeatResponseDto response = new SeatResponseDto();
        response.setResult("200");
        response.setMessage("Success");
        response.setSeats(concertService.getAvailableSeats(concertOptionId, token));
        return ResponseEntity.ok(response);
    }
}