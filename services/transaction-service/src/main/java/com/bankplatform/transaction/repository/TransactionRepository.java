package com.bankplatform.transaction.repository;

import com.bankplatform.transaction.entity.Transaction;
import com.bankplatform.transaction.entity.TransactionStatus;
import com.bankplatform.transaction.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByReference(String reference);
    List<Transaction> findBySourceAccountId(UUID accountId);
    List<Transaction> findByDestinationAccountId(UUID accountId);
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findByType(TransactionType type);
    List<Transaction> findByInitiatedBy(UUID userId);
}
