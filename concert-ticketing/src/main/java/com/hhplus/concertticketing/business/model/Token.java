package com.hhplus.concertticketing.business.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private Long concertId;

    private String tokenValue; //검증을 위한 토큰 값

    @Enumerated(EnumType.STRING)
    private TokenStatus status; // ACTIVE, WAITING, EXPIRED

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @Version
    private Long version;

}

