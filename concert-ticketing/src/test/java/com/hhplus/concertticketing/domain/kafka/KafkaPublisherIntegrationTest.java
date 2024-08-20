package com.hhplus.concertticketing.domain.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.domain.model.ReservationStatus;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"payment_topic"})
public class KafkaPublisherIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private KafkaPublisher kafkaPublisher;
    private Reservation reservation;

    private String receivedMessage;

    @BeforeEach
    public void setUp() {
        kafkaPublisher = new KafkaPublisher(kafkaTemplate, "payment_topic", objectMapper);

        reservation = new Reservation();
        reservation.setId(3L);
        reservation.setCustomerId(2L);
        reservation.setSeatId(640000L);
        reservation.setConcertOptionId(41150L);
        reservation.setStatus(ReservationStatus.PAID);
    }

    @KafkaListener(topics = "payment_topic", groupId = "test-group")
    public void listen(ConsumerRecord<String, String> record) {
        receivedMessage = record.value();
    }

    @Test
    public void testPublishAndConsumePaymentInfo() throws Exception {
        // JSON 직렬화
        String messageAsJson = objectMapper.writeValueAsString(reservation);

        // 메시지 발행
        kafkaPublisher.publishPaymentInfo(reservation);

        // 2초간 대기하여 메시지 소비를 대기
        Thread.sleep(2000);

        // 메시지 검증
        assertThat(receivedMessage).isEqualTo(messageAsJson);
    }
}