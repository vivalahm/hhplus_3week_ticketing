package com.hhplus.concertticketing.Interfaces.presentation.dto.response;

import com.hhplus.concertticketing.domain.model.Concert;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConcertResponse {
    private String result;
    private String message;
    private Concert concert;
}
