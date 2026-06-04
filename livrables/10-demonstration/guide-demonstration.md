# Livrable 10 — Guide de Démonstration

## Prérequis

- Docker Desktop installé et lancé
- Java 17+, Maven, Python 3.14, Node.js 24
- 10 terminaux disponibles

## Démarrage complet

Voir le fichier `DEMARRAGE.md` à la racine du projet.

## Scénario de démonstration

### 1. Inscription d'un client
- Aller sur http://localhost:5173
- Cliquer "Créer un compte"
- Remplir le formulaire

### 2. Connexion
- Email : test@bank.com
- Mot de passe : password123

### 3. Ouvrir un compte bancaire
- Menu "Comptes" → "Nouveau compte"
- Type : COURANT, Devise : XOF, Dépôt : 50000

### 4. Effectuer une transaction
- Menu "Transactions" → "Nouvelle transaction"
- Type : Dépôt, Montant : 10000

### 5. Demander un prêt
- Menu "Prêts" → "Demander un prêt"
- Montant : 500000, Durée : 24 mois

### 6. Vérification KYC
- Menu "Documents"
- Soumettre une CNI → Analyse OCR automatique

### 7. Vérification Eureka
- http://localhost:8761 → Tous les services enregistrés

### 8. Vérification health checks
- http://localhost:8081/auth/health
- http://localhost:8082/customers/health
- http://localhost:8083/accounts/health
- http://localhost:8084/transactions/health
- http://localhost:8086/loans/health
- http://localhost:8087/documents/health
- http://localhost:8089/notifications/health

## URLs importantes

| URL | Description |
|-----|-------------|
| http://localhost:5173 | Frontend React |
| http://localhost:8761 | Eureka Dashboard |
| http://localhost:8888 | Config Server |
| http://localhost:8080 | API Gateway |
| http://localhost:8086/docs | API Loan (Swagger) |
| http://localhost:8087/docs | API Document (Swagger) |
