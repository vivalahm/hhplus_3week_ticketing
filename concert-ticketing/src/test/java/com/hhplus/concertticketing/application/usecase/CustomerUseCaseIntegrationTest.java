package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.repository.CustomerRepository;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.ChargePointRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.CustomerPointResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CustomerUseCaseIntegrationTest {

    @Autowired
    private CustomerUseCase customerUseCase;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        // Initialize customer
        customer = new Customer();
        customer.setName("홍길동");
        customer.setPoint(0.0);
        customer = customerRepository.saveCustomer(customer); // Save and retrieve the persisted customer with generated ID
        assertNotNull(customer.getId(), "Customer ID should not be null after saving");
    }

    @Test
    @DisplayName("포인트 충전 테스트")
    void chargePoint_ShouldIncreaseCustomerPoint() {
        ChargePointRequest request = new ChargePointRequest();
        request.setCustomerId(customer.getId());
        request.setAmount(100.0);

        CustomerPointResponse response = customerUseCase.chargePoint(request);

        assertNotNull(response);
        assertEquals(100.0, response.getPoint());

        Customer updatedCustomer = customerRepository.getCustomerById(customer.getId()).orElseThrow();
        assertEquals(100.0, updatedCustomer.getPoint());
    }

    @Test
    @DisplayName("포인트 사용 테스트")
    void usePoint_ShouldDecreaseCustomerPoint() {
        // Pre-charge the customer with points
        customer.setPoint(100.0);
        customerRepository.saveCustomer(customer);

        CustomerPointResponse response = customerUseCase.usePoint(customer.getId(), 50.0);

        assertNotNull(response);
        assertEquals(50.0, response.getPoint());

        Customer updatedCustomer = customerRepository.getCustomerById(customer.getId()).orElseThrow();
        assertEquals(50.0, updatedCustomer.getPoint());
    }

    @Test
    @DisplayName("포인트 조회 테스트")
    void getPoint_ShouldReturnCustomerPoint() {
        // Pre-charge the customer with points
        customer.setPoint(100.0);
        customerRepository.saveCustomer(customer);

        CustomerPointResponse response = customerUseCase.getPoint(customer.getId());

        assertNotNull(response);
        assertEquals(100.0, response.getPoint());
    }
}
