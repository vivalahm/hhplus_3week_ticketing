package com.hhplus.concertticketing.domain.repository;

import com.hhplus.concertticketing.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> getCustomerById(Long customerId);
    Customer getCustomerByIdWithPessimisticLock(Long customerId);
    Customer saveCustomer(Customer customer);
    List<Customer> getAllCustomers();
    void deleteCustomer(Long customerId);
}
