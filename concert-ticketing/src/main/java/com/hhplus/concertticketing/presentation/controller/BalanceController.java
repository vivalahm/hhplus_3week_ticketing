package com.hhplus.concertticketing.presentation.controller;

import com.hhplus.concertticketing.business.service.BalanceService;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.presentation.dto.request.BalanceRequestDto;
import com.hhplus.concertticketing.presentation.dto.response.BalanceResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BalanceController {
    @Autowired
    private BalanceService balanceService;

    @PatchMapping("/balance/charge")
    public ResponseEntity<BalanceResponseDto> chargeBalance(@RequestBody BalanceRequestDto request) {
        if (request.getUserId() == null || request.getAmount() == null) {
            throw new CustomException("400", "Missing or invalid parameters");
        }
        try {
            return ResponseEntity.ok(balanceService.chargeBalance(request));
        } catch (Exception e) {
            throw new CustomException("500", "Internal server error");
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponseDto> getBalance(@RequestParam Long userId) {
        if (userId == null) {
            throw new CustomException("400", "Missing or invalid userId");
        }
        try {
            return ResponseEntity.ok(balanceService.getBalance(userId));
        } catch (Exception e) {
            throw new CustomException("500", "Internal server error");
        }
    }
}