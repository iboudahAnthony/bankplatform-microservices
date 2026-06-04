package com.bankplatform.account.dto;

import com.bankplatform.account.entity.Account;
import com.bankplatform.account.entity.AccountStatus;
import com.bankplatform.account.entity.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AccountResponse {

    private UUID id;
    private String accountNumber;
    private UUID customerId;
    private UUID operatorId;
    private AccountType type;
    private BigDecimal balance;
    private String currency;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
    private AccountStatus status;
    private LocalDateTime openedAt;

    public AccountResponse() {}

    public static AccountResponse from(Account a) {
        AccountResponse r = new AccountResponse();
        r.id = a.getId();
        r.accountNumber = a.getAccountNumber();
        r.customerId = a.getCustomerId();
        r.operatorId = a.getOperatorId();
        r.type = a.getType();
        r.balance = a.getBalance();
        r.currency = a.getCurrency();
        r.dailyLimit = a.getDailyLimit();
        r.monthlyLimit = a.getMonthlyLimit();
        r.status = a.getStatus();
        r.openedAt = a.getOpenedAt();
        return r;
    }

    // Getters
    public UUID getId()                { return id; }
    public String getAccountNumber()   { return accountNumber; }
    public UUID getCustomerId()        { return customerId; }
    public UUID getOperatorId()        { return operatorId; }
    public AccountType getType()       { return type; }
    public BigDecimal getBalance()     { return balance; }
    public String getCurrency()        { return currency; }
    public BigDecimal getDailyLimit()  { return dailyLimit; }
    public BigDecimal getMonthlyLimit(){ return monthlyLimit; }
    public AccountStatus getStatus()   { return status; }
    public LocalDateTime getOpenedAt() { return openedAt; }
}
