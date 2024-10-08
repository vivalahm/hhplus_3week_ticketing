package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.domain.model.Customer;
import com.hhplus.concertticketing.domain.repository.CustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
    private final CustomerJpaRepository customerJpaRepository;
    public CustomerRepositoryImpl(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public Optional<Customer> getCustomerById(Long customerId){
        return customerJpaRepository.findById(customerId);
    }

    @Override
    public Customer getCustomerByIdWithPessimisticLock(Long customerId) {
        return customerJpaRepository.findByIdForUpdate(customerId);
    }

    @Override
    public Customer saveCustomer(Customer customer){
        return customerJpaRepository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return List.of();
    }

    @Override
    public void deleteCustomer(Long userId){
        customerJpaRepository.deleteById(userId);
    }

}
