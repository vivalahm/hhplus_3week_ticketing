package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.repository.CustomerRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("홍길동");
        customerRepository.saveCustomer(customer);
    }

    @Test
    @DisplayName("포인트 충전 통합 테스트")
    void chargePoint_ShouldReturnUpdatedCustomer() {
        Double amount = 100.0;

        Customer updatedCustomer = customerService.chargePoint(customer.getId(), amount);

        assertNotNull(updatedCustomer);
        assertEquals(100.0, updatedCustomer.getPoint());
    }

    @Test
    @DisplayName("고객 ID로 조회 통합 테스트")
    void getCustomerById_ShouldReturnCustomer() {
        Customer foundCustomer = customerService.getCustomerById(customer.getId());

        assertNotNull(foundCustomer);
        assertEquals(customer.getId(), foundCustomer.getId());
    }

    @Test
    @DisplayName("고객 ID로 조회 시 고객이 존재하지 않을 때 예외 발생 통합 테스트")
    void getCustomerById_ShouldThrowException_WhenNotFound() {
        Long nonExistentCustomerId = 999L;

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            customerService.getCustomerById(nonExistentCustomerId);
        });

        assertEquals("고객 정보가 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("포인트 사용 통합 테스트")
    void usePoint_ShouldReturnUpdatedCustomer() {
        customer.chargePoint(100.0);
        customerRepository.saveCustomer(customer);

        Double amount = 50.0;
        Customer updatedCustomer = customerService.usePoint(customer.getId(), amount);

        assertNotNull(updatedCustomer);
        assertEquals(50.0, updatedCustomer.getPoint());
    }

    @Test
    @DisplayName("포인트 사용 시 고객이 존재하지 않을 때 예외 발생 통합 테스트")
    void usePoint_ShouldThrowException_WhenCustomerNotFound() {
        Long nonExistentCustomerId = 999L;
        Double amount = 50.0;

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            customerService.usePoint(nonExistentCustomerId, amount);
        });

        assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("낙관적 락을 이용한 동시성 포인트 충전 테스트")
    void chargePoint_ShouldHandleOptimisticLocking() throws InterruptedException {
        Customer customer1 = customerService.getCustomerById(customer.getId());
        Customer customer2 = customerService.getCustomerById(customer.getId());

        Thread thread1 = new Thread(() -> {
            customer1.chargePoint(100.0);
            customerService.chargePoint(customer1.getId(), 100.0);
        });

        Thread thread2 = new Thread(() -> {
            customer2.chargePoint(50.0);
            try {
                customerService.chargePoint(customer2.getId(), 50.0);
            } catch (OptimisticLockException e) {
                System.out.println("Expected OptimisticLockException: " + e.getMessage());
            }
        });

        thread1.start();
        Thread.sleep(100); // 잠시 대기하여 첫 번째 트랜잭션이 우선 실행되도록 함
        thread2.start();

        thread1.join();
        thread2.join();

        // 최종 결과 검증
        Customer updatedCustomer = customerService.getCustomerById(customer.getId());
        assertTrue(updatedCustomer.getPoint() == 100.0 || updatedCustomer.getPoint() == 150.0);
    }
}
