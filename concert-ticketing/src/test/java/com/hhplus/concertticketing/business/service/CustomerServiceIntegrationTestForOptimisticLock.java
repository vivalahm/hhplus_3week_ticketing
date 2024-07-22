package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.repository.CustomerRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerServiceIntegrationTestForOptimisticLock {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @Transactional
    void chargePoint_success() {
        Customer customer = new Customer();
        customer.setName("John Doe");
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
        customer.setName("John Doe");
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
    void optimisticLockingFailure_chargePoint() {
        // Given
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setPoint(100.0);
        customerRepository.saveCustomer(customer);

        // Simulate two concurrent transactions
        Thread thread1 = new Thread(() -> {
            try {
                customerService.chargePoint(customer.getId(), 50.0);
            } catch (CustomException e) {
                // Expected exception due to optimistic locking failure
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                customerService.chargePoint(customer.getId(), 30.0);
            } catch (CustomException e) {
                // Expected exception due to optimistic locking failure
            }
        });

        // When
        thread1.start();
        thread2.start();

        // Join threads to ensure both have finished
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then
        Customer updatedCustomer = customerRepository.getCustomerById(customer.getId()).orElseThrow();
        // Only one transaction should succeed, so the point should be 150 or 130
        assertTrue(updatedCustomer.getPoint() == 150.0 || updatedCustomer.getPoint() == 130.0);
    }

    @Test
    void optimisticLockingFailure_usePoint() {
        // Given
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setPoint(100.0);
        customerRepository.saveCustomer(customer);

        // Simulate two concurrent transactions
        Thread thread1 = new Thread(() -> {
            try {
                customerService.usePoint(customer.getId(), 50.0);
            } catch (CustomException e) {
                // Expected exception due to optimistic locking failure
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                customerService.usePoint(customer.getId(), 30.0);
            } catch (CustomException e) {
                // Expected exception due to optimistic locking failure
            }
        });

        // When
        thread1.start();
        thread2.start();

        // Join threads to ensure both have finished
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then
        Customer updatedCustomer = customerRepository.getCustomerById(customer.getId()).orElseThrow();
        // Only one transaction should succeed, so the point should be 50 or 70
        assertTrue(updatedCustomer.getPoint() == 50.0 || updatedCustomer.getPoint() == 70.0);
    }
}