# Livrable 2 — Étude DDD Complète
# Domain Driven Design - Plateforme Bancaire

## 1. Identification des Sous-domaines

### Core Domains (Cœur métier — haute valeur)
| Sous-domaine | Description | Justification |
|-------------|-------------|---------------|
| **Customer** | Gestion des clients, KYC, vérification d'identité | Différenciateur clé |
| **Account** | Gestion des comptes, soldes, limites | Cœur de l'activité bancaire |
| **Transaction** | Dépôts, retraits, transferts | Valeur principale pour le client |
| **Loan** | Prêts, scoring, échéanciers | Source de revenus principale |

### Supporting Domains (Soutien)
| Sous-domaine | Description |
|-------------|-------------|
| **Authentication** | Gestion des identités et des accès |
| **Operator** | Règles métier par opérateur, commissions |
| **Document/OCR** | Extraction et vérification de documents |

### Generic Domains (Génériques)
| Sous-domaine | Description |
|-------------|-------------|
| **Notification** | SMS, Email, Push |
| **Audit** | Traçabilité et journalisation |
| **Reporting** | Statistiques et tableaux de bord |

---

## 2. Bounded Contexts et leurs limites

### BC1 — Identity & Authentication
**Service** : auth-service (Java)
**Responsabilités** : Authentification, autorisation, gestion tokens JWT
**Agrégats** :
- `User` (racine) — id, email, passwordHash, role, isActive
- `RefreshToken` — token, expiresAt, isRevoked

**Événements** :
- `UserRegistered` → déclenche création profil client
- `UserLoggedIn` → audit
- `TokenRevoked` → sécurité

---

### BC2 — Customer Management
**Service** : customer-service (Java)
**Responsabilités** : Profil client, statut KYC, score crédit
**Agrégats** :
- `Customer` (racine) — id, userId, firstName, lastName, email, status, creditScore

**Événements** :
- `CustomerCreated`
- `CustomerVerified` → déclenche ouverture compte automatique
- `CustomerSuspended`

---

### BC3 — Account Management
**Service** : account-service (Java)
**Responsabilités** : Comptes bancaires, soldes, limites
**Agrégats** :
- `Account` (racine) — id, accountNumber, customerId, type, balance, status, limits

**Événements** :
- `AccountOpened`
- `AccountDebited`
- `AccountCredited`
- `AccountClosed`

---

### BC4 — Transaction Processing
**Service** : transaction-service (Java)
**Responsabilités** : Exécution des transactions, cohérence des soldes
**Agrégats** :
- `Transaction` (racine) — id, reference, type, amount, status, sourceAccount, destAccount

**Événements** :
- `TransactionInitiated`
- `TransactionCompleted` → Kafka → Notification
- `TransactionFailed` → Kafka → Notification
- `TransactionReversed`

---

### BC5 — Loan Management
**Service** : loan-service (Python)
**Responsabilités** : Demandes de prêts, scoring, échéanciers
**Agrégats** :
- `Loan` (racine) — id, customerId, amount, rate, duration, status, creditScore
- `Repayment` (entité) — dueDate, amount, principal, interest, status

**Événements** :
- `LoanRequested`
- `LoanApproved` → génération échéancier
- `LoanRejected`
- `RepaymentMade`

---

### BC6 — Document & OCR
**Service** : document-service (Python)
**Responsabilités** : Upload, OCR, vérification KYC
**Agrégats** :
- `Document` (racine) — id, customerId, type, extractedInfo, confidenceScore, status

**Événements** :
- `DocumentUploaded`
- `OCRCompleted`
- `KYCPassed`
- `KYCFailed`

---

### BC7 — Notification
**Service** : notification-service (Node.js)
**Responsabilités** : Envoi de notifications multicanal
**Consomme** : Events Kafka (transaction, loan, customer)

---

## 3. Context Map — Relations entre Bounded Contexts

```
[Auth BC] ←──── Conformiste ────── [Customer BC]
                                         │
                              Client/Fournisseur
                                         │
                                   [Account BC]
                                         │
                              Client/Fournisseur
                                         │
                               [Transaction BC]
                                    │     │
                              Kafka │     │ Kafka
                                    ▼     ▼
                            [Notification BC]
                                         
[Customer BC] ──── Client/Fournisseur ──── [Loan BC]
[Loan BC]     ──── Client/Fournisseur ──── [Document BC]
```

### Types de relations
| Source | Cible | Relation | Description |
|--------|-------|----------|-------------|
| Customer | Auth | Conformiste | Customer utilise userId de Auth |
| Account | Customer | Conformiste | Account référence customerId |
| Transaction | Account | Client/Fournisseur | Transaction appelle Account pour débit/crédit |
| Loan | Customer | Client/Fournisseur | Loan vérifie l'éligibilité du client |
| Transaction | Notification | Published Language (Kafka) | Events publiés sur Kafka |
| Loan | Notification | Published Language (Kafka) | Events publiés sur Kafka |

---

## 4. Agrégats, Entités et Value Objects

### Tableau récapitulatif
| Bounded Context | Agrégat Racine | Entités | Value Objects |
|----------------|---------------|---------|---------------|
| Auth | User | RefreshToken | Email, PasswordHash |
| Customer | Customer | — | CustomerStatus, CreditScore |
| Account | Account | — | AccountNumber, Balance, Currency |
| Transaction | Transaction | — | TransactionReference, Money |
| Loan | Loan | Repayment | LoanStatus, InterestRate |
| Document | Document | OCRResult | ConfidenceScore, DocumentType |

---

## 5. Événements Métier

| Événement | Contexte Source | Contexte(s) Cible | Canal |
|-----------|----------------|-------------------|-------|
| CustomerRegistered | Auth | Customer | Synchrone |
| CustomerVerified | Customer | Account | Synchrone |
| AccountOpened | Account | — | — |
| TransactionCompleted | Transaction | Notification | Kafka |
| TransactionFailed | Transaction | Notification | Kafka |
| LoanRequested | Loan | Document | Synchrone |
| LoanApproved | Loan | Account, Notification | Kafka |
| LoanRejected | Loan | Notification | Kafka |
| DocumentProcessed | Document | Customer, Loan | Synchrone |
