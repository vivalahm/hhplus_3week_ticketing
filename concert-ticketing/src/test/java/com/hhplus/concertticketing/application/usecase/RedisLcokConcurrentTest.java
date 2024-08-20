package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.Interfaces.presentation.dto.request.ChargePointRequest;
import com.hhplus.concertticketing.domain.model.Customer;
import com.hhplus.concertticketing.domain.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisLcokConcurrentTest {
    private static final Logger log = LoggerFactory.getLogger(RedisLcokConcurrentTest.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerUseCase customerUseCase;

    @Test
    @DisplayName("Redis 분산 락을 통한 동시 포인트 충전 테스트")
    void chargePointByRedis_ShouldHandleConcurrentRequests() throws InterruptedException {
        // 테스트 데이터 생성
        Customer customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(0.0);
        Customer savedCustomer = customerRepository.saveCustomer(customer);

        ChargePointRequest request = new ChargePointRequest();
        request.setCustomerId(savedCustomer.getId());
        request.setAmount(100.0);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        Runnable task = () -> {
            long startTimeStamp = System.nanoTime();
            try {
                customerUseCase.chargePointByRedis(request);
                successCount.incrementAndGet();
            } catch (Exception e) {
                log.info("Error: " + e.getMessage());
                failureCount.incrementAndGet();
            } finally {
                long endTimeStamp = System.nanoTime();
                log.info("Execution time: " + (endTimeStamp - startTimeStamp) / 1000000 + "ms");
                latch.countDown();
            }
        };

        for (int i = 0; i < 10; i++) {
            executor.submit(task);
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executor.shutdown();

        log.info("성공한 포인트 충전 수: " + successCount.get());
        log.info("실패한 포인트 충전 수: " + failureCount.get());

        // 결과 검증: 하나의 포인트 충전만 성공해야 한다.
        assertEquals(1, successCount.get());
        assertEquals(9, failureCount.get());
    }
}
