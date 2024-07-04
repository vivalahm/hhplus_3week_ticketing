package com.hhplus.concertticketing.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceRequestDto {
    private Long userId;
    private Double amount;
}
