package com.hhplus.concertticketing.presentation.dto.response;

import java.time.LocalDateTime;

public class TokenResponse {
    private String token;
    private String status;
    private LocalDateTime expiresAt;
}
