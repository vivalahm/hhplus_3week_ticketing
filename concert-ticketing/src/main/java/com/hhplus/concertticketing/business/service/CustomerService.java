package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.repository.CustomerRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import jakarta.persistence.OptimisticLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer chargePoint(Long customerId, Double amount) {
        Customer customer = customerRepository.getCustomerById(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "고객 정보를 찾을 수 없습니다."));
        customer.chargePoint(amount);
        try {
            return customerRepository.saveCustomer(customer);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 포인트를 충전하는 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Transactional
    public Customer getCustomerById(Long customerId) {
        return customerRepository.getCustomerById(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "고객 정보가 없습니다."));
    }

    @Transactional
    public Customer usePoint(Long customerId, Double amount) {
        Customer customer = customerRepository.getCustomerById(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "사용자가 존재하지 않습니다."));
        customer.usePoint(amount);
        try {
            return customerRepository.saveCustomer(customer);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 포인트를 사용 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }
}