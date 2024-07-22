package com.hhplus.concertticketing.adaptor.presentation.controller;

import com.hhplus.concertticketing.application.usecase.PaymentUseCase;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.PaymentRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "결제 컨트롤러", description = "결제 처리를 위한 API")
public class PaymentController {
    @Autowired
    PaymentUseCase paymentUseCase;

    @PostMapping("/pay")
    @Operation(summary = "결제 처리", description = "주어진 예약에 대해 결제를 처리합니다.")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody @Parameter(description = "예약 ID 및 결제 세부 정보를 포함하는 PaymentRequest 객체") PaymentRequest request) {
        if (request.getReservationId() == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        try {
            PaymentResponse response = new PaymentResponse();
            response.setResult("200");
            response.setMessage("성공");
            response.setStatus("PAID");
            response.setPaymentDate(paymentUseCase.processPayment(request).getPaymentDate());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}