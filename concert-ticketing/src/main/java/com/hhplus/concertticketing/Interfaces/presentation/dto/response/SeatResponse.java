package com.hhplus.concertticketing.Interfaces.presentation.dto.response;

import com.hhplus.concertticketing.domain.model.Seat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeatResponse {
    private String result;
    private String message;
    private List<Seat> seats;
}
