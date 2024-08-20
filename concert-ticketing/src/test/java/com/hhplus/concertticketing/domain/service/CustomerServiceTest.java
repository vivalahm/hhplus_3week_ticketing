package com.hhplus.concertticketing.domain.service;

import com.hhplus.concertticketing.domain.model.Customer;
import com.hhplus.concertticketing.domain.repository.CustomerRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("포인트 충전 시 업데이트된 고객 객체 반환 테스트")
    void chargePoint_ShouldReturnUpdatedCustomer() {
        Long customerId = 1L;
        Double amount = 100.0;
        Customer customer = new Customer();
        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.saveCustomer(customer)).thenReturn(customer);

        Customer updatedCustomer = customerService.chargePoint(customerId, amount);

        assertNotNull(updatedCustomer);
        assertEquals(100.0, updatedCustomer.getPoint());
        verify(customerRepository, times(1)).getCustomerById(customerId);
        verify(customerRepository, times(1)).saveCustomer(customer);
    }

    @Test
    @DisplayName("고객 ID로 조회하여 고객 객체가 존재할 때 반환 테스트")
    void getCustomerById_ShouldReturnCustomer_WhenFound() {
        Long customerId = 1L;
        Customer customer = new Customer();
        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.of(customer));

        Customer foundCustomer = customerService.getCustomerById(customerId);

        assertNotNull(foundCustomer);
        verify(customerRepository, times(1)).getCustomerById(customerId);
    }

    @Test
    @DisplayName("고객 ID로 조회할 때 고객 객체가 존재하지 않으면 예외 발생 테스트")
    void getCustomerById_ShouldThrowException_WhenNotFound() {
        Long customerId = 1L;
        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            customerService.getCustomerById(customerId);
        });

        assertEquals("고객 정보가 없습니다.", exception.getMessage());
        verify(customerRepository, times(1)).getCustomerById(customerId);
    }

    @Test
    @DisplayName("포인트 사용 시 업데이트된 고객 객체 반환 테스트")
    void usePoint_ShouldReturnUpdatedCustomer() {
        Long customerId = 1L;
        Double amount = 50.0;
        Customer customer = new Customer();
        customer.chargePoint(100.0);  // 초기 잔액 설정
        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.saveCustomer(customer)).thenReturn(customer);

        Customer updatedCustomer = customerService.usePoint(customerId, amount);

        assertNotNull(updatedCustomer);
        assertEquals(50.0, updatedCustomer.getPoint());
        verify(customerRepository, times(1)).getCustomerById(customerId);
        verify(customerRepository, times(1)).saveCustomer(customer);
    }

    @Test
    @DisplayName("고객 ID로 조회할 때 고객 객체가 존재하지 않으면 예외 발생 테스트")
    void usePoint_ShouldThrowException_WhenCustomerNotFound() {
        Long customerId = 1L;
        Double amount = 50.0;
        when(customerRepository.getCustomerById(customerId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            customerService.usePoint(customerId, amount);
        });

        assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
        verify(customerRepository, times(1)).getCustomerById(customerId);
    }
}