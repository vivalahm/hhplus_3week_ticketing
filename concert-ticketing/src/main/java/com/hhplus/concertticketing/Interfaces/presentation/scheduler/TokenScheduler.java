package com.hhplus.concertticketing.Interfaces.presentation.scheduler;

import com.hhplus.concertticketing.application.usecase.TokenUseCase;
import com.hhplus.concertticketing.domain.model.Concert;
import com.hhplus.concertticketing.domain.service.ConcertService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenScheduler {
    private final TokenUseCase tokenUseCase;
    private final ConcertService concertService;

    public TokenScheduler(TokenUseCase tokenUseCase, ConcertService concertService) {
        this.tokenUseCase = tokenUseCase;
        this.concertService = concertService;
    }

    @Scheduled(fixedRate = 60 * 1000) // 1분 간격 스케줄링
    public void checkExpiredTokens() {
        List<Concert> concerts = concertService.getAvailableConcerts(); // 활성 콘서트 ID 목록 가져오기
        for (Concert concert : concerts) {
            tokenUseCase.checkAndUpdateExpiredTokens(concert.getId(), 30);
        }
    }
}