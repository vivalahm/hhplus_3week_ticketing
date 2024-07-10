package com.hhplus.concertticketing.presentation.dto.response;

import java.time.LocalDateTime;

public class ReservationResponse {
    private Long reservationId;
    private String status;
    private LocalDateTime expiresAt;

    public ReservationResponse(Long reservationId, String status, LocalDateTime expiresAt) {
        this.reservationId = reservationId;
        this.status = status;
        this.expiresAt = expiresAt;
    }
}
