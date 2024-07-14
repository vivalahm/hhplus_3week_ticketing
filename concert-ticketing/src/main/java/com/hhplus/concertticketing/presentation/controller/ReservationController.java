package com.hhplus.concertticketing.presentation.controller;

import com.hhplus.concertticketing.application.usecase.ReservationUseCase;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.presentation.dto.request.ReservationRequest;
import com.hhplus.concertticketing.presentation.dto.response.ReservationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@Tag(name = "Reservation Controller", description = "API for managing reservations")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private ReservationUseCase reservationUseCase;

    @PostMapping("/reserve")
    @Operation(summary = "Reserve a seat", description = "Reserves a seat for a given concert option")
    public ResponseEntity<ReservationResponse> reserveSeat(
            @RequestBody @Parameter(description = "ReservationRequest object containing tokenValue, concertOptionId, and seatId") ReservationRequest request) {

        logger.info("Received reservation request: {}", request);

        if (request.getTokenValue() == null || request.getTokenValue().isEmpty()) {
            logger.error("Invalid or expired token");
            throw new CustomException("401", "Invalid or expired token");
        }
        if (request.getConcertOptionId() == null || request.getSeatId() == null) {
            logger.error("Missing or invalid parameters");
            throw new CustomException("400", "Missing or invalid parameters");
        }
        try {
            ReservationResponse response = reservationUseCase.reserveTicket(request);
            logger.info("Reservation successful: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Internal server error", e);
            throw new CustomException("500", "Internal server error");
        }
    }
}