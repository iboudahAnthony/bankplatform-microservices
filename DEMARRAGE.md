# 🚀 Guide de Démarrage - Plateforme Bancaire

## ORDRE DE DÉMARRAGE (important !)

Ouvre **un terminal séparé** pour chaque étape.

---

## ÉTAPE 1 — Bases de données (Docker)

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462"
docker compose up -d
```
✅ Démarre : PostgreSQL, MongoDB, Redis, Kafka, Zookeeper

---

## ÉTAPE 2 — Config Server (attends 10 secondes après Docker)

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462\infrastructure\config-server"
mvn spring-boot:run
```
✅ Prêt quand tu vois : `Started ConfigServerApplication`
🌐 Vérif : http://localhost:8888/actuator/health

---

## ÉTAPE 3 — Eureka Discovery Server

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462\infrastructure\discovery-server"
mvn spring-boot:run
```
✅ Prêt quand tu vois : `Started DiscoveryServerApplication`
🌐 Vérif : http://localhost:8761

---

## ÉTAPE 4 — Auth Service

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462\services\auth-service"
mvn spring-boot:run
```
✅ Prêt quand tu vois : `Started AuthServiceApplication`
🌐 Vérif : http://localhost:8081/auth/health

---

## ÉTAPE 5 — Customer Service

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462\services\customer-service"
mvn spring-boot:run
```
✅ Prêt : http://localhost:8082/customers/health

---

## ÉTAPE 6 — Account Service

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462\services\account-service"
mvn spring-boot:run
```
✅ Prêt : http://localhost:8083/accounts/health

---

## ÉTAPE 7 — Transaction Service

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462\services\transaction-service"
mvn spring-boot:run
```
✅ Prêt : http://localhost:8084/transactions/health

---

## ÉTAPE 8 — Loan Service (Python)

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462\services\loan-service"
C:\Python314\python.exe -m uvicorn main:app --port 8086 --reload
```
✅ Prêt : http://localhost:8086/loans/health

---

## ÉTAPE 9 — Document Service / OCR (Python)

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462\services\document-service"
C:\Python314\python.exe -m uvicorn main:app --port 8087 --reload
```
✅ Prêt : http://localhost:8087/documents/health

---

## ÉTAPE 10 — Notification Service (Node.js)

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462\services\notification-service"
node src/index.js
```
✅ Prêt : http://localhost:8089/notifications/health

---

## ÉTAPE 11 — Frontend React

```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462\frontend"
npm run dev
```
✅ Ouvre : http://localhost:5173 ou http://localhost:5174

---

## 🔑 Compte de test

- **Email** : test@bank.com
- **Mot de passe** : password123

---

## 📊 Tableau de bord des services

| Service | Port | Technologie | Commande |
|---------|------|-------------|---------|
| Config Server | 8888 | Java | `mvn spring-boot:run` |
| Eureka | 8761 | Java | `mvn spring-boot:run` |
| Auth Service | 8081 | Java | `mvn spring-boot:run` |
| Customer Service | 8082 | Java | `mvn spring-boot:run` |
| Account Service | 8083 | Java | `mvn spring-boot:run` |
| Transaction Service | 8084 | Java | `mvn spring-boot:run` |
| Loan Service | 8086 | Python | `C:\Python314\python.exe -m uvicorn main:app --port 8086 --reload` |
| Document Service | 8087 | Python | `C:\Python314\python.exe -m uvicorn main:app --port 8087 --reload` |
| Notification Service | 8089 | Node.js | `node src/index.js` |
| Frontend | 5173 | React | `npm run dev` |
| PostgreSQL | 5432 | Docker | `docker compose up -d` |
| Kafka | 9092 | Docker | `docker compose up -d` |

---

## ⚠️ Si npm ou node non reconnu

```powershell
$env:Path += ";C:\Program Files\nodejs"
```

## ⚠️ Si mvn non reconnu

```powershell
$env:Path += ";C:\Users\THE EYE INFORMATIQUE\Downloads\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin"
```

---

## 🛑 Pour arrêter tous les services

Dans chaque terminal : **Ctrl+C**

Pour arrêter Docker :
```powershell
cd "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462"
docker compose down
```
