package com.hhplus.concertticketing.business.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "token")
public class Token implements Serializable {

    private static final long serialVersionUID = 2L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private Long concertId;

    private String tokenValue; //검증을 위한 토큰 값

    @Enumerated(EnumType.STRING)
    private TokenStatus status; // ACTIVE, WAITING, EXPIRED
}

