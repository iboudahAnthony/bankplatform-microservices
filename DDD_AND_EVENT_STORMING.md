# DDD & Event Storming Analysis - Plateforme Bancaire

## 📋 Table of Contents
1. [Domain Overview](#domain-overview)
2. [Bounded Contexts](#bounded-contexts)
3. [Aggregates & Entities](#aggregates--entities)
4. [Value Objects](#value-objects)
5. [Domain Events](#domain-events)
6. [Event Storming Timeline](#event-storming-timeline)
7. [Integration Patterns](#integration-patterns)

---

## Domain Overview

**Problem Statement:** Manage a distributed banking platform supporting multiple account types, transactions, loans, and customer management across multiple microservices.

**Core Domains:**
- **Customer Domain** - Customer onboarding and profile management
- **Account Domain** - Account lifecycle, balance management, and limits
- **Transaction Domain** - Money transfers and transaction processing
- **Loan Domain** - Loan applications, approvals, and repayments
- **Authentication Domain** - User identity and access control
- **Document Domain** - Document verification and OCR processing
- **Notification Domain** - Event-driven notifications

---

## Bounded Contexts

### 1. **Authentication & Identity Bounded Context**
**Responsibility:** User authentication, authorization, and session management

**Key Entities:**
- `User` - Root Aggregate
  - email (unique)
  - password (hashed)
  - firstName, lastName
  - phoneNumber
  - role (CLIENT, OPERATOR_ADMIN, OPERATOR_ANALYST, SUPER_ADMIN)
  - customerId or operatorId (foreign reference)
  - isActive, failedAttempts
  - lastLoginAt

- `RefreshToken` - Entity
  - userId
  - token
  - expiresAt
  - isRevoked

**Invariants:**
- Email must be unique
- Password must be hashed
- Only one active refresh token per session
- Role determines authorization level

**Integration Points:**
- ← Triggered by: Customer Registration (Customer Context)
- ← Triggered by: Operator Onboarding (Customer Context)
- → Events published: UserCreated, UserActivated, UserDeactivated, LoginAttempted

---

### 2. **Customer Bounded Context**
**Responsibility:** Customer lifecycle, onboarding, and profile management

**Key Entities:**
- `Customer` - Root Aggregate
  - userId (foreign reference to Auth)
  - firstName, lastName
  - email, phoneNumber
  - dateOfBirth
  - nationality, address, city, country
  - identityDocument, documentNumber
  - creditScore (default: 500)
  - status (ACTIVE, SUSPENDED, CLOSED, KYC_PENDING)
  - operatorId (assigned operator)

- `Operator` - Root Aggregate
  - userId (foreign reference to Auth)
  - firstName, lastName
  - email
  - department
  - role (ADMIN, ANALYST)

**Invariants:**
- Customer must have KYC verification (Document Service)
- Credit score must be between 0-1000
- Each customer has exactly one assigned operator
- Email must be unique

**Business Rules:**
- New customer starts with KYC_PENDING status
- Operator must approve KYC before account opening
- Credit score impacts loan eligibility
- Suspended customers cannot perform transactions

**Integration Points:**
- ← Triggered by: User Registration (Auth Context)
- ← Triggered by: Document Verification (Document Context)
- → Events published: CustomerCreated, CustomerKYCApproved, CustomerKYCRejected, CustomerSuspended, CreditScoreUpdated

---

### 3. **Account Bounded Context**
**Responsibility:** Account creation, balance management, and transaction processing

**Key Entities:**
- `Account` - Root Aggregate
  - accountNumber (unique, IBAN format)
  - customerId (foreign reference)
  - type (COURANT, EPARGNE, MOBILE_MONEY)
  - balance (BigDecimal, precision 20.2)
  - currency (default: XOF)
  - dailyLimit (1,000,000 XOF default)
  - monthlyLimit (5,000,000 XOF default)
  - status (ACTIVE, FROZEN, CLOSED, PENDING)
  - operatorId (opened by)
  - openedAt, updatedAt

**Value Objects:**
- `Money` - amount + currency
- `AccountNumber` - generated IBAN
- `TransactionLimit` - daily + monthly allowance

**Invariants:**
- Account number must be unique
- Balance cannot be negative
- Daily limit ≤ Monthly limit
- Only ACTIVE accounts can perform transactions
- Frozen accounts can receive but not send

**Business Rules:**
- COURANT: Checking account with high transaction limits
- EPARGNE: Savings account with withdrawal restrictions
- MOBILE_MONEY: Mobile banking with lower limits
- Account status transitions: PENDING → ACTIVE → FROZEN/CLOSED

**Integration Points:**
- ← Triggered by: AccountOpeningRequested (Customer Context)
- ← Triggered by: MoneyReceived (Transaction Context)
- ← Triggered by: MoneyDeducted (Transaction Context)
- → Events published: AccountOpened, AccountActivated, AccountFrozen, AccountClosed, BalanceUpdated, LimitUpdated

---

### 4. **Transaction Bounded Context**
**Responsibility:** Money transfer processing and transaction lifecycle management

**Key Entities:**
- `Transaction` - Root Aggregate
  - reference (unique transaction ID)
  - type (TRANSFER, DEPOSIT, WITHDRAWAL, LOAN_REPAYMENT, INTEREST)
  - sourceAccountId
  - destinationAccountId
  - amount
  - fees (calculated based on transaction type)
  - commission (for operators)
  - currency (XOF)
  - status (PENDING, INITIATED, COMPLETED, FAILED, REVERSED)
  - description
  - operatorSourceId, operatorDestinationId
  - initiatedBy (user who initiated)
  - initiatedAt, completedAt

**Value Objects:**
- `TransactionReference` - unique identifier
- `Money` - amount + currency
- `TransactionFees` - fee calculation rules

**Invariants:**
- Transaction reference must be unique
- Amount must be > 0
- Source and destination cannot be same account
- Transaction can only transition through valid states
- Funds must be available before completion

**Business Rules:**
- Transaction States Flow: PENDING → INITIATED → COMPLETED
  - On Failure: PENDING → FAILED → REVERSED (with refund)
- Fees calculation:
  - TRANSFER: 0.5% of amount
  - WITHDRAWAL: 1% of amount
  - DEPOSIT: Free
  - LOAN_REPAYMENT: 0% (no fees)
- Daily limit check before completing transfer
- Balance validation before transaction

**Integration Points:**
- ← Triggered by: User initiates transfer (from API)
- ← Triggered by: Loan repayment (Loan Context)
- ← Triggered by: MoneyReceived (for deposits)
- → Events published: TransactionInitiated, TransactionCompleted, TransactionFailed, TransactionReversed, FeesCalculated, CommissionCalculated

---

### 5. **Loan Bounded Context**
**Responsibility:** Loan application, approval workflow, and repayment management

**Key Entities:**
- `Loan` - Root Aggregate
  - customerId
  - accountId
  - operatorId (loan officer)
  - amount (principal)
  - interestRate (% per annum, default: 8%)
  - durationMonths
  - monthlyPayment (calculated)
  - totalAmount (amount + interest)
  - status (SUBMITTED, PENDING_REVIEW, APPROVED, REJECTED, ACTIVE, CLOSED)
  - creditScore
  - purpose (home, car, business, education, etc.)
  - rejectionReason
  - requestedAt, approvedAt, closedAt

- `Repayment` - Entity (child of Loan)
  - loanId
  - dueDate
  - amount
  - status (PENDING, PAID, OVERDUE)
  - paidAt
  - paymentReference

**Value Objects:**
- `LoanAmount` - principal amount
- `InterestRate` - percentage
- `RepaymentSchedule` - monthly payment plan
- `CreditScore` - borrower credit rating

**Invariants:**
- Loan amount must be positive
- Interest rate between 3% - 25%
- Duration between 3 - 60 months
- Credit score required for approval
- Monthly payment = (amount × (1 + interest)) / duration

**Business Rules:**
- Minimum credit score 400 for loan eligibility
- Loan approval workflow:
  1. SUBMITTED → stored in DB
  2. PENDING_REVIEW → awaiting operator review
  3. APPROVED/REJECTED → based on credit score & operator decision
- If APPROVED: status becomes ACTIVE when first payment due
- Repayment scheduling: calculate monthly payment
- Auto-transition to CLOSED when all repayments completed
- OVERDUE status if payment missed

**Integration Points:**
- ← Triggered by: LoanApplicationSubmitted (from UI)
- ← Triggered by: RepaymentInitiated (Notification/Manual)
- → Events published: LoanApplicationSubmitted, LoanApproved, LoanRejected, LoanActivated, RepaymentScheduleCreated, RepaymentProcessed, LoanClosed

---

### 6. **Document & OCR Bounded Context**
**Responsibility:** Document verification, OCR processing, and KYC validation

**Key Entities:**
- `Document` - Root Aggregate
  - customerId
  - documentType (IDENTITY, PASSPORT, DRIVING_LICENSE, PROOF_OF_ADDRESS)
  - filePath
  - fileName
  - fileSize
  - uploadedAt
  - verificationStatus (PENDING, PROCESSING, VERIFIED, REJECTED)
  - ocrResult (JSON extracted data)
  - extractedData (name, number, expiry, etc.)
  - verifiedBy (operator)
  - verifiedAt
  - rejectionReason

**Value Objects:**
- `DocumentType` - category enum
- `VerificationStatus` - lifecycle state
- `OCRResult` - extracted text/data

**Invariants:**
- Document must be uploaded before verification
- OCR processing is asynchronous
- Verification requires operator approval
- One primary document per customer

**Business Rules:**
- KYC requires at least one verified identity document
- Auto-reject if document expired
- OCR extraction triggers on upload
- Verification triggers KYC update in Customer Context

**Integration Points:**
- ← Triggered by: CustomerOnboarding (Customer Context)
- ← Triggered by: DocumentUploadRequested (from UI)
- → Events published: DocumentUploaded, OCRProcessingStarted, OCRCompleted, DocumentVerified, DocumentRejected, KYCApprovalTriggered

---

### 7. **Notification Bounded Context**
**Responsibility:** Event-driven notifications via Email, SMS, and Push

**Key Entities:**
- `Notification` - Root Aggregate
  - userId/customerId
  - type (EMAIL, SMS, PUSH)
  - channel (EMAIL, SMS, PUSH_NOTIFICATION)
  - subject
  - content
  - templateId
  - status (PENDING, SENT, FAILED, RETRYING)
  - sentAt
  - attempts
  - lastError

**Value Objects:**
- `NotificationType` - category
- `Channel` - delivery method

**Invariants:**
- Notification must have valid recipient
- Max 3 retry attempts
- Subject + content required

**Business Rules:**
- Listen to all domain events (Event Bus)
- Route to appropriate notification channel
- Retry failed notifications with exponential backoff
- Template-based message generation

**Events Consumed:**
- UserCreated → Send welcome email
- AccountOpened → Send account confirmation
- TransactionCompleted → Send transaction receipt
- LoanApproved → Send loan approval notification
- RepaymentDue → Send payment reminder
- DocumentVerified → Send KYC approval
- DocumentRejected → Send resubmission request

---

## Aggregates & Entities

### Aggregate Summary

| Bounded Context | Root Aggregate | Key Entities | Child Value Objects |
|---|---|---|---|
| **Authentication** | User | RefreshToken | UserRole, UserStatus |
| **Customer** | Customer | - | CustomerStatus, CreditScore |
| **Customer** | Operator | - | OperatorRole, Department |
| **Account** | Account | - | AccountType, AccountStatus, Money, TransactionLimit |
| **Transaction** | Transaction | - | TransactionType, TransactionStatus, Money, TransactionFees |
| **Loan** | Loan | Repayment | LoanStatus, InterestRate, RepaymentSchedule |
| **Document** | Document | - | DocumentType, VerificationStatus, OCRResult |
| **Notification** | Notification | - | NotificationType, Channel, NotificationStatus |

### Aggregate Design Principles Applied

✅ **Single Responsibility:** Each aggregate has a clear domain purpose  
✅ **Invariant Consistency:** Business rules enforced within aggregates  
✅ **Loose Coupling:** Inter-aggregate communication via events  
✅ **Transactional Boundaries:** Aggregate = Transaction boundary  
✅ **Foreign Key References:** Only by ID (not direct object references)  

---

## Value Objects

### Shared Value Objects

```
Money
├── amount: BigDecimal
├── currency: String (ISO 4217)
└── operations: add(), subtract(), multiply()

AccountNumber (IBAN)
├── country: String
├── bank: String
├── checkDigits: String
└── accountId: String

TransactionReference
├── timestamp: Long
├── sequenceNumber: Long
├── checksum: String

TransactionFees
├── basePercentage: BigDecimal
├── transactionType: TransactionType
└── calculate(amount): Money

InterestRate
├── percentage: BigDecimal
├── minRate: 3%
├── maxRate: 25%
└── validate(): Boolean
```

### Context-Specific Value Objects

**Authentication Domain:**
- `UserRole` - enum: CLIENT, OPERATOR_ADMIN, OPERATOR_ANALYST, SUPER_ADMIN

**Customer Domain:**
- `CustomerStatus` - enum: ACTIVE, SUSPENDED, CLOSED, KYC_PENDING
- `CreditScore` - int (0-1000)

**Account Domain:**
- `AccountType` - enum: COURANT, EPARGNE, MOBILE_MONEY
- `AccountStatus` - enum: ACTIVE, FROZEN, CLOSED, PENDING

**Transaction Domain:**
- `TransactionType` - enum: TRANSFER, DEPOSIT, WITHDRAWAL, LOAN_REPAYMENT, INTEREST
- `TransactionStatus` - enum: PENDING, INITIATED, COMPLETED, FAILED, REVERSED

**Loan Domain:**
- `LoanStatus` - enum: SUBMITTED, PENDING_REVIEW, APPROVED, REJECTED, ACTIVE, CLOSED
- `RepaymentStatus` - enum: PENDING, PAID, OVERDUE

**Document Domain:**
- `DocumentType` - enum: IDENTITY, PASSPORT, DRIVING_LICENSE, PROOF_OF_ADDRESS
- `VerificationStatus` - enum: PENDING, PROCESSING, VERIFIED, REJECTED
- `OCRResult` - JSON object with extracted data

---

## Domain Events

### Event Hierarchy

```
DomainEvent (abstract)
├── UserCreated
├── UserActivated
├── UserDeactivated
├── LoginAttempted
│
├── CustomerCreated
├── CustomerKYCApproved
├── CustomerKYCRejected
├── CustomerSuspended
├── CreditScoreUpdated
│
├── AccountOpened
├── AccountActivated
├── AccountFrozen
├── AccountClosed
├── BalanceUpdated
├── DailyLimitUpdated
├── MonthlyLimitUpdated
│
├── TransactionInitiated
├── TransactionCompleted
├── TransactionFailed
├── TransactionReversed
├── FeesCalculated
├── CommissionCalculated
│
├── LoanApplicationSubmitted
├── LoanApproved
├── LoanRejected
├── LoanActivated
├── RepaymentScheduleCreated
├── RepaymentProcessed
├── RepaymentOverdue
├── LoanClosed
│
├── DocumentUploaded
├── OCRProcessingStarted
├── OCRCompleted
├── DocumentVerified
├── DocumentRejected
│
└── NotificationSent
    ├── EmailSent
    ├── SMSSent
    └── PushNotificationSent
```

### Event Structure

```java
DomainEvent {
  eventId: UUID
  eventType: String
  aggregateId: UUID
  aggregateType: String
  timestamp: LocalDateTime
  version: Integer
  payload: {
    // Domain-specific data
  }
  correlationId: UUID (for distributed tracing)
  causationId: UUID (what caused this event)
}
```

### Event Examples

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "eventType": "AccountOpened",
  "aggregateId": "770e8400-e29b-41d4-a716-446655440001",
  "aggregateType": "Account",
  "timestamp": "2024-06-12T10:30:00Z",
  "version": 1,
  "payload": {
    "accountNumber": "XF0111234567890",
    "customerId": "550e8400-e29b-41d4-a716-446655440002",
    "accountType": "COURANT",
    "initialBalance": 0.00,
    "currency": "XOF",
    "status": "PENDING"
  }
}
```

---

## Event Storming Timeline

### User Journey: "New Customer Opens Account and Transfers Money"

```
Timeline:
─────────────────────────────────────────────────────────────────

T0: User Registration
  │
  ├─ User submits registration form
  ├─ [Command] RegisterUserCommand
  ├─ [Event] UserCreated (email, firstName, lastName, role=CLIENT)
  │
  └─ [System] Auto-send welcome email
      └─ NotificationService consumes UserCreated

T1: KYC Verification
  │
  ├─ User uploads identity document
  ├─ [Command] UploadDocumentCommand
  ├─ [Event] DocumentUploaded
  │
  ├─ [System] OCR Processing (async)
  │   └─ [Event] OCRProcessingStarted
  │   └─ [Event] OCRCompleted (extracts name, ID number, expiry)
  │
  ├─ Operator verifies document
  ├─ [Command] VerifyDocumentCommand
  ├─ [Event] DocumentVerified
  │
  └─ [System] Trigger KYC approval
      └─ [Command] ApproveKYCCommand
      └─ [Event] CustomerKYCApproved (customerId, status=ACTIVE)

T2: Customer Profile Creation
  │
  ├─ [Command] CreateCustomerCommand (triggered by UserCreated)
  ├─ [Event] CustomerCreated (customerId, userId, creditScore=500)
  │
  └─ [System] Assign operator
      └─ [Event] OperatorAssigned

T3: Account Opening
  │
  ├─ User selects account type (COURANT)
  ├─ [Command] OpenAccountCommand
  ├─ [Event] AccountOpened
  │   └─ accountNumber: XF0111234567890
  │   └─ status: PENDING
  │   └─ balance: 0.00
  │
  ├─ [System] Operator approves account
  ├─ [Command] ApproveAccountCommand
  ├─ [Event] AccountActivated (status → ACTIVE)
  │
  └─ [System] Send confirmation email
      └─ NotificationService: "Account activated successfully"

T4: Deposit Money
  │
  ├─ User deposits 100,000 XOF via mobile money
  ├─ [Command] DepositMoneyCommand
  ├─ [Event] TransactionInitiated
  │   └─ type: DEPOSIT
  │   └─ amount: 100,000
  │   └─ status: INITIATED
  │
  ├─ [System] Process deposit
  │ ├─ Calculate fees: 0 (deposit free)
  │ └─ [Event] FeesCalculated (fees: 0.00)
  │
  ├─ [Command] CompleteTransactionCommand
  ├─ [Event] TransactionCompleted
  │   └─ status: COMPLETED
  │   └─ completedAt: 2024-06-12T10:35:00Z
  │
  ├─ [System] Update account balance
  ├─ [Command] UpdateBalanceCommand
  ├─ [Event] BalanceUpdated
  │   └─ newBalance: 100,000.00
  │   └─ previousBalance: 0.00
  │
  └─ [System] Send transaction receipt
      └─ NotificationService: "Deposit confirmed: 100,000 XOF"

T5: Transfer Money to Another Account
  │
  ├─ User initiates transfer
  │  ├─ destinationAccount: XF0111234567891
  │  └─ amount: 50,000 XOF
  │
  ├─ [System] Validate:
  │  ├─ Source account active? ✓
  │  ├─ Sufficient balance? ✓ (100,000 ≥ 50,000)
  │  ├─ Daily limit not exceeded? ✓
  │  └─ Destination account exists? ✓
  │
  ├─ [Command] InitiateTransferCommand
  ├─ [Event] TransactionInitiated
  │   └─ type: TRANSFER
  │   └─ sourceAccountId: 770e8400-e29b-41d4-a716-446655440001
  │   └─ destinationAccountId: 770e8400-e29b-41d4-a716-446655440099
  │   └─ amount: 50,000
  │   └─ status: INITIATED
  │   └─ status: PENDING
  │
  ├─ [System] Calculate fees (0.5%)
  │  └─ [Event] FeesCalculated (fees: 250 XOF)
  │
  ├─ [System] Deduct from source account
  │  ├─ [Command] DeductMoneyCommand
  │  ├─ [Event] MoneyDeducted (amount: 50,000 + 250 = 50,250 XOF)
  │  └─ [Event] BalanceUpdated (newBalance: 49,750 XOF)
  │
  ├─ [System] Credit to destination account
  │  ├─ [Command] AddMoneyCommand
  │  ├─ [Event] MoneyReceived (amount: 50,000 XOF)
  │  └─ [Event] BalanceUpdated (newBalance: 50,000 XOF)
  │
  ├─ [Command] CompleteTransactionCommand
  ├─ [Event] TransactionCompleted
  │   └─ status: COMPLETED
  │   └─ completedAt: 2024-06-12T10:40:00Z
  │
  ├─ [System] Send notifications
  │  ├─ Source: "Transfer sent: 50,000 XOF (fee: 250 XOF)"
  │  └─ Destination: "Money received: 50,000 XOF"
  │
  └─ [Operator] Commission calculated
      └─ [Event] CommissionCalculated (operator receives share)

T6: Loan Application
  │
  ├─ User applies for 500,000 XOF loan
  ├─ [Command] SubmitLoanApplicationCommand
  │  ├─ amount: 500,000
  │  ├─ duration: 24 months
  │  └─ purpose: home
  │
  ├─ [Event] LoanApplicationSubmitted
  │   └─ status: SUBMITTED
  │   └─ creditScore: 500
  │
  ├─ [System] Trigger operator review
  │  └─ [Command] ReviewLoanCommand (sent to queue)
  │  └─ Operator reviews creditScore, financial history
  │
  ├─ [Command] ApproveLoanCommand
  ├─ [Event] LoanApproved
  │   └─ status: APPROVED
  │   └─ approvedAmount: 500,000 XOF
  │   └─ interestRate: 8%
  │   └─ totalAmount: 540,000 XOF (with interest)
  │
  ├─ [System] Generate repayment schedule
  │  └─ 24 monthly payments of 22,500 XOF
  │  └─ First payment due: 2024-07-12
  │
  ├─ [Event] RepaymentScheduleCreated
  │   └─ 24 repayments scheduled
  │
  ├─ [System] Disberse loan amount
  │  ├─ [Command] DisburseLoanCommand
  │  ├─ Create internal transaction (Loan Account → User Account)
  │  ├─ [Event] TransactionInitiated (type: LOAN_DISBURSEMENT)
  │  ├─ [Event] TransactionCompleted
  │  ├─ [Event] MoneyReceived (500,000 XOF to user account)
  │  └─ [Event] BalanceUpdated
  │
  └─ [System] Send notifications
      └─ "Loan approved and disbursed: 500,000 XOF"

T7: Loan Repayment (First Payment Due)
  │
  ├─ [System] Automatic reminder 5 days before due
  │  └─ [Event] RepaymentReminder
  │  └─ NotificationService: "Payment due: 22,500 XOF on 2024-07-12"
  │
  ├─ User makes repayment
  ├─ [Command] ProcessRepaymentCommand
  │  └─ repaymentAmount: 22,500 XOF
  │
  ├─ [System] Create repayment transaction
  │  ├─ [Event] TransactionInitiated
  │  │   └─ type: LOAN_REPAYMENT
  │  │   └─ amount: 22,500
  │  │   └─ fees: 0 (loan repayments are free)
  │  │
  │  ├─ [Event] FeesCalculated (fees: 0.00)
  │  ├─ [Event] TransactionCompleted
  │  └─ [Event] MoneyDeducted (22,500 from user account)
  │
  ├─ [System] Update loan repayment
  │  ├─ [Command] MarkRepaymentAsPaidCommand
  │  ├─ [Event] RepaymentProcessed
  │  └─ Update remaining loan balance
  │
  └─ [System] Send confirmation
      └─ "Repayment received: 22,500 XOF"

T8: Loan Closure (After 24 Months)
  │
  ├─ Final repayment completed
  ├─ [System] Check if all repayments done
  │  └─ Total paid: 540,000 XOF ✓
  │
  ├─ [Command] CloseLoanCommand
  ├─ [Event] LoanClosed
  │   └─ status: CLOSED
  │   └─ closedAt: 2024-06-12
  │
  └─ [System] Send confirmation
      └─ "Loan fully repaid and closed"
```

### Event Sequence Diagram

```
User           API         Command    Event        Account      Transaction   Notification
 │              │          Handler    Store        Service      Service        Service
 │ Register      │             │         │             │             │             │
 ├──────────────>│             │         │             │             │             │
 │               ├──RegisterUser────────>│             │             │             │
 │               │             │         │             │             │             │
 │               │             │      UserCreated ────────────────────────────────>│
 │               │             │         │             │             │             │
 │               │<──────────OK─────────|             │             │             ├──Welcome Email
 │               │             │         │             │             │             │
 │ Upload Doc    │             │         │             │             │             │
 ├──────────────>│             │         │             │             │             │
 │               ├──UploadDoc───────────>│             │             │             │
 │               │             │         │             │             │             │
 │               │             │      DocumentUploaded────────────────────────────>│
 │               │             │         │             │             │             │
 │               │             │      OCRProcessing ───>│ [async]     │             │
 │               │             │         │             │             │             │
 │               │<──────────OK─────────|             │             │             │
 │               │             │         │             │             │             │
 │               │             │      OCRCompleted     │             │             │
 │               │             │         │             │             │             │
 │ (Operator     │             │         │             │             │             │
 │  reviews)     │             │         │             │             │             │
 │ Verify Doc    │             │         │             │             │             │
 ├──────────────>│             │         │             │             │             │
 │               ├──VerifyDoc───────────>│             │             │             │
 │               │             │         │             │             │             │
 │               │             │      DocumentVerified ────────────────────────────>│
 │               │             │         │             │             │             │
 │               │             │         │        [Create Customer]  │             │
 │               │             │      CustomerCreated  │             │             │
 │               │             │         │             │             │             │
 │ Open Account  │             │         │             │             │             │
 ├──────────────>│             │         │             │             │             │
 │               ├──OpenAccount──────────────────────────>│           │             │
 │               │             │         │             │             │             │
 │               │             │      AccountOpened    │<────────────┤             │
 │               │             │         │             │             │             │
 │               │<──────────OK─────────|             │             │             │
 │               │             │         │             │             │             │
 │               │             │      AccountActivated ────────────────────────────>│
 │               │             │         │             │             │             │ Confirmation
 │               │             │         │             │             │             │ Email
 │               │             │         │             │             │             │
 │ Deposit Money │             │         │             │             │             │
 ├──────────────>│             │         │             │             │             │
 │               ├────Deposit───────────────────────────────────────>│             │
 │               │             │         │             │        TransactionInitiated
 │               │             │         │             │             │             │
 │               │             │         │             │        [Calculate Fees]   │
 │               │             │      FeesCalculated   │             │             │
 │               │             │         │             │             │             │
 │               │             │         │             │        TransactionCompleted
 │               │             │         │             │             │             │
 │               │             │      BalanceUpdated   │<────────────┤             │
 │               │             │         │             │             │             │
 │               │<────OK───────────────|             │             │             │
 │               │             │         │             │             │             ├──Receipt
 │               │             │         │             │             │             │
 │ Transfer      │             │         │             │             │             │
 ├──────────────>│             │         │             │             │             │
 │               ├─Transfer────────────────────────────────────────>│             │
 │               │             │         │             │        TransactionInitiated
 │               │             │         │             │             │             │
 │               │             │         │             │        [Validate Limits]  │
 │               │             │         │             │        [Calculate Fees]   │
 │               │             │         │             │        [Deduct & Credit] │
 │               │             │         │             │        TransactionCompleted
 │               │             │         │             │             │             │
 │               │             │      BalanceUpdated   │<────────────┤             │
 │               │             │         │             │             │             │
 │               │<────OK───────────────|             │             │             │
 │               │             │         │             │             │             ├──Notifications
```

---

## Integration Patterns

### 1. **Event-Driven Async Communication**

**Pattern:** Domain Events → Kafka → Services

```
Account Service publishes → Event Bus (Kafka)
                            ↓
                    [AccountOpened Event]
                            ↓
                    ┌───────┴────────┬──────────┐
                    ↓                ↓          ↓
            Notification    Audit    Analytics
            Service         Service  Service
            (send email)    (log)    (track KPIs)
```

**Benefits:**
- Loose coupling between services
- Scalable and resilient
- Event sourcing capable
- Audit trail provided

### 2. **Synchronous API Calls (Request/Response)**

**Pattern:** API Gateway → Service A → Service B

```
User Request → API Gateway → Account Service → validates → OK/Error
                             ↓
                        (uses events for side effects)
```

**Scenarios:**
- Query operations (GET Account balance)
- Validation checks (verify sufficient funds)
- Immediate responses needed (account status)

### 3. **SAGA Pattern for Distributed Transactions**

**Example: Money Transfer Across Services**

```
Transfer SAGA:
┌─────────────────────────────────────────┐
│ 1. TransactionService                   │
│    └─> TransactionInitiated Event       │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│ 2. AccountService (listener)             │
│    ├─> Deduct from source account       │
│    ├─> (if fails, compensate)            │
│    └─> MoneyDeducted Event               │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│ 3. AccountService (listener)             │
│    ├─> Add to destination account       │
│    ├─> (if fails, compensate)            │
│    └─> MoneyReceived Event               │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│ 4. TransactionService (listener)         │
│    ├─> Mark transaction COMPLETED       │
│    └─> TransactionCompleted Event       │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│ 5. NotificationService (listener)        │
│    └─> Send confirmation emails         │
└─────────────────────────────────────────┘
```

**Compensation (on failure):**
```
If Step 3 fails:
  ├─> TransactionFailed Event
  ├─> Refund source account (MoneyReceived)
  ├─> TransactionReversed Event
  └─> NotificationService: "Transfer failed, funds returned"
```

### 4. **Request/Reply with Correlation ID**

```
LoanService → ReviewerQueue → [Async Worker] → ResponseQueue
  (puts request with correlationId)         (processes async)
     ↓                                             ↓
  [waits for response with same correlationId]
```

---

## Event Store Design

### Event Stream Structure

```
EventStream: Transaction:550e8400-e29b-41d4-a716-446655440000
├── Version 1: TransactionInitiated (2024-06-12 10:35:00)
├── Version 2: FeesCalculated (2024-06-12 10:35:01)
├── Version 3: MoneyDeducted (2024-06-12 10:35:02)
├── Version 4: MoneyReceived (2024-06-12 10:35:03)
└── Version 5: TransactionCompleted (2024-06-12 10:35:04)

EventStream: Account:770e8400-e29b-41d4-a716-446655440001
├── Version 1: AccountOpened (2024-06-12 10:00:00)
├── Version 2: AccountActivated (2024-06-12 10:05:00)
├── Version 3: BalanceUpdated (2024-06-12 10:35:03)
│   └��� newBalance: 100,000.00
│   └─ previousBalance: 100,250.00
├── Version 4: BalanceUpdated (2024-06-12 10:35:04)
│   └─ newBalance: 49,750.00
│   └─ previousBalance: 100,000.00
└── Version 5: DailyLimitUpdated (2024-06-12 10:35:05)
    └─ newLimit: 950,000.00
    └─ previousLimit: 1,000,000.00
```

### Event Sourcing Benefits

✅ Complete audit trail - all state changes recorded  
✅ Time travel - reconstruct state at any point  
✅ Debugging - replay events to find bugs  
✅ Reporting - create custom views from events  
✅ Scalability - eventual consistency  

---

## Context Mapping

### Relationships Between Bounded Contexts

```
                        ┌──────────────────────┐
                        │  Authentication      │
                        │  & Identity          │
                        └───────┬──────────────┘
                                │
                    [uses]      │      [creates]
                                │
                    ┌───────────▼──────────────┐
                    │   Customer              │
                    │   (Customer Profile)    │
                    └───┬───────────────┬─────┘
                        │               │
            [opens]     │               │ [triggers KYC]
                        │               │
                    ┌───▼──────┐   ┌────▼─────────┐
                    │ Account  │   │   Document   │
                    │ (Core)   │   │   & OCR      │
                    └──┬───────┘   └──────────────┘
                       │
        [processes]    │
                       │
                    ┌──▼──────────┐
                    │ Transaction │
                    │ (Payment)   │
                    └──┬──────────┘
                       │
        [repayment]    │
                       │
                    ┌──▼──────────┐
                    │   Loan      │
                    │ (Lending)   │
                    └─────────────┘

        All contexts publish to → [Event Bus] → Notification Service
```

### Integration Types

| From | To | Pattern | Type |
|------|----|---------|----|
| Customer | Account | Event: CustomerCreated | Async |
| Account | Transaction | Event: AccountActivated | Async |
| Transaction | Account | Event: TransactionCompleted | Async |
| Loan | Transaction | Event: RepaymentInitiated | Async |
| Document | Customer | Event: DocumentVerified | Async |
| All | Notification | Event-driven | Async |

---

## Key DDD Patterns Applied

### 1. **Ubiquitous Language**
- **Account Types:** COURANT (checking), EPARGNE (savings), MOBILE_MONEY
- **Account Status:** ACTIVE, FROZEN, CLOSED, PENDING
- **Transaction Status:** INITIATED, COMPLETED, FAILED, REVERSED
- **Loan Status:** SUBMITTED, PENDING_REVIEW, APPROVED, REJECTED, ACTIVE, CLOSED

### 2. **Aggregates with Invariants**
- Account balance cannot go negative
- Transaction source ≠ destination
- Loan monthly payment calculated correctly
- Customer KYC must be verified before account activation

### 3. **Value Objects**
- Money (amount + currency)
- TransactionReference (unique ID)
- InterestRate (percentage with limits)
- AccountNumber (IBAN format)

### 4. **Domain Events**
- Capture business-meaningful state changes
- Enable event sourcing
- Drive async workflows
- Create audit trail

### 5. **Repositories**
- Account repositories in Account Service
- Transaction repositories in Transaction Service
- Customer repositories in Customer Service
- Each bounded context manages own data

### 6. **Application Services**
- Orchestrate domain logic
- Handle commands
- Publish events
- Coordinate with other services

---

## Implementation Roadmap

### Phase 1: Foundation (Events & Aggregates)
- [ ] Define event schema and event store
- [ ] Implement User and Customer aggregates
- [ ] Create event publishers in each service
- [ ] Set up Kafka topics

### Phase 2: Core Transactions (Account + Transaction)
- [ ] Implement Account aggregate with invariants
- [ ] Build Transaction aggregate with status machine
- [ ] Implement Account balance updates via events
- [ ] Add transaction fee calculations

### Phase 3: Lending (Loans)
- [ ] Create Loan aggregate
- [ ] Build Repayment schedule generator
- [ ] Implement loan approval workflow
- [ ] Add repayment processing

### Phase 4: Cross-Service Coordination
- [ ] Implement SAGA for money transfers
- [ ] Add compensation handlers
- [ ] Implement event correlation IDs
- [ ] Build distributed transaction logs

### Phase 5: Advanced Features
- [ ] Event sourcing implementation
- [ ] Read model projections
- [ ] Time travel/replay capabilities
- [ ] Advanced analytics from events

---

## References & Best Practices

**DDD Resources:**
- "Domain-Driven Design" by Eric Evans
- "Implementing Domain-Driven Design" by Vaughn Vernon
- Microservices.io - Event Sourcing pattern

**Event Storming Workshop:**
- Identify all domain events
- Create timeline for user journeys
- Map commands to events
- Define aggregates from events
- Establish integration points

**Monitoring & Observability:**
- Track event flow through Kafka
- Monitor service latency
- Alert on transaction failures
- Audit trail for compliance

---

**Document Created:** 2024-06-12  
**Last Updated:** 2024-06-12  
**Version:** 1.0
