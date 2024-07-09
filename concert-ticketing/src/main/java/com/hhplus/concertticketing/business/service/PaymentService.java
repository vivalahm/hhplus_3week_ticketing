package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.presentation.dto.request.PaymentRequestDto;
import com.hhplus.concertticketing.presentation.dto.response.PaymentResponseDto;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        // Mock 구현
        PaymentResponseDto response = new PaymentResponseDto();
        response.setResult("200");
        response.setMessage("Success");
        PaymentResponseDto.Data data = new PaymentResponseDto.Data();
        data.setPaymentId(456L);
        response.setData(data);
        return response;
    }
}