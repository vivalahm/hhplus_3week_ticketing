package com.hhplus.concertticketing.application.usecase.listener;

import com.hhplus.concertticketing.application.usecase.event.TokenExpireEvent;
import com.hhplus.concertticketing.business.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class TokenExpireEventListener {
    private final TokenService tokenService;

    public TokenExpireEventListener(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTokenExpireEvent(TokenExpireEvent event) {
        try {
            tokenService.removeToken(event.getToken().getTokenValue());
        } catch (Exception e) {
            log.error("토큰 만료 처리에 실패하였습니다. 토큰 정보: {}", event.getToken(), e);
        }
    }
}
