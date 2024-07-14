package com.hhplus.concertticketing.presentation.controller;

import com.hhplus.concertticketing.application.usecase.CustomerUseCase;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.presentation.dto.request.ChargePointRequest;
import com.hhplus.concertticketing.presentation.dto.response.CustomerPointResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Customer Controller", description = "API for managing customers")
public class CustomerController {
    @Autowired
    private CustomerUseCase customerUseCase;

    @PatchMapping("/point/charge")
    @Operation(summary = "Charge customer balance", description = "Charges the customer's balance with the specified amount")
    public ResponseEntity<CustomerPointResponse> chargeBalance(
            @RequestBody @Parameter(description = "ChargePointRequest object containing customerId and amount") ChargePointRequest request) {
        if (request.getCustomerId() == null || request.getAmount() == null) {
            CustomerPointResponse response = new CustomerPointResponse();
            response.setResult("400");
            response.setMessage("Missing or invalid parameters");
            throw new CustomException("400", "Missing or invalid parameters");
        }
        try {
            CustomerPointResponse response = new CustomerPointResponse();
            response.setResult("200");
            response.setMessage("Success");
            response.setPoint(customerUseCase.chargePoint(request).getPoint());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CustomerPointResponse response = new CustomerPointResponse();
            response.setResult("500");
            response.setMessage("Internal server error");
            throw new CustomException("500", "Internal server error");
        }
    }

    @GetMapping("/point")
    @Operation(summary = "Get customer balance", description = "Returns the balance of the specified customer")
    public ResponseEntity<CustomerPointResponse> getBalance(
            @RequestParam @Parameter(description = "ID of the customer") Long customerId) {
        if (customerId == null) {
            CustomerPointResponse response = new CustomerPointResponse();
            response.setResult("400");
            response.setMessage("Missing or invalid userId");
            throw new CustomException("400", "Missing or invalid userId");
        }
        try {
            CustomerPointResponse response = new CustomerPointResponse();
            response.setResult("200");
            response.setMessage("Success");
            response.setPoint(customerUseCase.getPoint(customerId).getPoint());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CustomerPointResponse response = new CustomerPointResponse();
            response.setResult("500");
            response.setMessage("Internal server error");
            throw new CustomException("500", "Internal server error");
        }
    }
}