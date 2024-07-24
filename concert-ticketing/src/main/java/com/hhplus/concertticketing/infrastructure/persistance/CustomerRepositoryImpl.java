package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.repository.CustomerRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
    private final CustomerJpaRepository customerJpaRepository;
    public CustomerRepositoryImpl(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(Long customerId){
        return customerJpaRepository.findById(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByIdWithLock(Long customerId) {
        return customerJpaRepository.getCustomerByIdWithLock(customerId);
    }

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer){
        return customerJpaRepository.save(customer);
    }

    @Override
    @Transactional
    public List<Customer> getAllCustomers() {
        return customerJpaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteCustomer(Long userId){
        customerJpaRepository.deleteById(userId);
    }

}
