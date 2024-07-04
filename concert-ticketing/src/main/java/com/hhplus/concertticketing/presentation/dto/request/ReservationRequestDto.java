package com.hhplus.concertticketing.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequestDto {
    private String token;
    private Long concertOptionId;
    private Long seatId;
    private Long userId;
}
