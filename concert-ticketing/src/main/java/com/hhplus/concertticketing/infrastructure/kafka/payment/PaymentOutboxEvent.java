package com.hhplus.concertticketing.infrastructure.kafka.payment;

import com.hhplus.concertticketing.domain.model.PaymentOutBoxEventStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_outbox")
@Getter
@Setter
public class PaymentOutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private Long aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String status = PaymentOutBoxEventStatus.PENDING.name();

    @Column(nullable = false)
    private Integer retryCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PaymentOutboxEvent() {
    }

    public PaymentOutboxEvent(String aggregateType, Long aggregateId, String eventType) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
    }
}
