package com.hhplus.concertticketing.Interfaces.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {
    private Long customerId;
    private Long concertId;
}
