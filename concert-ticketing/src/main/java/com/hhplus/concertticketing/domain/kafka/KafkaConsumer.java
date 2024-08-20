package com.hhplus.concertticketing.domain.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.model.Reservation;
import com.hhplus.concertticketing.domain.service.ConcertService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {
    private final ConcertService concertService;
    private final ObjectMapper objectMapper;

    public KafkaConsumer(ConcertService concertService, ObjectMapper objectMapper) {
        this.concertService = concertService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${payment_topic}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void listenPaymentCreated(String message) {
        try {
            // JSON 문자열을 Reservation 객체로 역직렬화
            Reservation reservation = objectMapper.readValue(message, Reservation.class);
            log.info("Received message: {}", reservation);
            // 좌석 예약 완료
            concertService.reserveSeat(reservation.getSeatId());

            log.info("좌석 예약이 성공적으로 처리되었습니다. 좌석 ID: {}", reservation.getSeatId());
        } catch (Exception e) {
            log.error("Kafka 메시지를 처리하는 동안 오류가 발생했습니다. 메시지: {}", message, e);
        }
    }
}
