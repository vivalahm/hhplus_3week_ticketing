package com.hhplus.concertticketing.adaptor.presentation.dto.response;

import com.hhplus.concertticketing.business.model.TokenStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenStatusResponse {
    private TokenStatus status;
    private Long Position;

    public TokenStatusResponse(TokenStatus status, Long Position) {
        this.status = status;
        this.Position = Position;
    }
}
