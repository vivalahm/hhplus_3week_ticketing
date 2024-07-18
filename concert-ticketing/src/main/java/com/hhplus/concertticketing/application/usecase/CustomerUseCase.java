package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.service.CustomerService;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.ChargePointRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.CustomerPointResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerUseCase {
    private final CustomerService customerService;

    public CustomerUseCase(CustomerService customerService) {
        this.customerService = customerService;
    }

    public CustomerPointResponse chargePoint(ChargePointRequest chargePointRequest) {
        Customer customer = customerService.chargePoint(chargePointRequest.getCustomerId(),chargePointRequest.getAmount());
        return new CustomerPointResponse(customer.getPoint());
    }

    public CustomerPointResponse usePoint(Long customerId, Double amount) {
        Customer customer = customerService.usePoint(customerId, amount);
        return new CustomerPointResponse(customer.getPoint());
    }

    public CustomerPointResponse getPoint(Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return new CustomerPointResponse(customer.getPoint());
    }

}
