package com.hhplus.concertticketing.presentation.dto.response;

import com.hhplus.concertticketing.presentation.dto.SeatDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeatResponseDto {
    private String result;
    private String message;
    private List<SeatDto> seats;
}
