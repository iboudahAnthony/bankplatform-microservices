# Livrable 9 — CI/CD GitHub Actions

## Pipeline créé

Fichier : `.github/workflows/ci-cd.yml`

## Étapes du pipeline

```
Push sur main/develop
        │
        ▼
Job 1: Build Java (Maven) ──────────┐
Job 2: Build Python (pip) ──────────┤── Parallèle
Job 3: Build Node.js (npm) ─────────┘
        │
        ▼ (seulement sur main)
Job 4: Build & Push Docker Images
        │
        ▼ (seulement sur main)
Job 5: Deploy to Kubernetes
```

## Secrets GitHub requis

| Secret | Description |
|--------|-------------|
| `DOCKER_USERNAME` | Nom d'utilisateur Docker Hub |
| `DOCKER_PASSWORD` | Mot de passe Docker Hub |
| `KUBECONFIG` | Config Kubernetes (base64) |

## Branches

- `develop` → Build + Tests uniquement
- `main` → Build + Tests + Docker + Kubernetes

## Commandes utiles

```bash
# Voir les workflows
gh workflow list

# Déclencher manuellement
gh workflow run ci-cd.yml
```
