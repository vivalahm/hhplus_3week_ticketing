package com.hhplus.concertticketing.Interfaces.presentation.scheduler;

import com.hhplus.concertticketing.application.usecase.ReservationUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationScheduler {
    private final ReservationUseCase reservationUseCase;

    public ReservationScheduler(ReservationUseCase reservationUseCase) {
        this.reservationUseCase = reservationUseCase;
    }

    @Scheduled(fixedRate = 60*1000)
    public void checkExpiredReservations() {
        reservationUseCase.checkAndUpdateExpiredReservations();
    }
}
