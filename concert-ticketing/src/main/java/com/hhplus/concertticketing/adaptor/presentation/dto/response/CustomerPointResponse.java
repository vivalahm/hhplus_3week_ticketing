package com.hhplus.concertticketing.adaptor.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerPointResponse {
    private String result;
    private String message;
    private Double point;

    public CustomerPointResponse(Double point) {
        this.point = point;
    }
}
