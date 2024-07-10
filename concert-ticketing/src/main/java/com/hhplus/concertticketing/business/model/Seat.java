package com.hhplus.concertticketing.business.model;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_option_id", nullable = false)
    private ConcertOption concertOption;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private String status; // AVAILABLE, LOCKED, RESERVED
}
