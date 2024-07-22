package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.repository.CustomerRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTestForOptimisticLock {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void chargePoint_success() {
        Long customerId = 1L;
        Double amount = 100.0;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setPoint(0.0);

        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.saveCustomer(customer)).thenReturn(customer);

        Customer updatedCustomer = customerService.chargePoint(customerId, amount);

        assertNotNull(updatedCustomer);
        assertEquals(100.0, updatedCustomer.getPoint());
    }

    @Test
    void chargePoint_notFound() {
        Long customerId = 1L;
        Double amount = 100.0;

        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> customerService.chargePoint(customerId, amount));
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void chargePoint_optimisticLockingFailure() {
        Long customerId = 1L;
        Double amount = 100.0;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setPoint(0.0);

        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.saveCustomer(customer)).thenThrow(ObjectOptimisticLockingFailureException.class);

        CustomException exception = assertThrows(CustomException.class, () -> customerService.chargePoint(customerId, amount));
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
    }

    @Test
    void usePoint_success() {
        Long customerId = 1L;
        Double amount = 50.0;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setPoint(100.0);

        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.saveCustomer(customer)).thenReturn(customer);

        Customer updatedCustomer = customerService.usePoint(customerId, amount);

        assertNotNull(updatedCustomer);
        assertEquals(50.0, updatedCustomer.getPoint());
    }

    @Test
    void usePoint_notFound() {
        Long customerId = 1L;
        Double amount = 50.0;

        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> customerService.usePoint(customerId, amount));
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void usePoint_optimisticLockingFailure() {
        Long customerId = 1L;
        Double amount = 50.0;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setPoint(100.0);

        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.saveCustomer(customer)).thenThrow(ObjectOptimisticLockingFailureException.class);

        CustomException exception = assertThrows(CustomException.class, () -> customerService.usePoint(customerId, amount));
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
    }
}