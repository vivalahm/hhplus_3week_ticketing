package com.hhplus.concertticketing.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatDto {
    private Long seatId;
    private String seatNumber;
    private String status;

    public SeatDto(Long seatId, String seatNumber, String status) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.status = status;
    }
}
