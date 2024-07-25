package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.adaptor.presentation.dto.request.ChargePointRequest;
import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.repository.CustomerRepository;
import com.hhplus.concertticketing.business.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrentTest {

    private static final Logger log = LoggerFactory.getLogger(ConcurrentTest.class);

    @Autowired
    private CustomerUseCase customerUseCase;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Test
    @DisplayName("동시 포인트 충전 테스트")
    void chargePoint_ShouldHandleConcurrentRequests() throws InterruptedException {
        Customer customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(0.0);
        Customer savedCustomer = customerRepository.saveCustomer(customer);
        ChargePointRequest request = new ChargePointRequest();
        request.setCustomerId(savedCustomer.getId());
        request.setAmount(100.0);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        Runnable task = () -> {
            long startTimeStamp = System.nanoTime();
            try {
                customerUseCase.chargePoint(request);
            } catch (Exception e) {
                log.info("Error: " + e.getMessage());
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

        Customer updatedCustomer = customerService.getCustomerById(savedCustomer.getId());
        log.info("최종 사용자 포인트: " + updatedCustomer.getPoint());
        assertEquals(1000.0, updatedCustomer.getPoint());
    }

    @Test
    @DisplayName("동시 포인트 사용 테스트")
    void usePoint_ShouldHandleConcurrentRequests() throws InterruptedException {
        Customer customer2 = new Customer();
        customer2.setName("홍길동");
        customer2.setPoint(10000.0);
        Customer savedCustomer2 = customerRepository.saveCustomer(customer2);


        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        Runnable task = () -> {
            long startTimeStamp = System.nanoTime();
            try {
                customerUseCase.usePoint(savedCustomer2.getId(), 100.0);
            } catch (Exception e) {
                log.info("Error: " + e.getMessage());
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

        Customer updatedCustomer = customerService.getCustomerById(savedCustomer2.getId());
        log.info("최종 사용자 포인트: " + updatedCustomer.getPoint());
        assertEquals(9000.0, updatedCustomer.getPoint());
    }
}
