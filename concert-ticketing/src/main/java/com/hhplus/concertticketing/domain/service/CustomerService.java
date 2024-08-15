package com.hhplus.concertticketing.domain.service;

import com.hhplus.concertticketing.domain.model.Customer;
import com.hhplus.concertticketing.domain.repository.CustomerRepository;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Customer chargePoint(Long customerId, Double amount) {
        Customer customer = customerRepository.getCustomerByIdWithPessimisticLock(customerId);
        if(customer == null) throw new CustomException(ErrorCode.NOT_FOUND, "사용자가 존재하지 않습니다.");
        customer.chargePoint(amount);
        return customerRepository.saveCustomer(customer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Customer getCustomerById(Long customerId) {
        return customerRepository.getCustomerById(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "고객 정보가 없습니다."));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Customer usePoint(Long customerId, Double amount) {
        Customer customer = customerRepository.getCustomerByIdWithPessimisticLock(customerId);
        if(customer == null) throw new CustomException(ErrorCode.NOT_FOUND, "사용자가 존재하지 않습니다.");
        customer.usePoint(amount);
        try {
            return customerRepository.saveCustomer(customer);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "동시에 포인트를 사용 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }
}