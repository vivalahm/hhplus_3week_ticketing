package com.hhplus.concertticketing.presentation.controller;

import com.hhplus.concertticketing.application.usecase.ConcertUseCase;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.presentation.dto.response.ConcertOptionResponse;
import com.hhplus.concertticketing.presentation.dto.response.SeatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Concert Controller", description = "API for managing concerts")
public class ConcertController {
    @Autowired
    private ConcertUseCase concertUseCase;

    @GetMapping("/{concertId}/available-dates")
    @Operation(summary = "Get available dates for a concert", description = "Returns available dates for a given concert ID")
    public ResponseEntity<ConcertOptionResponse> getAvailableDates(
            @PathVariable @Parameter(description = "ID of the concert") Long concertId,
            @RequestParam @Parameter(description = "Token value for authorization") String tokenValue) {
        if (tokenValue == null || tokenValue.isEmpty()) {
            throw new CustomException("401", "Invalid or expired token");
        }
        ConcertOptionResponse response = new ConcertOptionResponse();
        response.setResult("200");
        response.setMessage("Success");
        response.setConcertOptions(concertUseCase.getAvailableOptions(concertId, tokenValue));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{concertOptionId}/available-seats")
    @Operation(summary = "Get available seats for a concert option", description = "Returns available seats for a given concert option ID")
    public ResponseEntity<SeatResponse> getAvailableSeats(
            @PathVariable @Parameter(description = "ID of the concert option") Long concertOptionId,
            @RequestParam @Parameter(description = "Token value for authorization") String tokenValue) {
        if (tokenValue == null || tokenValue.isEmpty()) {
            throw new CustomException("401", "Invalid or expired token");
        }
        SeatResponse response = new SeatResponse();
        response.setResult("200");
        response.setMessage("Success");
        response.setSeats(concertUseCase.getAvailableSeats(concertOptionId, tokenValue));
        return ResponseEntity.ok(response);
    }
}