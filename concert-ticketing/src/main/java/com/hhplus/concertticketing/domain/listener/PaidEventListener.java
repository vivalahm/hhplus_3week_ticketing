package com.hhplus.concertticketing.domain.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concertticketing.domain.event.PaidEvent;
import com.hhplus.concertticketing.domain.kafka.KafkaPublisher;
import com.hhplus.concertticketing.domain.model.Token;
import com.hhplus.concertticketing.domain.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class PaidEventListener {
    private final KafkaPublisher kafkaPublisher;
    private final TokenService tokenService;

    public PaidEventListener(KafkaPublisher kafkaPublisher, TokenService tokenService) {
        this.kafkaPublisher = kafkaPublisher;
        this.tokenService = tokenService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaidEvent(PaidEvent paidEvent) {
        try {
            // 예약 정보를 Kafka로 전달
            kafkaPublisher.publishPaymentInfo(paidEvent.getReservation());
        } catch (Exception e) {
            log.error("예약 정보 전달에 실패하였습니다. 예약 정보: {}", paidEvent.getReservation(), e);
        }finally {
            // 토큰 만료 처리
            Token token = tokenService.getTokenByConcertIdAndCustomerId(paidEvent.getConcertOption().getConcertId(), paidEvent.getReservation().getCustomerId())
                    .orElseThrow(() -> new RuntimeException("토큰이 존재하지 않습니다."));

            tokenService.removeToken(token.getTokenValue());
        }

    }
}
