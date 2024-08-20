package com.hhplus.concertticketing.Interfaces.presentation.outBoxProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.message.PaymentMessageOutboxWritter;
import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.domain.model.ReservationStatus;
import com.hhplus.concertticketing.domain.repository.ReservationRepository;
import com.hhplus.concertticketing.infrastructure.kafka.payment.PaymentOutboxEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PaymentOutBoxEventProcessorTest {

    @Mock
    private PaymentMessageOutboxWritter paymentMessageOutboxWritter;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentOutBoxEventProcessor paymentOutBoxEventProcessor;

    @Captor
    private ArgumentCaptor<PaymentOutboxEvent> paymentOutboxEventCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessPaymentOutboxEvent_Success() throws Exception {
        // Given
        PaymentOutBoxEventProcessor paymentOutBoxEventProcessor = new PaymentOutBoxEventProcessor(
                paymentMessageOutboxWritter, kafkaTemplate, reservationRepository, objectMapper
        );

        // ReflectionTestUtils를 사용하여 private 필드에 접근
        ReflectionTestUtils.setField(paymentOutBoxEventProcessor, "PAYMENT_TOPIC", "payment_topic");

        PaymentOutboxEvent event = new PaymentOutboxEvent();
        event.setAggregateId(1L);
        event.setStatus("PENDING");
        event.setRetryCount(0);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.PAID);

        when(paymentMessageOutboxWritter.findByStatus("PENDING")).thenReturn(Collections.singletonList(event));
        when(reservationRepository.getReservationById(1L)).thenReturn(Optional.of(reservation));
        when(objectMapper.writeValueAsString(reservation)).thenReturn("{\"id\":1,\"status\":\"PAID\"}");

        // When
        paymentOutBoxEventProcessor.processPaymentOutboxEvent();

        // Then
        verify(kafkaTemplate, times(1)).send(eq("payment_topic"), eq("1"), eq("{\"id\":1,\"status\":\"PAID\"}"));
        verify(paymentMessageOutboxWritter, times(1)).save(paymentOutboxEventCaptor.capture());
        PaymentOutboxEvent savedEvent = paymentOutboxEventCaptor.getValue();
        assertEquals("SENT", savedEvent.getStatus());
        assertEquals(0, savedEvent.getRetryCount());
    }

    @Test
    void testProcessPaymentOutboxEvent_RetryAndFail() throws Exception {
        // Given
        PaymentOutboxEvent event = new PaymentOutboxEvent();
        event.setAggregateId(1L);
        event.setStatus("PENDING");
        event.setRetryCount(2); // 이미 두 번 재시도됨

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.PAID);

        when(paymentMessageOutboxWritter.findByStatus("PENDING")).thenReturn(Collections.singletonList(event));
        when(reservationRepository.getReservationById(1L)).thenReturn(Optional.of(reservation));
        when(objectMapper.writeValueAsString(reservation)).thenThrow(new RuntimeException("Kafka exception"));

        // When
        paymentOutBoxEventProcessor.processPaymentOutboxEvent();

        // Then
        verify(paymentMessageOutboxWritter, times(1)).save(paymentOutboxEventCaptor.capture());
        PaymentOutboxEvent savedEvent = paymentOutboxEventCaptor.getValue();
        assertEquals("FAILED", savedEvent.getStatus());  // 상태는 FAILED여야 함
        assertEquals(3, savedEvent.getRetryCount());  // retryCount는 3이어야 함
    }
}