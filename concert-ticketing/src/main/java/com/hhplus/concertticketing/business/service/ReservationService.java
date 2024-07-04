package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.presentation.dto.request.ReservationRequestDto;
import com.hhplus.concertticketing.presentation.dto.response.ReservationResponseDto;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    public ReservationResponseDto reserveSeat(ReservationRequestDto request) {
        // Mock 구현
        ReservationResponseDto response = new ReservationResponseDto();
        response.setResult("200");
        response.setMessage("Success");
        ReservationResponseDto.Data data = new ReservationResponseDto.Data();
        data.setReservationId(123L);
        response.setData(data);
        return response;
    }
}