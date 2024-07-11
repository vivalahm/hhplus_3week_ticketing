package com.hhplus.concertticketing.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargePointRequest {
    private Long customerId;
    private Double amount;
}
