package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.presentation.dto.request.QueueRequestDto;
import com.hhplus.concertticketing.presentation.dto.response.TokenResponseDto;
import org.springframework.stereotype.Service;

@Service
public class QueueService {
    public TokenResponseDto generateToken(QueueRequestDto request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("Missing or invalid userId");
        }
        // Mock 구현
        TokenResponseDto response = new TokenResponseDto();
        response.setResult("200");
        response.setMessage("Success");
        TokenResponseDto.Data data = new TokenResponseDto.Data();
        data.setToken("randomUUID");
        data.setQueuePosition(1);
        data.setExpiresAt("2024-07-04T12:00:00");
        response.setData(data);
        return response;
    }
}