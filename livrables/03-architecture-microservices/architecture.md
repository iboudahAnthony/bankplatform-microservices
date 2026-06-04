# Livrable 3 — Architecture Microservices

## 1. Vue d'ensemble

La plateforme adopte une architecture microservices distribuée composée de 10 services indépendants, organisés en 3 couches :

```
┌─────────────────────────────────────────┐
│           Frontend React (5173)          │
└──────────────────┬──────────────────────┘
                   │ HTTPS
┌──────────────────▼──────────────────────┐
│         API Gateway (8080)               │
│    Spring Cloud Gateway + JWT Filter     │
└──────┬───────────────────────────┬───────┘
       │ Service Discovery          │ Config
┌──────▼──────┐          ┌─────────▼──────┐
│   Eureka    │          │  Config Server  │
│   (8761)    │          │    (8888)       │
└─────────────┘          └────────────────┘
       │
┌──────▼────────────────────────────────────┐
│              Microservices                 │
├──────────┬──────────┬──────────┬───────────┤
│   Auth   │ Customer │ Account  │Transaction│
│  (8081)  │  (8082)  │  (8083)  │  (8084)   │
│  Java    │  Java    │  Java    │   Java    │
├──────────┼──────────┼──────────┼───────────┤
│   Loan   │Document  │Notifica- │           │
│  (8086)  │  (8087)  │  tion    │           │
│  Python  │  Python  │  (8089)  │           │
│          │          │  Node.js │           │
└──────────┴──────────┴──────────┴───────────┘
       │
┌──────▼────────────────────────────────────┐
│           Infrastructure Data              │
├──────────┬──────────┬──────────┬───────────┤
│PostgreSQL│ MongoDB  │  Redis   │  Kafka    │
│  (5432)  │ (27017)  │  (6379)  │  (9092)   │
└──────────┴──────────┴──────────┴───────────┘
```

---

## 2. Description des services

### Infrastructure

| Service | Port | Rôle |
|---------|------|------|
| Config Server | 8888 | Centralise la configuration de tous les services |
| Discovery Server | 8761 | Registre de services (Eureka) — découverte automatique |
| API Gateway | 8080 | Point d'entrée unique, routage, filtre JWT |

### Microservices Métier

| Service | Port | Techno | Rôle |
|---------|------|--------|------|
| auth-service | 8081 | Java/Spring Boot | Authentification JWT, gestion tokens |
| customer-service | 8082 | Java/Spring Boot | Profils clients, KYC |
| account-service | 8083 | Java/Spring Boot | Comptes bancaires, soldes |
| transaction-service | 8084 | Java/Spring Boot | Transactions, débit/crédit |
| loan-service | 8086 | Python/FastAPI | Prêts, scoring, échéanciers |
| document-service | 8087 | Python/FastAPI | OCR, vérification documents |
| notification-service | 8089 | Node.js/Express | Notifications SMS/Email |

---

## 3. Justification des choix technologiques

### Java (Spring Boot) — Services critiques
**Raisons** :
- Écosystème mature pour les applications financières
- Spring Security pour JWT et OAuth2
- Spring Data JPA pour les transactions ACID
- Intégration native avec Eureka et Spring Cloud Gateway
- Typage fort = moins d'erreurs en production

### Python (FastAPI) — Services IA/ML
**Raisons** :
- Bibliothèques OCR/ML disponibles (Tesseract, Pillow)
- FastAPI pour des APIs haute performance
- SQLAlchemy pour l'ORM
- Idéal pour les calculs financiers (loan scoring)
- Documentation Swagger automatique

### Node.js (Express) — Services événementiels
**Raisons** :
- I/O non-bloquant — idéal pour consommer des événements Kafka
- Bibliothèque KafkaJS native
- Faible empreinte mémoire
- Parfait pour les tâches asynchrones (notifications)

---

## 4. Patterns architecturaux utilisés

| Pattern | Utilisation | Service(s) |
|---------|-------------|------------|
| API Gateway | Point d'entrée unique, routage | api-gateway |
| Service Discovery | Découverte automatique des services | Eureka |
| Centralized Config | Configuration externalisée | config-server |
| Database per Service | Isolation des données | Tous les services |
| Event-Driven | Communication asynchrone | transaction → notification |
| Saga Pattern | Transactions distribuées | transaction-service |
| Circuit Breaker | Résilience | À implémenter (Resilience4j) |

---

## 5. Communication entre services

### Synchrone (REST)
```
API Gateway → auth-service (login/register)
API Gateway → customer-service (CRUD clients)
API Gateway → account-service (CRUD comptes)
API Gateway → transaction-service (exécution)
transaction-service → account-service (débit/crédit)
loan-service → customer-service (vérif éligibilité)
```

### Asynchrone (Kafka)
```
transaction-service ──[transaction.completed]──► notification-service
transaction-service ──[transaction.failed]────► notification-service
loan-service ──────────[loan.approved]─────────► notification-service
```

---

## 6. Sécurité

### Flux d'authentification
```
Client → POST /api/auth/login → auth-service
auth-service → JWT (15min) + RefreshToken (7j)
Client → Header: Authorization: Bearer <JWT>
API Gateway → Valide JWT → Forward X-User-Id, X-User-Role
Services → Reçoivent les headers sans valider le JWT
```
