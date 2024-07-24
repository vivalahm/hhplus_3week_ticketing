package com.hhplus.concertticketing.business.repository;

import com.hhplus.concertticketing.business.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> getCustomerById(Long customerId);
    Optional<Customer> getCustomerByIdWithLock(Long customerId);
    Customer saveCustomer(Customer customer);
    List<Customer> getAllCustomers();
    void deleteCustomer(Long customerId);
}
