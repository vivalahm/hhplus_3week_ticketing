package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.repository.CustomerRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer chargePoint(Long customerId, Double amount) {
        Customer customer = customerRepository.getCustomerByIdWithLock(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "고객 정보를 찾을 수 없습니다."));
        customer.chargePoint(amount);
        return customerRepository.saveCustomer(customer);
    }

    @Transactional
    public Customer getCustomerById(Long customerId) {
        return customerRepository.getCustomerById(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "고객 정보가 없습니다."));
    }

    @Transactional
    public Customer usePoint(Long customerId, Double amount) {
        Customer customer = customerRepository.getCustomerByIdWithLock(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자가 존재하지 않습니다."));
        customer.usePoint(amount);
        return customerRepository.saveCustomer(customer);
    }
}