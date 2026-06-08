# Livrable 1 — Cahier des Charges
# Plateforme Bancaire Distribuée

## 1. Présentation du projet

### 1.1 Contexte
Dans le cadre de la transformation numérique des services financiers, ce projet vise à développer une plateforme bancaire distribuée permettant à plusieurs opérateurs financiers de collaborer au sein d'un même écosystème numérique.

### 1.2 Objectifs
- Permettre la gestion complète des clients et de leurs comptes
- Gérer les transactions financières (dépôts, retraits, transferts)
- Traiter les demandes de prêts avec scoring automatique
- Intégrer une solution OCR/IA pour la vérification des documents
- Assurer la scalabilité, la résilience et la sécurité du système

---

## 2. Exigences Fonctionnelles

### 2.1 Gestion des utilisateurs
| ID | Exigence | Priorité |
|----|----------|---------|
| F01 | L'utilisateur peut s'inscrire avec email et mot de passe | Haute |
| F02 | L'utilisateur peut se connecter et obtenir un token JWT | Haute |
| F03 | Le système supporte plusieurs rôles : CLIENT, OPERATOR_ADMIN, OPERATOR_ANALYST, SUPER_ADMIN | Haute |
| F04 | Le compte est bloqué après 5 tentatives échouées | Moyenne |
| F05 | L'utilisateur peut renouveler son token via refresh token | Moyenne |

### 2.2 Gestion des clients
| ID | Exigence | Priorité |
|----|----------|---------|
| F10 | Créer un profil client avec informations personnelles | Haute |
| F11 | Vérifier l'identité du client (KYC) | Haute |
| F12 | Suspendre ou réactiver un compte client | Moyenne |
| F13 | Consulter la liste des clients par opérateur | Moyenne |
| F14 | Calculer et stocker le score crédit du client | Haute |

### 2.3 Gestion des comptes
| ID | Exigence | Priorité |
|----|----------|---------|
| F20 | Ouvrir un compte (COURANT, EPARGNE, MOBILE_MONEY) | Haute |
| F21 | Consulter le solde et l'historique | Haute |
| F22 | Bloquer ou clôturer un compte | Moyenne |
| F23 | Définir des limites journalières et mensuelles | Haute |
| F24 | Support multi-devises (XOF, EUR, USD) | Moyenne |

### 2.4 Transactions
| ID | Exigence | Priorité |
|----|----------|---------|
| F30 | Effectuer un dépôt sur un compte | Haute |
| F31 | Effectuer un retrait depuis un compte | Haute |
| F32 | Transfert intra-opérateur | Haute |
| F33 | Transfert inter-opérateur | Haute |
| F34 | Annuler une transaction en cours | Moyenne |
| F35 | Consulter l'historique des transactions | Haute |
| F36 | Générer une référence unique par transaction | Haute |

### 2.5 Prêts
| ID | Exigence | Priorité |
|----|----------|---------|
| F40 | Soumettre une demande de prêt | Haute |
| F41 | Calculer automatiquement le score de crédit | Haute |
| F42 | Approuver ou rejeter une demande | Haute |
| F43 | Générer l'échéancier de remboursement | Haute |
| F44 | Enregistrer les remboursements | Haute |
| F45 | Clôturer automatiquement le prêt soldé | Moyenne |

### 2.6 Documents & KYC
| ID | Exigence | Priorité |
|----|----------|---------|
| F50 | Soumettre des documents (CNI, passeport, bulletins...) | Haute |
| F51 | Extraire automatiquement les informations via OCR | Haute |
| F52 | Calculer un score de confiance du document | Haute |
| F53 | Vérification KYC automatique (comparaison nom/prénom) | Haute |
| F54 | Stocker les métadonnées des documents traités | Moyenne |

### 2.7 Notifications
| ID | Exigence | Priorité |
|----|----------|---------|
| F60 | Notifier le client après chaque transaction | Haute |
| F61 | Notifier le client après approbation/rejet de prêt | Haute |
| F62 | Support SMS et Email | Moyenne |
| F63 | Historique des notifications | Basse |

---

## 3. Exigences Non Fonctionnelles

### 3.1 Performance
| ID | Exigence | Cible |
|----|----------|-------|
| NF01 | Temps de réponse API | < 500ms (95e percentile) |
| NF02 | Débit transactions simultanées | > 100 TPS |
| NF03 | Disponibilité système | > 99.9% |

### 3.2 Sécurité
| ID | Exigence |
|----|----------|
| NF10 | Authentification JWT avec expiration 15 minutes |
| NF11 | Refresh token avec expiration 7 jours |
| NF12 | Chiffrement BCrypt des mots de passe |
| NF13 | Communication HTTPS en production |
| NF14 | En-têtes CORS configurés |
| NF15 | Validation des entrées sur tous les endpoints |

### 3.3 Scalabilité
| ID | Exigence |
|----|----------|
| NF20 | Chaque microservice scalable indépendamment |
| NF21 | Déploiement sur cluster Kubernetes |
| NF22 | Load balancing via API Gateway |

### 3.4 Maintenabilité
| ID | Exigence |
|----|----------|
| NF30 | Architecture microservices avec séparation des responsabilités |
| NF31 | Pipeline CI/CD automatisé |
| NF32 | Logs centralisés |
| NF33 | Health checks sur tous les services |

### 3.5 Interopérabilité
| ID | Exigence |
|----|----------|
| NF40 | APIs REST documentées (OpenAPI/Swagger) |
| NF41 | Communication asynchrone via Apache Kafka |
| NF42 | Format JSON pour tous les échanges |

---

## 4. Contraintes Techniques

### 4.1 Technologies obligatoires
- **Java** : Services backend critiques (auth, accounts, transactions)
- **Python** : Services IA/ML (loans, documents OCR)
- **JavaScript/Node.js** : Services événementiels (notifications, audit)

### 4.2 Infrastructure
- **Docker** : Conteneurisation obligatoire
- **Kubernetes** : Orchestration en production
- **PostgreSQL** : Base de données principale
- **Apache Kafka** : Messagerie asynchrone
- **Redis** : Cache

---

## 5. Acteurs du système

| Acteur | Description |
|--------|-------------|
| Client | Utilisateur final qui gère ses comptes |
| Administrateur | Supervise l'ensemble de la plateforme |
| Opérateur Financier | Gère ses clients et valide les prêts |
| Analyste | Analyse et valide les dossiers de prêts |
| Système OCR/IA | Traite automatiquement les documents |
