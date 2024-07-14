package com.hhplus.concertticketing.business.model;

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
}

