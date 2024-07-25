package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.repository.CustomerRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerServiceIntegrationTestForPessimisticLock {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceIntegrationTestForPessimisticLock.class);


    @Test
    @Transactional
    void chargePoint_success() {
        Customer customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(0.0);
        customerRepository.saveCustomer(customer);

        Customer updatedCustomer = customerService.chargePoint(customer.getId(), 100.0);

        assertNotNull(updatedCustomer);
        assertEquals(100.0, updatedCustomer.getPoint());
    }

    @Test
    @Transactional
    void chargePoint_notFound() {
        CustomException exception = assertThrows(CustomException.class, () -> customerService.chargePoint(999L, 100.0));
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @Transactional
    void usePoint_success() {
        Customer customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(100.0);
        customerRepository.saveCustomer(customer);

        Customer updatedCustomer = customerService.usePoint(customer.getId(), 50.0);

        assertNotNull(updatedCustomer);
        assertEquals(50.0, updatedCustomer.getPoint());
    }

    @Test
    @Transactional
    void usePoint_notFound() {
        CustomException exception = assertThrows(CustomException.class, () -> customerService.usePoint(999L, 50.0));
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void pessimisticLocking_concurrentChargePoint() throws InterruptedException {
        // 초기 데이터 설정
        Customer customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(0.0);
        customerRepository.saveCustomer(customer);

        int numberOfThreads = 10;
        double chargeAmount = 10.0;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 두 개의 동시 트랜잭션 시뮬레이션
        Runnable task = () -> {
            try {
                performChargePoint(customer.getId(), chargeAmount);
            } catch (CustomException e) {
                // 예외 처리
            }
            latch.countDown();
        };

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(task).start();
        }

        // 스레드가 종료될 때까지 대기
        latch.await();

        // 결과 검증
        Customer updatedCustomer = customerRepository.getCustomerByIdWithLock(customer.getId()).orElseThrow();
        // 모든 트랜잭션이 성공해야 하므로 예상 포인트는 초기 포인트 + (chargeAmount * numberOfThreads)
        double expectedPoints = 0.0 + (chargeAmount * numberOfThreads);
        assertEquals(expectedPoints, updatedCustomer.getPoint());

        logger.info("최종 포인트: {}", updatedCustomer.getPoint());
    }

    @Test
    void pessimisticLocking_concurrentUsePoint() throws InterruptedException {
        // 초기 데이터 설정
        Customer customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(200.0); // Ensure enough points for deductions
        customerRepository.saveCustomer(customer);

        int numberOfThreads = 10;
        double deductionAmount = 10.0;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 동시 트랜잭션 시뮬레이션
        Runnable task = () -> {
            try {
                performUsePoint(customer.getId(), deductionAmount);
            } catch (CustomException e) {
                // 예외 처리
            }
            latch.countDown();
        };

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(task).start();
        }

        // 스레드가 종료될 때까지 대기
        latch.await();

        // 결과 검증
        Customer updatedCustomer = customerRepository.getCustomerByIdWithLock(customer.getId()).orElseThrow();
        // 모든 트랜잭션이 성공해야 하므로 예상 포인트는 초기 포인트 - (deductionAmount * numberOfThreads)
        double expectedPoints = 200.0 - (deductionAmount * numberOfThreads);
        assertEquals(expectedPoints, updatedCustomer.getPoint());

        logger.info("최종 포인트: {}", updatedCustomer.getPoint());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void performChargePoint(Long customerId, Double amount) {
        customerService.chargePoint(customerId, amount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void performUsePoint(Long customerId, Double amount) {
        customerService.usePoint(customerId, amount);
    }
}