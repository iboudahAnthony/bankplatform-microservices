package com.bankplatform.account.repository;

import com.bankplatform.account.entity.Account;
import com.bankplatform.account.entity.AccountStatus;
import com.bankplatform.account.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByCustomerId(UUID customerId);
    List<Account> findByOperatorId(UUID operatorId);
    List<Account> findByStatus(AccountStatus status);
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByCustomerIdAndType(UUID customerId, AccountType type);
    boolean existsByAccountNumber(String accountNumber);
}
