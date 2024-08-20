package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.domain.model.Customer;
import com.hhplus.concertticketing.domain.service.CustomerService;
import com.hhplus.concertticketing.Interfaces.presentation.dto.request.ChargePointRequest;
import com.hhplus.concertticketing.Interfaces.presentation.dto.response.CustomerPointResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class CustomerUseCase {
    private final CustomerService customerService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String  Lock_key = "PointLock";

    public CustomerUseCase(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Transactional
    public CustomerPointResponse chargePoint(ChargePointRequest chargePointRequest) {
        Customer customer = customerService.chargePoint(chargePointRequest.getCustomerId(),chargePointRequest.getAmount());
        return new CustomerPointResponse(customer.getPoint());
    }

    @Transactional
    public CustomerPointResponse usePoint(Long customerId, Double amount) {
        Customer customer = customerService.usePoint(customerId, amount);
        return new CustomerPointResponse(customer.getPoint());
    }

    public CustomerPointResponse getPoint(Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return new CustomerPointResponse(customer.getPoint());
    }

    public CustomerPointResponse chargePointByRedis(ChargePointRequest chargePointRequest) {
        boolean acquireLock = stringRedisTemplate.opsForValue().setIfAbsent(Lock_key, "locked", 10, TimeUnit.SECONDS);
        if(acquireLock) {
            try {
                Customer customer = customerService.chargePoint(chargePointRequest.getCustomerId(), chargePointRequest.getAmount());
                return new CustomerPointResponse(customer.getPoint());
            } finally {
                stringRedisTemplate.delete(Lock_key);
            }
        } else {
            throw new RuntimeException("락 권한이 없습니다.");
        }
    }

}
