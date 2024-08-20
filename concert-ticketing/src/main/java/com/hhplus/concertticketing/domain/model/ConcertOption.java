package com.hhplus.concertticketing.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "concert_option")
@Getter
@Setter
public class ConcertOption implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertId;

    private Boolean isAvailable = true;

    private LocalDateTime concertDate;

    private Double price;

    public void makeAvailable(){
        this.isAvailable = true;
    }

    public void makeNotAvailable(){
        this.isAvailable = false;
    }
}
