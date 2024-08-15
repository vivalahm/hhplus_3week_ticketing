package com.hhplus.concertticketing.Interfaces.presentation.outBoxProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.message.PaymentMessageOutboxWritter;
import com.hhplus.concertticketing.domain.model.PaymentOutBoxEventStatus;
import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.domain.model.ReservationStatus;
import com.hhplus.concertticketing.domain.repository.ReservationRepository;
import com.hhplus.concertticketing.infrastructure.kafka.payment.PaymentOutboxEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PaymentOutBoxEventProcessor {

    @Value("${payment_topic}")
    private String PAYMENT_TOPIC;

    private final PaymentMessageOutboxWritter paymentMessageOutboxWritter;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ReservationRepository reservationRepository;
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRY_COUNT = 3;

    public PaymentOutBoxEventProcessor(PaymentMessageOutboxWritter paymentMessageOutboxWritter,
                                       KafkaTemplate<String, String> kafkaTemplate,
                                       ReservationRepository reservationRepository,
                                       ObjectMapper objectMapper) {
        this.paymentMessageOutboxWritter = paymentMessageOutboxWritter;
        this.kafkaTemplate = kafkaTemplate;
        this.reservationRepository = reservationRepository;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 5000)
    public void processPaymentOutboxEvent() {
        List<PaymentOutboxEvent> pendingEvents = paymentMessageOutboxWritter.findByStatus(PaymentOutBoxEventStatus.PENDING.name());

        for (PaymentOutboxEvent event : pendingEvents) {
            try {
                Reservation reservation = reservationRepository.getReservationById(event.getAggregateId())
                        .orElseThrow(() -> new RuntimeException("예약 정보가 존재하지 않습니다."));

                if (ReservationStatus.PAID.equals(reservation.getStatus())) {
                    String payload = objectMapper.writeValueAsString(reservation);
                    log.info("결제정보 이벤트 재전송: {}", payload);
                    kafkaTemplate.send(PAYMENT_TOPIC, String.valueOf(event.getAggregateId()), payload);
                    event.setStatus("SENT");
                    event.setRetryCount(0); // 재시도 횟수 초기화
                    paymentMessageOutboxWritter.save(event);
                }

            } catch (Exception e) {
                log.error("카프카 결제정보 이벤트 전송에 실패했습니다: {}", event, e);
                handleEventFailure(event);
            }
        }
    }

    private void handleEventFailure(PaymentOutboxEvent event) {
        int retryCount = event.getRetryCount() + 1;

        if (retryCount >= MAX_RETRY_COUNT) {
            event.setStatus("FAILED");
            event.setRetryCount(MAX_RETRY_COUNT);
            log.error("최대 재시도 횟수를 초과하여 이벤트 전송이 실패하였습니다.: {}", event);
        } else {
            event.setRetryCount(retryCount);
        }

        paymentMessageOutboxWritter.save(event);
    }
}