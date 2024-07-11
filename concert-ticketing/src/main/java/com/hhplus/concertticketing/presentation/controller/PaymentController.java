package com.hhplus.concertticketing.presentation.controller;

import com.hhplus.concertticketing.application.usecase.PaymentUseCase;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.presentation.dto.request.PaymentRequest;
import com.hhplus.concertticketing.presentation.dto.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Payment Controller", description = "API for processing payments")
public class PaymentController {
    @Autowired
    PaymentUseCase paymentUseCase;

    @PostMapping("/pay")
    @Operation(summary = "Process payment", description = "Processes a payment for a given reservation")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody @Parameter(description = "PaymentRequest object containing reservationId and payment details") PaymentRequest request) {
        if (request.getReservationId() == null) {
            PaymentResponse response = new PaymentResponse();
            response.setResult("400");
            response.setMessage("Missing or invalid parameters");
            throw new CustomException("400", "Missing or invalid parameters");
        }
        try {
            PaymentResponse response = new PaymentResponse();
            response.setResult("200");
            response.setMessage("Success");
            response.setStatus("PAID");
            response.setPaymentDate(paymentUseCase.processPayment(request).getPaymentDate());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            PaymentResponse response = new PaymentResponse();
            response.setResult("500");
            response.setMessage("Internal server error");
            throw new CustomException("500", "Internal server error");
        }
    }
}