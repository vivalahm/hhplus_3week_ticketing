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

    @Column(nullable = false)
    private Long concertId;

    @Column(nullable = false, unique = true)
    private String tokenValue; //검증을 위한 토큰 값

    @Column(nullable = false)
    private String status; // ACTIVE, WAITING, EXPIRED

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
