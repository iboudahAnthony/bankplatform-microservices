#!/bin/bash
# Script de déploiement complet sur Kubernetes

echo "=== Déploiement Plateforme Bancaire sur Kubernetes ==="

# 1. Namespace
kubectl apply -f namespace.yml

# 2. Config & Secrets
kubectl apply -f configmap.yml

# 3. Bases de données
kubectl apply -f postgres.yml
echo "Attente démarrage PostgreSQL..."
kubectl wait --for=condition=ready pod -l app=postgres -n bankplatform --timeout=120s

# 4. Infrastructure
kubectl apply -f config-server.yml
echo "Attente Config Server..."
kubectl wait --for=condition=ready pod -l app=config-server -n bankplatform --timeout=120s

kubectl apply -f discovery-server.yml
echo "Attente Eureka..."
kubectl wait --for=condition=ready pod -l app=discovery-server -n bankplatform --timeout=120s

kubectl apply -f api-gateway.yml

# 5. Microservices
kubectl apply -f auth-service.yml
kubectl apply -f customer-service.yml
kubectl apply -f account-service.yml
kubectl apply -f transaction-service.yml
kubectl apply -f loan-service.yml
kubectl apply -f notification-service.yml

echo ""
echo "=== Déploiement terminé ==="
echo "Vérification des pods :"
kubectl get pods -n bankplatform

echo ""
echo "Vérification des services :"
kubectl get services -n bankplatform
