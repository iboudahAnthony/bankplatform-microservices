# Pipeline CI/CD - Plateforme Bancaire

## Architecture du Pipeline

```
Push sur main/develop
        │
        ▼
┌─────────────────────────────────────┐
│  JOB 1: Build Java Services         │
│  - Compile avec Maven               │
│  - Tests unitaires                  │
│  - Upload JARs                      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  JOB 2: Build Python Service         │
│  - Install pip dependencies          │
│  - Syntax check                      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  JOB 3: Build Node.js Service        │
│  - npm install                       │
│  - Syntax check                      │
└──────────────┬──────────────────────┘
               │ (seulement sur main)
┌──────────────▼──────────────────────┐
│  JOB 4: Build Docker Images          │
│  - Build toutes les images           │
│  - Push sur Docker Hub               │
│  - Tag avec SHA du commit            │
└──────────────┬──────────────────────┘
               │ (seulement sur main)
┌──────────────▼──────────────────────┐
│  JOB 5: Deploy to Kubernetes         │
│  - kubectl apply tous les manifests  │
│  - Rollout status check              │
└─────────────────────────────────────┘
```

## Secrets GitHub requis

| Secret | Description |
|--------|-------------|
| `DOCKER_USERNAME` | Nom d'utilisateur Docker Hub |
| `DOCKER_PASSWORD` | Mot de passe Docker Hub |
| `KUBECONFIG` | Contenu du fichier kubeconfig (base64) |

## Branches

- `develop` : déclenche build + tests uniquement
- `main` : déclenche build + tests + Docker + déploiement K8s
