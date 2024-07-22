package com.hhplus.concertticketing.adaptor.presentation.dto.response;

import com.hhplus.concertticketing.business.model.Seat;
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
