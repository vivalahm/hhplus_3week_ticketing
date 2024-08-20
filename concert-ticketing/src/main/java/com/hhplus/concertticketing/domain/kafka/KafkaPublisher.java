package com.hhplus.concertticketing.domain.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.message.PaymentMessageOutboxWritter;
import com.hhplus.concertticketing.domain.model.PaymentOutBoxEventStatus;
import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.infrastructure.kafka.payment.PaymentOutboxEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;


import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String PAYMENT_TOPIC;
    private final ObjectMapper objectMapper;
    private final PaymentMessageOutboxWritter paymentMessageOutboxWritter;

    public KafkaPublisher(KafkaTemplate<String, String> kafkaTemplate, @Value("${payment_topic}") String paymentTopic, ObjectMapper objectMapper, PaymentMessageOutboxWritter paymentMessageOutboxWritter) {
        this.kafkaTemplate = kafkaTemplate;
        this.PAYMENT_TOPIC = paymentTopic;
        this.objectMapper = objectMapper;
        this.paymentMessageOutboxWritter = paymentMessageOutboxWritter;
    }

    public void publishPaymentInfo(Reservation reservationInfo) {
        try {
            String messageAsJson = objectMapper.writeValueAsString(reservationInfo);
            String partition = String.valueOf(reservationInfo.getId());
            // Kafka 메시지를 전송하고 CompletableFuture를 사용하여 결과를 처리
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(PAYMENT_TOPIC, partition, messageAsJson);

            // 전송 성공 또는 실패를 처리하는 코드 추가
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    // 메시지 전송 성공
                    log.info("Sent message=[{}] with offset=[{}]", messageAsJson, result.getRecordMetadata().offset());
                    PaymentOutboxEvent paymentOutboxEvent = new PaymentOutboxEvent("Reservation", reservationInfo.getId(), "PaidEvent");
                    paymentOutboxEvent.setStatus(PaymentOutBoxEventStatus.SENT.name());
                    paymentMessageOutboxWritter.save(paymentOutboxEvent);
                } else {
                    // 메시지 전송 실패
                    log.error("Unable to send message=[{}] due to : {}", messageAsJson, ex.getMessage());
                    PaymentOutboxEvent paymentOutboxEvent = new PaymentOutboxEvent("Reservation", reservationInfo.getId(), "PaidEvent");
                    paymentOutboxEvent.setStatus(PaymentOutBoxEventStatus.PENDING.name());
                    paymentMessageOutboxWritter.save(paymentOutboxEvent);
                }
            });

        }catch (Exception e){
            throw new RuntimeException("예약 정보 전달에 실패하였습니다. 예약 정보: " + reservationInfo, e);
        }
    }
}
