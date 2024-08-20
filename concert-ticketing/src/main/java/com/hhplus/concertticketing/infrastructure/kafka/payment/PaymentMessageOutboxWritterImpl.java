package com.hhplus.concertticketing.infrastructure.kafka.payment;

import com.hhplus.concertticketing.domain.message.PaymentMessageOutboxWritter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentMessageOutboxWritterImpl implements PaymentMessageOutboxWritter {
    private final PaymentJpaOutboxWritter paymentJpaOutboxWritter;

    public PaymentMessageOutboxWritterImpl(PaymentJpaOutboxWritter paymentJpaOutboxWritter) {
        this.paymentJpaOutboxWritter = paymentJpaOutboxWritter;
    }

    @Override
    public PaymentOutboxEvent save(PaymentOutboxEvent message) {
        return paymentJpaOutboxWritter.save(message);
    }

    @Override
    public PaymentOutboxEvent findById(Long id) {
        return paymentJpaOutboxWritter.findById(id).orElse(null);
    }

    @Override
    public List<PaymentOutboxEvent> findByStatus(String status) {
        return paymentJpaOutboxWritter.findAllByStatus(status);
    }

    @Override
    public void complete(PaymentOutboxEvent message) {
        paymentJpaOutboxWritter.delete(message);
    }
}
