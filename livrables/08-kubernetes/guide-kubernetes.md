# Livrable 8 — Déploiement Kubernetes

## Fichiers créés

| Fichier | Description |
|---------|-------------|
| `k8s/namespace.yml` | Namespace bankplatform |
| `k8s/configmap.yml` | ConfigMap + Secrets |
| `k8s/postgres.yml` | StatefulSet PostgreSQL |
| `k8s/config-server.yml` | Deployment Config Server |
| `k8s/discovery-server.yml` | Deployment Eureka |
| `k8s/api-gateway.yml` | Deployment + LoadBalancer |
| `k8s/auth-service.yml` | Deployment (2 replicas) |
| `k8s/customer-service.yml` | Deployment (2 replicas) |
| `k8s/account-service.yml` | Deployment (2 replicas) |
| `k8s/transaction-service.yml` | Deployment (3 replicas) |
| `k8s/loan-service.yml` | Deployment (2 replicas) |
| `k8s/notification-service.yml` | Deployment (2 replicas) |

## Déployer sur Kubernetes

```bash
# Déploiement complet
cd k8s
bash deploy-all.sh

# Vérifier les pods
kubectl get pods -n bankplatform

# Vérifier les services
kubectl get services -n bankplatform

# Logs d'un service
kubectl logs -f deployment/auth-service -n bankplatform
```

## Architecture Kubernetes

```
Namespace: bankplatform
├── Infrastructure
│   ├── config-server (1 replica)
│   ├── discovery-server (1 replica)
│   └── api-gateway (2 replicas) ← LoadBalancer
├── Services Java (2-3 replicas chacun)
├── Services Python (2 replicas)
├── Services Node.js (2 replicas)
└── Bases de données (StatefulSet)
```
