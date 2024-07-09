package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.presentation.dto.request.BalanceRequestDto;
import com.hhplus.concertticketing.presentation.dto.response.BalanceResponseDto;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {
    public BalanceResponseDto chargeBalance(BalanceRequestDto request) {
        // Mock 구현
        BalanceResponseDto response = new BalanceResponseDto();
        response.setResult("200");
        response.setMessage("Success");
        response.setBalance(5000.00); // 예시 잔액
        return response;
    }

    public BalanceResponseDto getBalance(Long userId) {
        // Mock 구현
        BalanceResponseDto response = new BalanceResponseDto();
        response.setResult("200");
        response.setMessage("Success");
        response.setBalance(5000.00); // 예시 잔액
        return response;
    }
}