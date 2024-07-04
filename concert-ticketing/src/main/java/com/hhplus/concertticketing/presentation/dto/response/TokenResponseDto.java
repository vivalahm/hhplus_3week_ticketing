package com.hhplus.concertticketing.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    private String result;
    private String message;
    private Data data;

    @Getter
    @Setter
    public static class Data {
        private String token;
        private int queuePosition;
        private String expiresAt;

    }

}
