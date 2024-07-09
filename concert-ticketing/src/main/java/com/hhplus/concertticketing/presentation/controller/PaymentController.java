package com.hhplus.concertticketing.presentation.controller;

import com.hhplus.concertticketing.business.service.PaymentService;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.presentation.dto.request.PaymentRequestDto;
import com.hhplus.concertticketing.presentation.dto.response.PaymentResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponseDto> processPayment(@RequestBody PaymentRequestDto request) {
        if (request.getToken() == null || request.getToken().isEmpty()) {
            throw new CustomException("401", "Invalid or expired token");
        }
        if (request.getReservationId() == null || request.getAmount() == null) {
            throw new CustomException("400", "Missing or invalid parameters");
        }
        try {
            return ResponseEntity.ok(paymentService.processPayment(request));
        } catch (Exception e) {
            throw new CustomException("500", "Internal server error");
        }
    }
}
