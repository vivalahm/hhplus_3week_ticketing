package com.hhplus.concertticketing.infrastructure.kafka.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentJpaOutboxWritter extends JpaRepository<PaymentOutboxEvent, Long> {
    List<PaymentOutboxEvent> findAllByStatus(String status);
}
