package com.hhplus.concertticketing.presentation.dto.response;

import com.hhplus.concertticketing.presentation.dto.ConcertOptionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConcertOptionResponseDto {
    private String result;
    private String message;
    private List<ConcertOptionDto> concertOptions;
}
