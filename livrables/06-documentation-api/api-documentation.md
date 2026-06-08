# Livrable 6 — Documentation Technique & APIs

## 1. Architecture des APIs

Toutes les APIs sont accessibles via l'API Gateway sur le port **8080**.
Les services Python exposent également une documentation Swagger automatique.

---

## 2. Auth Service — Port 8081

### POST /api/auth/register
Inscription d'un nouvel utilisateur.

**Request Body :**
```json
{
  "firstName": "Jean",
  "lastName": "Dupont",
  "email": "jean@example.com",
  "password": "password123",
  "phoneNumber": "+225000000",
  "role": "CLIENT"
}
```

**Response 201 :**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "7db929c6-...",
  "tokenType": "Bearer",
  "expiresIn": 900000,
  "userId": "07cf945e-...",
  "email": "jean@example.com",
  "role": "CLIENT",
  "firstName": "Jean",
  "lastName": "Dupont"
}
```

---

### POST /api/auth/login
Connexion d'un utilisateur existant.

**Request Body :**
```json
{
  "email": "jean@example.com",
  "password": "password123"
}
```
**Response 200** : Même format que /register

---

### POST /api/auth/refresh
Renouveler le token d'accès.

**Request Body :**
```json
{ "refreshToken": "7db929c6-..." }
```

---

## 3. Customer Service — Port 8082

### POST /api/customers
Créer un profil client.

**Headers :** `Authorization: Bearer <token>`

**Request Body :**
```json
{
  "userId": "07cf945e-...",
  "firstName": "Jean",
  "lastName": "Dupont",
  "email": "jean@example.com",
  "phoneNumber": "+225000000",
  "nationality": "Ivoirienne",
  "address": "12 rue de la Paix",
  "city": "Abidjan",
  "country": "Côte d'Ivoire"
}
```

**Response 201 :**
```json
{
  "id": "uuid",
  "firstName": "Jean",
  "status": "PENDING",
  "creditScore": 500,
  "createdAt": "2026-06-03T10:00:00"
}
```

---

### GET /api/customers/{id}
Récupérer un client par ID.

### GET /api/customers/by-user/{userId}
Récupérer un client par userId.

### POST /api/customers/{id}/verify
Vérifier (activer) un client — Rôle ADMIN/OPERATOR requis.

### POST /api/customers/{id}/suspend
Suspendre un client.

---

## 4. Account Service — Port 8083

### POST /api/accounts
Ouvrir un compte bancaire.

**Request Body :**
```json
{
  "customerId": "uuid-client",
  "type": "COURANT",
  "currency": "XOF",
  "initialDeposit": 50000
}
```

**Response 201 :**
```json
{
  "id": "uuid",
  "accountNumber": "BNK1234567890",
  "balance": 50000,
  "currency": "XOF",
  "status": "ACTIVE",
  "dailyLimit": 1000000,
  "monthlyLimit": 5000000
}
```

---

### GET /api/accounts/customer/{customerId}
Lister tous les comptes d'un client.

### POST /api/accounts/{id}/debit
Débiter un compte (appelé par transaction-service).
```json
{ "amount": 10000 }
```

### POST /api/accounts/{id}/credit
Créditer un compte.
```json
{ "amount": 10000 }
```

---

## 5. Transaction Service — Port 8084

### POST /api/transactions
Effectuer une transaction.

**Request Body :**
```json
{
  "type": "TRANSFERT_INTRA",
  "sourceAccountId": "uuid-source",
  "destinationAccountId": "uuid-dest",
  "amount": 25000,
  "currency": "XOF",
  "description": "Paiement loyer"
}
```

**Types disponibles :** `DEPOT`, `RETRAIT`, `TRANSFERT_INTRA`, `TRANSFERT_INTER`

**Response 201 :**
```json
{
  "id": "uuid",
  "reference": "TXN-1717400000-1234",
  "type": "TRANSFERT_INTRA",
  "amount": 25000,
  "status": "COMPLETED",
  "initiatedAt": "2026-06-03T10:00:00",
  "completedAt": "2026-06-03T10:00:01"
}
```

---

### GET /api/transactions/account/{accountId}
Historique des transactions d'un compte.

### GET /api/transactions/reference/{reference}
Récupérer une transaction par référence.

---

## 6. Loan Service — Port 8086
Documentation Swagger : http://localhost:8086/docs

### POST /loans
Soumettre une demande de prêt.

**Request Body :**
```json
{
  "customer_id": "uuid",
  "amount": 500000,
  "duration_months": 24,
  "interest_rate": 8.0,
  "purpose": "Achat véhicule"
}
```

**Response 201 :**
```json
{
  "id": "uuid",
  "amount": 500000,
  "interest_rate": 8.0,
  "duration_months": 24,
  "status": "SUBMITTED",
  "credit_score": 670
}
```

### PUT /loans/{id}/approve
Approuver un prêt (OPERATOR_ADMIN requis).
```json
{ "interest_rate": 7.5 }
```

### PUT /loans/{id}/reject
Rejeter un prêt.
```json
{ "reason": "Revenus insuffisants" }
```

---

## 7. Document Service — Port 8087
Documentation Swagger : http://localhost:8087/docs

### POST /documents/upload
Uploader et analyser un document par OCR.

**Form Data :**
- `file` : Fichier image (JPG, PNG, PDF)
- `document_type` : CNI, PASSPORT, BULLETIN_SALAIRE...
- `customer_id` : UUID du client

**Response 200 :**
```json
{
  "document_id": "uuid",
  "verification_status": "VERIFIED",
  "confidence_score": 0.85,
  "extracted_info": {
    "last_name": "DUPONT",
    "first_name": "JEAN",
    "date_of_birth": "15/03/1990",
    "document_number": "CI1234567890"
  }
}
```

### POST /documents/verify-kyc
Vérification KYC automatique.

---

## 8. Notification Service — Port 8089

### POST /notifications/send
Envoyer une notification.

**Request Body :**
```json
{
  "type": "SMS",
  "recipient": "user-id",
  "subject": "Transaction effectuée",
  "message": "Votre dépôt de 50000 XOF a été effectué. Réf: TXN-XXX"
}
```

### GET /notifications/history
Récupérer l'historique des notifications.

---

## 9. Codes d'erreur standards

| Code HTTP | Description |
|-----------|-------------|
| 200 | Succès |
| 201 | Créé avec succès |
| 400 | Données invalides |
| 401 | Non authentifié |
| 403 | Accès refusé |
| 404 | Ressource introuvable |
| 409 | Conflit (ex: email déjà utilisé) |
| 500 | Erreur interne serveur |
