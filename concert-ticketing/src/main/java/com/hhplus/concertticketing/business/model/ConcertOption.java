package com.hhplus.concertticketing.business.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "concert_option")
public class ConcertOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "concer_id", nullable = false)
    private Concert concert;

    @Column(nullable = false)
    private Integer maxSeatNumber;

    @Column(nullable = false)
    private LocalDateTime concertDate;

    @Column(nullable = false)
    private Double price;
}
