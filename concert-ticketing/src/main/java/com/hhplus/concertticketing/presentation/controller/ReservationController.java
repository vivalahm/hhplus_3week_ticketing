package com.hhplus.concertticketing.presentation.controller;

import com.hhplus.concertticketing.business.service.ReservationService;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.presentation.dto.request.ReservationRequestDto;
import com.hhplus.concertticketing.presentation.dto.response.ReservationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponseDto> reserveSeat(@RequestBody ReservationRequestDto request) {
        if (request.getToken() == null || request.getToken().isEmpty()) {
            throw new CustomException("401", "Invalid or expired token");
        }
        if (request.getConcertOptionId() == null || request.getSeatId() == null || request.getUserId() == null) {
            throw new CustomException("400", "Missing or invalid parameters");
        }
        try {
            return ResponseEntity.ok(reservationService.reserveSeat(request));
        } catch (Exception e) {
            throw new CustomException("500", "Internal server error");
        }
    }
}