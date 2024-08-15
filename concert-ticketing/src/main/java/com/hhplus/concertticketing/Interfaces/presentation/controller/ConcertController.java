package com.hhplus.concertticketing.Interfaces.presentation.controller;

import com.hhplus.concertticketing.Interfaces.presentation.dto.request.ConcertRequest;
import com.hhplus.concertticketing.Interfaces.presentation.dto.response.ConcertOptionResponse;
import com.hhplus.concertticketing.Interfaces.presentation.dto.response.ConcertResponse;
import com.hhplus.concertticketing.Interfaces.presentation.dto.response.SeatResponse;
import com.hhplus.concertticketing.application.usecase.ConcertUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "콘서트 컨트롤러", description = "콘서트 정보를 받아오기 위한 API")
public class ConcertController {
    @Autowired
    private ConcertUseCase concertUseCase;

    @GetMapping("/{concertId}/available-dates")
    @Operation(summary = "가능한 콘서트 일정 가져오기", description = "콘서트 ID를 입력하면 해당 콘서트에 예약 가능한 콘서트 날짜를 보여줍니다.")
    public ResponseEntity<ConcertOptionResponse> getAvailableDates(
            @PathVariable("concertId") @Parameter(description = "콘서트의 ID") Long concertId) {
        ConcertOptionResponse response = new ConcertOptionResponse();
        response.setResult("200");
        response.setMessage("성공");
        response.setConcertOptions(concertUseCase.getAvailableOptions(concertId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{concertOptionId}/available-seats")
    @Operation(summary = "예약 가능한 좌석 정보 가져오기", description = "콘서트 옵션 ID를 입력하면 예약 가능한 좌석을 보여줍니다.")
    public ResponseEntity<SeatResponse> getAvailableSeats(
            @PathVariable("concertOptionId") @Parameter(description = "콘서트 옵션의 ID") Long concertOptionId) {
        SeatResponse response = new SeatResponse();
        response.setResult("200");
        response.setMessage("성공");
        response.setSeats(concertUseCase.getAvailableSeats(concertOptionId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{concertId}/saveConcert")
    @Operation(summary = "콘서트 저장하기", description = "콘서트를 저장합니다.")
    public ResponseEntity<ConcertResponse> saveConcert(
            @RequestBody ConcertRequest concertRequest) {
        ConcertResponse response = new ConcertResponse();
        response.setResult("200");
        response.setMessage("성공");
        response.setConcert(concertUseCase.saveConcert(concertRequest));
        return ResponseEntity.ok(response);
    }
}