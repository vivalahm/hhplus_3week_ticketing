package com.hhplus.concertticketing.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConcertOptionDto {
    private Long concertOptionId;
    private String concertDate;

    public ConcertOptionDto(Long concertOptionId, String concertDate) {
        this.concertOptionId = concertOptionId;
        this.concertDate = concertDate;
    }
}
