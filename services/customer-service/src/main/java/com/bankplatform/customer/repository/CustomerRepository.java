package com.bankplatform.customer.repository;

import com.bankplatform.customer.entity.Customer;
import com.bankplatform.customer.entity.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByUserId(UUID userId);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    List<Customer> findByStatus(CustomerStatus status);
    List<Customer> findByOperatorId(UUID operatorId);
}
