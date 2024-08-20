package com.hhplus.concertticketing.Interfaces.presentation.controller;

import com.hhplus.concertticketing.application.usecase.ReservationUseCase;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import com.hhplus.concertticketing.Interfaces.presentation.dto.request.ReservationRequest;
import com.hhplus.concertticketing.Interfaces.presentation.dto.response.ReservationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@Tag(name = "예약 컨트롤러", description = "예약 관리를 위한 API")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private ReservationUseCase reservationUseCase;

    @PostMapping("/reserve")
    @Operation(summary = "좌석 예약", description = "주어진 콘서트 옵션에 대해 좌석을 예약합니다.")
    public ResponseEntity<ReservationResponse> reserveSeat(
            @RequestBody @Parameter(description = "토큰 값, 콘서트 옵션 ID, 좌석 ID를 포함하는 ReservationRequest 객체") ReservationRequest request, HttpServletRequest httpServletRequest) {

        logger.info("예약 요청 수신: {}", request);

        request.setTokenValue(httpServletRequest.getHeader("Authorization"));

        if (request.getConcertOptionId() == null || request.getSeatId() == null) {
            logger.error("필수 파라미터가 누락되었거나 잘못되었습니다.");
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        try {
            ReservationResponse response = reservationUseCase.reserveTicket(request);
            logger.info("예약 성공: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("서버 내부 오류", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}