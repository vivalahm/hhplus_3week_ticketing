package com.hhplus.concertticketing.Interfaces.presentation.outBoxProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.message.PaymentMessageOutboxWritter;
import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.domain.model.ReservationStatus;
import com.hhplus.concertticketing.domain.repository.ReservationRepository;
import com.hhplus.concertticketing.infrastructure.kafka.payment.PaymentOutboxEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"payment_topic_test"})
public class PaymentOutBoxEventProcessorIntegrationTest {

    @Mock
    private PaymentMessageOutboxWritter paymentMessageOutboxWritter;

    @Mock
    private ReservationRepository reservationRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private PaymentOutBoxEventProcessor paymentOutBoxEventProcessor;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ObjectMapper objectMapper;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        paymentOutBoxEventProcessor = new PaymentOutBoxEventProcessor(paymentMessageOutboxWritter, kafkaTemplate, reservationRepository, objectMapper);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(consumerProps, new StringDeserializer(), new StringDeserializer());
        consumer.subscribe(Collections.singleton("payment_topic_test"));
    }

    @Test
    void testProcessPaymentOutboxEvent_Success() throws Exception {
        PaymentOutboxEvent event = new PaymentOutboxEvent();
        event.setAggregateId(1L);
        event.setStatus("PENDING");

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.PAID);

        when(paymentMessageOutboxWritter.findByStatus("PENDING")).thenReturn(Collections.singletonList(event));
        when(reservationRepository.getReservationById(1L)).thenReturn(Optional.of(reservation));
        // 여기서 objectMapper가 mock 객체여야 합니다.
        when(objectMapper.writeValueAsString(any(Reservation.class))).thenReturn("{\"id\":1,\"status\":\"PAID\"}");
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(null);

        paymentOutBoxEventProcessor.processPaymentOutboxEvent();

        verify(kafkaTemplate, times(1)).send(eq("payment_topic_test"), eq("1"), anyString());
        verify(paymentMessageOutboxWritter, times(1)).save(any(PaymentOutboxEvent.class));
    }

    @Test
    void testProcessPaymentOutboxEvent_RetryAndFail() throws Exception {
        // Given
        PaymentOutboxEvent event = new PaymentOutboxEvent();
        event.setAggregateId(1L);
        event.setStatus("PENDING");
        event.setRetryCount(2); // already retried twice

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.PAID);

        when(paymentMessageOutboxWritter.findByStatus("PENDING")).thenReturn(Collections.singletonList(event));
        when(reservationRepository.getReservationById(1L)).thenReturn(Optional.of(reservation));
        // Mock 객체의 메서드를 호출해야 합니다.
        when(objectMapper.writeValueAsString(any(Reservation.class))).thenThrow(new RuntimeException("Kafka exception"));

        // When
        paymentOutBoxEventProcessor.processPaymentOutboxEvent();

        // Then
        verify(paymentMessageOutboxWritter, times(1)).save(event);
        assertEquals("FAILED", event.getStatus());
        assertEquals(3, event.getRetryCount()); // failed after max retries
    }
}