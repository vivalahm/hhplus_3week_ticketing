package com.hhplus.concertticketing.domain.message;

import com.hhplus.concertticketing.infrastructure.kafka.payment.PaymentOutboxEvent;

import java.util.List;
import java.util.Optional;

public interface PaymentMessageOutboxWritter {
    public PaymentOutboxEvent save(PaymentOutboxEvent message);
    public PaymentOutboxEvent findById(Long id);
    public List<PaymentOutboxEvent> findByStatus(String status);
    public void complete(PaymentOutboxEvent message);
}
