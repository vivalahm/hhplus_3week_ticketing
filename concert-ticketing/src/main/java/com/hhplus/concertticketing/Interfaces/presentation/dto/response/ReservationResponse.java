package com.hhplus.concertticketing.Interfaces.presentation.dto.response;

import com.hhplus.concertticketing.domain.model.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationResponse {
    private Long reservationId;
    private ReservationStatus status;
    private LocalDateTime expiresAt;

    public ReservationResponse(Long reservationId, ReservationStatus status, LocalDateTime expiresAt) {
        this.reservationId = reservationId;
        this.status = status;
        this.expiresAt = expiresAt;
    }
}
