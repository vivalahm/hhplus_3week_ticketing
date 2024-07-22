package com.hhplus.concertticketing.adaptor.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {
    private String result;
    private String message;
    private String status;
    private LocalDateTime paymentDate;

    public PaymentResponse(String status, LocalDateTime paymentDate) {
        this.status = status;
        this.paymentDate = paymentDate;
    }
}
