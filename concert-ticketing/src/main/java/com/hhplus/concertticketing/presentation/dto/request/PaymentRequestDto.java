package com.hhplus.concertticketing.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private String token;
    private Long reservationId;
    private Double amount;
}
