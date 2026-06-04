package com.bankplatform.account.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "operator_id")
    private UUID operatorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private String currency = "XOF";

    @Column(name = "daily_limit", precision = 20, scale = 2)
    private BigDecimal dailyLimit = new BigDecimal("1000000");

    @Column(name = "monthly_limit", precision = 20, scale = 2)
    private BigDecimal monthlyLimit = new BigDecimal("5000000");

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @CreationTimestamp
    @Column(name = "opened_at", updatable = false)
    private LocalDateTime openedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Account() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Account a = new Account();
        public Builder accountNumber(String v)   { a.accountNumber = v; return this; }
        public Builder customerId(UUID v)        { a.customerId = v; return this; }
        public Builder operatorId(UUID v)        { a.operatorId = v; return this; }
        public Builder type(AccountType v)       { a.type = v; return this; }
        public Builder balance(BigDecimal v)     { a.balance = v; return this; }
        public Builder currency(String v)        { a.currency = v; return this; }
        public Builder dailyLimit(BigDecimal v)  { a.dailyLimit = v; return this; }
        public Builder monthlyLimit(BigDecimal v){ a.monthlyLimit = v; return this; }
        public Builder status(AccountStatus v)   { a.status = v; return this; }
        public Account build() { return a; }
    }

    // Getters & Setters
    public UUID getId()                          { return id; }
    public String getAccountNumber()             { return accountNumber; }
    public void setAccountNumber(String v)       { this.accountNumber = v; }
    public UUID getCustomerId()                  { return customerId; }
    public void setCustomerId(UUID v)            { this.customerId = v; }
    public UUID getOperatorId()                  { return operatorId; }
    public void setOperatorId(UUID v)            { this.operatorId = v; }
    public AccountType getType()                 { return type; }
    public void setType(AccountType v)           { this.type = v; }
    public BigDecimal getBalance()               { return balance; }
    public void setBalance(BigDecimal v)         { this.balance = v; }
    public String getCurrency()                  { return currency; }
    public void setCurrency(String v)            { this.currency = v; }
    public BigDecimal getDailyLimit()            { return dailyLimit; }
    public void setDailyLimit(BigDecimal v)      { this.dailyLimit = v; }
    public BigDecimal getMonthlyLimit()          { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal v)    { this.monthlyLimit = v; }
    public AccountStatus getStatus()             { return status; }
    public void setStatus(AccountStatus v)       { this.status = v; }
    public LocalDateTime getOpenedAt()           { return openedAt; }
    public LocalDateTime getUpdatedAt()          { return updatedAt; }
}
