package com.hhplus.concertticketing.presentation.dto.response;

import com.hhplus.concertticketing.business.model.TokenStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TokenResponse {
    private String tokenValue;
    private TokenStatus status;
    private LocalDateTime expiresAt;

    public TokenResponse(String tokenValue, TokenStatus status, LocalDateTime expiresAt) {
        this.tokenValue = tokenValue;
        this.status = status;
        this.expiresAt = expiresAt;
    }
}
