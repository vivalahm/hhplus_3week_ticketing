package com.hhplus.concertticketing.domain.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.message.PaymentMessageOutboxWritter;
import com.hhplus.concertticketing.domain.model.PaymentOutBoxEventStatus;
import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.infrastructure.kafka.payment.PaymentOutboxEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"payment_topic"}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class KafkaPublisherIntegrationTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PaymentMessageOutboxWritter paymentMessageOutboxWritter;

    @InjectMocks
    private KafkaPublisher kafkaPublisher;

    @Captor
    private ArgumentCaptor<PaymentOutboxEvent> paymentOutboxEventCaptor;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaPublisher = new KafkaPublisher(kafkaTemplate, "payment_topic", objectMapper, paymentMessageOutboxWritter);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "payment_topic");
    }

    @Test
    void testPublishPaymentInfo_Success() throws Exception {
        // Given
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        String messageAsJson = objectMapper.writeValueAsString(reservation);

        // When
        kafkaPublisher.publishPaymentInfo(reservation);

        // Then
        ConsumerRecord<String, String> received = KafkaTestUtils.getSingleRecord(consumer, "payment_topic");

        assertEquals(messageAsJson, received.value());
        verify(paymentMessageOutboxWritter, times(1)).save(paymentOutboxEventCaptor.capture());
        PaymentOutboxEvent savedEvent = paymentOutboxEventCaptor.getValue();
        assertEquals(PaymentOutBoxEventStatus.SENT.name(), savedEvent.getStatus());
        assertEquals("Reservation", savedEvent.getAggregateType());
        assertEquals(reservation.getId(), savedEvent.getAggregateId());
        assertEquals("PaidEvent", savedEvent.getEventType());
    }

    @Test
    void testPublishPaymentInfo_Failure() throws Exception {
        // Given
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        // KafkaTemplate의 send 메서드가 예외를 던지도록 설정합니다.
        doThrow(new RuntimeException("Kafka exception"))
                .when(kafkaTemplate).send(eq("payment_topic"), eq("1"), any());

        // When & Then
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> kafkaPublisher.publishPaymentInfo(reservation));

        // 예외 메시지를 확인합니다.
        assertEquals("Kafka exception", thrownException.getMessage());

        // PaymentOutboxEvent가 PENDING 상태로 저장되었는지 확인합니다.
        verify(paymentMessageOutboxWritter, times(1)).save(paymentOutboxEventCaptor.capture());
        PaymentOutboxEvent savedEvent = paymentOutboxEventCaptor.getValue();
        assertEquals(PaymentOutBoxEventStatus.PENDING.name(), savedEvent.getStatus());
        assertEquals("Reservation", savedEvent.getAggregateType());
        assertEquals(reservation.getId(), savedEvent.getAggregateId());
        assertEquals("PaidEvent", savedEvent.getEventType());
    }
}