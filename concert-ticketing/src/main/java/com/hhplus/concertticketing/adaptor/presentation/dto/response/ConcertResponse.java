package com.hhplus.concertticketing.adaptor.presentation.dto.response;

import com.hhplus.concertticketing.business.model.Concert;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConcertResponse {
    private String result;
    private String message;
    private Concert concert;
}
