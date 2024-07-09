package com.hhplus.concertticketing.business.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "concert")
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    String title;

    @OneToMany(mappedBy = "concert")
    List<ConcertOption> concertOptions;
}
