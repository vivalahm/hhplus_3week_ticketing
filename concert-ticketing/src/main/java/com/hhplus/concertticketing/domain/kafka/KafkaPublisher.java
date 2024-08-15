package com.hhplus.concertticketing.domain.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.model.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String PAYMENT_TOPIC;
    private final ObjectMapper objectMapper;

    public KafkaPublisher(KafkaTemplate<String, String> kafkaTemplate, @Value("${payment_topic}") String paymentTopic, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.PAYMENT_TOPIC = paymentTopic;
        this.objectMapper = objectMapper;
    }

    public void publishPaymentInfo(Reservation reservationInfo) {
        try {
            String messageAsJson = objectMapper.writeValueAsString(reservationInfo);
            String partition = String.valueOf(reservationInfo.getId());
            kafkaTemplate.send(PAYMENT_TOPIC, partition, messageAsJson);
        }catch (Exception e){
            throw new RuntimeException("예약 정보 전달에 실패하였습니다. 예약 정보: " + reservationInfo, e);
        }
    }
}
