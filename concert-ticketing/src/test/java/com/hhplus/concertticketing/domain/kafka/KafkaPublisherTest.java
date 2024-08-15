package com.hhplus.concertticketing.domain.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.message.PaymentMessageOutboxWritter;
import com.hhplus.concertticketing.domain.model.PaymentOutBoxEventStatus;
import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.infrastructure.kafka.payment.PaymentOutboxEvent;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class KafkaPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PaymentMessageOutboxWritter paymentMessageOutboxWritter;

    @InjectMocks
    private KafkaPublisher kafkaPublisher;

    @Captor
    private ArgumentCaptor<PaymentOutboxEvent> paymentOutboxEventCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaPublisher = new KafkaPublisher(kafkaTemplate, "payment_topic", objectMapper, paymentMessageOutboxWritter);
    }

    @Test
    void testPublishPaymentInfo_Success() throws Exception {
        // Given
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        String messageAsJson = "mockedJson";

        // Mocking Kafka future and result
        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        SendResult<String, String> sendResult = mock(SendResult.class);
        RecordMetadata recordMetadata = mock(RecordMetadata.class);

        // Set up the mocked responses
        when(objectMapper.writeValueAsString(reservation)).thenReturn(messageAsJson);
        when(kafkaTemplate.send(eq("payment_topic"), eq("1"), eq(messageAsJson)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(10L);

        // When
        kafkaPublisher.publishPaymentInfo(reservation);

        // Then
        verify(paymentMessageOutboxWritter, times(1)).save(paymentOutboxEventCaptor.capture());
        PaymentOutboxEvent savedEvent = paymentOutboxEventCaptor.getValue();
        assertEquals(PaymentOutBoxEventStatus.SENT.name(), savedEvent.getStatus());
        assertEquals("Reservation", savedEvent.getAggregateType());
        assertEquals(reservation.getId(), savedEvent.getAggregateId());
        assertEquals("PaidEvent", savedEvent.getEventType());

        verify(kafkaTemplate, times(1)).send(eq("payment_topic"), eq("1"), eq(messageAsJson));
        verify(objectMapper, times(1)).writeValueAsString(reservation);
    }

    @Test
    void testPublishPaymentInfo_Failure() throws Exception {
        // Given
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        String messageAsJson = "mockedJson";
        Exception kafkaException = new RuntimeException("Kafka exception");

        when(objectMapper.writeValueAsString(reservation)).thenReturn(messageAsJson);
        when(kafkaTemplate.send(eq("payment_topic"), eq("1"), eq(messageAsJson)))
                .thenReturn(CompletableFuture.failedFuture(kafkaException));

        // When
        kafkaPublisher.publishPaymentInfo(reservation);

        // Then
        verify(paymentMessageOutboxWritter, times(1)).save(paymentOutboxEventCaptor.capture());
        PaymentOutboxEvent savedEvent = paymentOutboxEventCaptor.getValue();
        assertEquals(PaymentOutBoxEventStatus.PENDING.name(), savedEvent.getStatus());
        assertEquals("Reservation", savedEvent.getAggregateType());
        assertEquals(reservation.getId(), savedEvent.getAggregateId());
        assertEquals("PaidEvent", savedEvent.getEventType());

        verify(kafkaTemplate, times(1)).send(eq("payment_topic"), eq("1"), eq(messageAsJson));
        verify(objectMapper, times(1)).writeValueAsString(reservation);
    }

    @Test
    void testPublishPaymentInfo_ExceptionThrown() throws Exception {
        // Given
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        when(objectMapper.writeValueAsString(reservation)).thenThrow(new RuntimeException("ObjectMapper exception"));

        // When & Then
        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> kafkaPublisher.publishPaymentInfo(reservation));
        assertEquals("예약 정보 전달에 실패하였습니다. 예약 정보: " + reservation, exception.getMessage());

        verify(paymentMessageOutboxWritter, never()).save(any(PaymentOutboxEvent.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }
}