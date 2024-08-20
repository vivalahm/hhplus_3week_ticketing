package com.hhplus.concertticketing.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="seat")
@Getter
@Setter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertOptionId;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus status; // AVAILABLE, LOCKED, RESERVED

    @Version
    private Long version;
}

