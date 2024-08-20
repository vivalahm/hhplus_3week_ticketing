package com.hhplus.concertticketing.domain.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.domain.service.ConcertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class KafkaConsumerTest {

    @Mock
    private ConcertService concertService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listenPaymentCreated_shouldReserveSeat_whenMessageIsValid() throws Exception {
        // given
        String message = "{\"seatId\": \"12345\"}";
        Reservation reservation = new Reservation();
        reservation.setSeatId(12345L);

        when(objectMapper.readValue(message, Reservation.class)).thenReturn(reservation);

        // when
        kafkaConsumer.listenPaymentCreated(message);

        // then
        verify(concertService, times(1)).reserveSeat(12345L);
    }

    @Test
    void listenPaymentCreated_shouldNotReserveSeat_whenExceptionIsThrown() throws Exception {
        // given
        String message = "{\"seatId\": \"12345\"}";

        when(objectMapper.readValue(message, Reservation.class)).thenThrow(new RuntimeException("Test Exception"));

        // when
        kafkaConsumer.listenPaymentCreated(message);

        // then
        verify(concertService, never()).reserveSeat(anyLong());
    }
}