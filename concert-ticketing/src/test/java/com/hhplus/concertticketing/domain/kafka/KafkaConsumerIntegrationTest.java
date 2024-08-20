package com.hhplus.concertticketing.domain.kafka;

import com.hhplus.concertticketing.domain.service.ConcertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "PAYMENT-INFO" }, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@DirtiesContext
public class KafkaConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private ConcertService concertService;

    @BeforeEach
    void setUp() {
        // Mockito를 사용하여 concertService의 동작을 정의
        doNothing().when(concertService).reserveSeat(anyLong());
    }

    @Test
    void testKafkaConsumer() throws Exception {
        // given
        String message = "{\"seatId\": \"12345\"}";

        // when
        kafkaTemplate.send("PAYMENT-INFO", message);

        // then
        // 특정 시간(5000ms) 내에 ConcertService의 reserveSeat 메서드가 호출되었는지 확인
        verify(concertService, timeout(5000).times(1)).reserveSeat(12345L);
    }
}