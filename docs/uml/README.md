# Diagrammes UML - Plateforme Bancaire Distribuée

## Comment visualiser ces diagrammes

### Option 1 : VS Code (recommandé)
1. Installer l'extension **PlantUML** dans VS Code
2. Installer Java (requis par PlantUML)
3. Ouvrir un fichier `.puml` et appuyer sur `Alt+D` pour prévisualiser

### Option 2 : En ligne (sans installation)
1. Aller sur https://www.plantuml.com/plantuml/uml/
2. Copier-coller le contenu du fichier `.puml`
3. Le diagramme s'affiche automatiquement

### Option 3 : Plugin IntelliJ IDEA
- Installer le plugin **PlantUML Integration**

---

## Liste des Diagrammes

| Fichier | Type | Description |
|---------|------|-------------|
| `01-use-case.puml` | Cas d'utilisation | Tous les acteurs et leurs interactions avec le système |
| `02-architecture-components.puml` | Composants | Architecture globale des microservices |
| `03-sequence-inscription.puml` | Séquence | Inscription client + vérification KYC avec OCR |
| `04-sequence-transaction.puml` | Séquence | Transfert inter-opérateur complet |
| `05-sequence-pret.puml` | Séquence | Demande, analyse et remboursement de prêt |
| `06-class-domain.puml` | Classes | Modèle de domaine DDD (agrégats, entités, enums) |
| `07-ddd-context-map.puml` | Context Map | Relations entre les Bounded Contexts (DDD) |
| `08-deployment.puml` | Déploiement | Architecture Kubernetes de production |

---

## Résumé de l'Architecture DDD

### Sous-domaines identifiés

#### Core Domains (cœur métier)
- **Customer Context** → customer-service (Java/Spring Boot)
- **Account Context** → account-service (Java/Spring Boot)
- **Transaction Context** → transaction-service (Java/Spring Boot)
- **Loan Context** → loan-service (Python/FastAPI)

#### Supporting Domains
- **Auth Context** → auth-service (Java/Spring Boot)
- **Operator Context** → operator-service (Java/Spring Boot)
- **Document/OCR Context** → document-service (Python/FastAPI)

#### Generic Domains
- **Notification Context** → notification-service (Node.js/Express)
- **Audit Context** → audit-service (Node.js/Express)
- **Reporting Context** → reporting-service (Python/FastAPI)

### Justification des technologies

| Technologie | Services | Justification |
|-------------|----------|---------------|
| Java (Spring Boot) | auth, customer, account, transaction, operator | Robustesse, transactions ACID, Spring Security, écosystème mature |
| Python (FastAPI) | loan, document, reporting | Librairies IA/OCR (Tesseract, OpenCV, Pandas), calculs financiers |
| Node.js (Express) | notification, audit | I/O non-bloquant, event-driven, idéal pour consommer des événements Kafka |
