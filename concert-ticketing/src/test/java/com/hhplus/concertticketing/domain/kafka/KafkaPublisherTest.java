package com.hhplus.concertticketing.domain.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.domain.model.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

public class KafkaPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaPublisher kafkaPublisher;

    private Reservation reservation;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 토픽 이름을 명시적으로 설정
        kafkaPublisher = new KafkaPublisher(kafkaTemplate, "payment_topic", objectMapper);

        reservation = new Reservation();
        reservation.setId(3L);
        reservation.setCustomerId(2L);
        reservation.setSeatId(640000L);
        reservation.setConcertOptionId(41150L);
        reservation.setStatus(ReservationStatus.PAID);
    }

    @Test
    public void testPublishPaymentInfo() throws Exception {
        // 목 객체를 통해 JSON 직렬화 과정 모의
        String messageAsJson = "{\"id\":3,\"customerId\":2,\"seatId\":640000,\"concertOptionId\":41150,\"status\":\"PAID\"}";
        when(objectMapper.writeValueAsString(reservation)).thenReturn(messageAsJson);

        kafkaPublisher.publishPaymentInfo(reservation);

        // JSON 직렬화가 수행되었는지 확인
        verify(objectMapper, times(1)).writeValueAsString(reservation);

        // KafkaTemplate의 send 메서드가 올바른 파라미터로 호출되었는지 확인
        verify(kafkaTemplate, times(1)).send(eq("payment_topic"), eq("3"), eq(messageAsJson));
    }
}