package com.hhplus.concertticketing.adaptor.presentation.controller;

import com.hhplus.concertticketing.adaptor.presentation.dto.response.ConcertOptionResponse;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.SeatResponse;
import com.hhplus.concertticketing.application.usecase.ConcertUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Concert Controller", description = "콘서트를 정보를 받아오기 위한 API")
public class ConcertController {
    @Autowired
    private ConcertUseCase concertUseCase;

    @GetMapping("/{concertId}/available-dates")
    @Operation(summary = "가능한 콘서트 일정을 가져오는 API", description = "콘서트ID를 주었을때 해당 콘서트에 예약 가능한 콘서트 날짜를 보여준다.")
    public ResponseEntity<ConcertOptionResponse> getAvailableDates(
            @PathVariable @Parameter(description = "콘서트의 ID") Long concertId) {
        ConcertOptionResponse response = new ConcertOptionResponse();
        response.setResult("200");
        response.setMessage("Success");
        response.setConcertOptions(concertUseCase.getAvailableOptions(concertId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{concertOptionId}/available-seats")
    @Operation(summary = "해당하는 콘서트옵션의 예약가능한 좌석 정보를 가져온다.", description = "콘서트옵션ID를 주었을 때, 예약 가능한 좌석을 보여준다.")
    public ResponseEntity<SeatResponse> getAvailableSeats(
            @PathVariable @Parameter(description = "콘서트 옵션의 ID") Long concertOptionId) {
        SeatResponse response = new SeatResponse();
        response.setResult("200");
        response.setMessage("Success");
        response.setSeats(concertUseCase.getAvailableSeats(concertOptionId));
        return ResponseEntity.ok(response);
    }
}