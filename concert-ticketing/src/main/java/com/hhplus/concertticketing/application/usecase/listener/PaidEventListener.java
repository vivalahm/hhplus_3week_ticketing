package com.hhplus.concertticketing.application.usecase.listener;

import com.hhplus.concertticketing.application.usecase.event.PaidEvent;
import com.hhplus.concertticketing.application.usecase.event.TokenExpireEvent;
import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.service.TokenService;
import com.hhplus.concertticketing.infrastructure.persistance.DataPlatformMockApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class PaidEventListener {
    private final DataPlatformMockApiClient dataPlatformMockApiClient;
    private final TokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    public PaidEventListener(DataPlatformMockApiClient dataPlatformMockApiClient, TokenService tokenService, ApplicationEventPublisher eventPublisher) {
        this.dataPlatformMockApiClient = dataPlatformMockApiClient;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaidEvent(PaidEvent paidEvent) {
        try {
            // 예약 정보를 데이터 플랫폼으로 전달
            dataPlatformMockApiClient.sendReservationInfo(paidEvent.getReservation());

            // 토큰 만료 이벤트 발행
            Token token = tokenService.getTokenByConcertIdAndCustomerId(paidEvent.getConcertOption().getConcertId(), paidEvent.getReservation().getCustomerId())
                    .orElseThrow(() -> new RuntimeException("토큰이 존재하지 않습니다."));

            eventPublisher.publishEvent(new TokenExpireEvent(this, token));
        } catch (Exception e) {
            log.error("예약 정보 전달에 실패하였습니다. 예약 정보: {}", paidEvent.getReservation(), e);
        }
    }
}
