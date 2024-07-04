package com.hhplus.concertticketing.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {
    private String result;
    private String message;
    private Data data;

    @Getter
    @Setter
    public static class Data {
        private Long paymentId;
    }

}
