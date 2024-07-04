package com.hhplus.concertticketing.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceResponseDto {
    private String result;
    private String message;
    private Double balance;
}
