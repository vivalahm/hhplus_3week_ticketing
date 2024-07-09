package com.hhplus.concertticketing.business.model;

import jakarta.persistence.*;

@Entity
@Table(name="seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "concert_option_id", nullable = false)
    private ConcertOption concertOption;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private String status; // AVAILABLE, LOCKED, RESERVED
}
