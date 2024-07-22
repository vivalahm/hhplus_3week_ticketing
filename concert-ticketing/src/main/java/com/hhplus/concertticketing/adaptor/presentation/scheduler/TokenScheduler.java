package com.hhplus.concertticketing.adaptor.presentation.scheduler;

import com.hhplus.concertticketing.application.usecase.TokenUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenScheduler {
    private final TokenUseCase tokenUseCase;

    public TokenScheduler(TokenUseCase tokenUseCase) {
        this.tokenUseCase = tokenUseCase;
    }

    @Scheduled(fixedRate = 60*1000) //1분 간격 스케줄링
    public void checkExpiredTokens() {
        tokenUseCase.checkAndUpdateExpiredTokens();
    }
}
