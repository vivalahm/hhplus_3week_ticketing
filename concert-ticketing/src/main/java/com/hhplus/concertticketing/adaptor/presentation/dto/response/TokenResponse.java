package com.hhplus.concertticketing.adaptor.presentation.dto.response;

import com.hhplus.concertticketing.business.model.TokenStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TokenResponse {
    private String tokenValue;
    private TokenStatus status;

    public TokenResponse(String tokenValue, TokenStatus status) {
        this.tokenValue = tokenValue;
        this.status = status;
    }
}
