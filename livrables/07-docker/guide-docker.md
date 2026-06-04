# Livrable 7 — Conteneurisation Docker

## Dockerfiles créés

| Service | Fichier | Image de base |
|---------|---------|---------------|
| config-server | `infrastructure/config-server/Dockerfile` | eclipse-temurin:17-jdk-alpine |
| discovery-server | `infrastructure/discovery-server/Dockerfile` | eclipse-temurin:17-jdk-alpine |
| api-gateway | `infrastructure/api-gateway/Dockerfile` | eclipse-temurin:17-jdk-alpine |
| auth-service | `services/auth-service/Dockerfile` | eclipse-temurin:17-jdk-alpine |
| customer-service | `services/customer-service/Dockerfile` | eclipse-temurin:17-jdk-alpine |
| account-service | `services/account-service/Dockerfile` | eclipse-temurin:17-jdk-alpine |
| transaction-service | `services/transaction-service/Dockerfile` | eclipse-temurin:17-jdk-alpine |
| loan-service | `services/loan-service/Dockerfile` | python:3.12-slim |
| notification-service | `services/notification-service/Dockerfile` | node:20-alpine |

## Lancer l'environnement de développement

```bash
# Démarrer les bases de données
docker compose up -d

# Démarrer tout le système
docker compose -f docker-compose.full.yml up -d

# Vérifier les conteneurs
docker ps

# Arrêter
docker compose down
```

## Variables d'environnement importantes

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| POSTGRES_USER | Utilisateur PostgreSQL | postgres |
| POSTGRES_PASSWORD | Mot de passe PostgreSQL | postgres |
| JWT_SECRET | Clé secrète JWT | bankplatform-secret-key-... |
| EUREKA_URL | URL Eureka | http://discovery-server:8761/eureka/ |
