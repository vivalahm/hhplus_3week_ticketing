package com.hhplus.concertticketing.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private Long seatId;

    private Long concertOptionId;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // RESERVING, RESERVED, CANCLED

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @Version
    private Long version;

    public void reserveTicket(Long customerId, Long concertOptionId, Long seatId){
        this.customerId = customerId;
        this.concertOptionId = concertOptionId;
        this.seatId = seatId;
        this.status = ReservationStatus.RESERVING;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
    }
}

