package com.hhplus.concertticketing.Interfaces.presentation.dto.response;

import com.hhplus.concertticketing.domain.model.TokenStatus;
import lombok.Getter;
import lombok.Setter;

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
