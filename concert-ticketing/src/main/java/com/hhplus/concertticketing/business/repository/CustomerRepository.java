package com.hhplus.concertticketing.business.repository;

import com.hhplus.concertticketing.business.model.Customer;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> getCustomerById(Long customerId);
    Customer saveCustomer(Customer customer);
}
