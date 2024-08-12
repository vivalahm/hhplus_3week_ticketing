package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.business.model.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataPlatformMockApiClient {
    public void sendReservationInfo(Reservation reservation) {
        try {
            Thread.sleep(1000);
            log.info("성공적으로 예약 정보를 전송했습니다. 예약 정보: {}", reservation);
        } catch (InterruptedException e) {
            log.error("예약정보 전달에 실패하였습니다 에러 내용:{} ", e.getMessage(), e);
        }
    }
}
