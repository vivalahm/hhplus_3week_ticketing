package com.hhplus.concertticketing.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
    private String tokenValue;
    private Long concertOptionId;
    private Long seatId;
}
