# Livrable 4 — Diagrammes UML

## Diagrammes disponibles

Les fichiers source PlantUML sont dans le dossier `docs/uml/`.
Les images PNG sont dans `docs/uml/images/`.

| Fichier | Type | Description |
|---------|------|-------------|
| `01-use-case.puml` | Cas d'utilisation | Tous les acteurs et leurs interactions |
| `02-architecture-components.puml` | Composants | Architecture globale microservices |
| `03-sequence-inscription.puml` | Séquence | Inscription + KYC + OCR |
| `04-sequence-transaction.puml` | Séquence | Transfert inter-opérateur |
| `05-sequence-pret.puml` | Séquence | Demande et remboursement de prêt |
| `06-class-domain.puml` | Classes | Modèle de domaine DDD |
| `07-ddd-context-map.puml` | Context Map | Relations entre Bounded Contexts |
| `08-deployment.puml` | Déploiement | Architecture Kubernetes |

## Comment visualiser

### Option 1 — VS Code
Installer l'extension **PlantUML** puis `Alt+D` pour prévisualiser.

### Option 2 — En ligne
Aller sur https://www.plantuml.com/plantuml/uml/ et coller le contenu.

### Option 3 — Générer les images
```powershell
cd docs/uml
python export_diagrams.py
```

## Résumé des acteurs (Cas d'utilisation)

- **Client** : S'inscrit, gère ses comptes, effectue des transactions, demande des prêts
- **Administrateur** : Gère les opérateurs, consulte les rapports, configure le système
- **Opérateur Financier** : Analyse et valide les dossiers de prêts, configure les règles
- **Système OCR/IA** : Extrait automatiquement les informations des documents
