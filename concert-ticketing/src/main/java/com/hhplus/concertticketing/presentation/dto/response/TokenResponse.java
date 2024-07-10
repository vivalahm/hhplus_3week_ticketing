package com.hhplus.concertticketing.presentation.dto.response;

import java.time.LocalDateTime;

public class TokenResponse {
    private String tokenValue;
    private String status;
    private LocalDateTime expiresAt;

    public TokenResponse(String tokenValue, String status, LocalDateTime expiresAt) {
        this.tokenValue = tokenValue;
        this.status = status;
        this.expiresAt = expiresAt;
    }
}
