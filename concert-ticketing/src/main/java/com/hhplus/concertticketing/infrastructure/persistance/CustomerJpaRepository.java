package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.domain.model.Customer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import org.springframework.data.repository.query.Param;


public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c FROM Customer c WHERE c.id = :customerId")
    Optional<Customer> getCustomerByIdWithLock(Long customerId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Customer c WHERE c.id = :id")
    Customer findByIdForUpdate(@Param("id") Long id);
}
