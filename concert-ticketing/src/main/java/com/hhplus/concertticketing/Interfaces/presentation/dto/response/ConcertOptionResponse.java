package com.hhplus.concertticketing.Interfaces.presentation.dto.response;

import com.hhplus.concertticketing.domain.model.ConcertOption;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConcertOptionResponse {
    private String result;
    private String message;
    private List<ConcertOption> concertOptions;
}
