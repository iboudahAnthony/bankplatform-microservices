# Script de démarrage de l'infrastructure
# Ordre : Config Server -> Discovery Server -> API Gateway

$BASE = "C:\Users\THE EYE INFORMATIQUE\Desktop\tp 462"

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Démarrage de l'infrastructure - Plateforme Bancaire" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

# Fonction pour compiler un projet Maven
function Build-Service {
    param([string]$Name, [string]$Path)
    Write-Host "`n[BUILD] $Name ..." -ForegroundColor Yellow
    Push-Location $Path
    $result = mvn clean package -DskipTests -q 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[BUILD] $Name -> OK" -ForegroundColor Green
    } else {
        Write-Host "[BUILD] $Name -> ERREUR" -ForegroundColor Red
        Write-Host $result
    }
    Pop-Location
}

# Compiler les 3 services
Build-Service "config-server"    "$BASE\infrastructure\config-server"
Build-Service "discovery-server" "$BASE\infrastructure\discovery-server"
Build-Service "api-gateway"      "$BASE\infrastructure\api-gateway"

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "  Démarrage des services (dans des fenêtres séparées)" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

# Démarrer Config Server
Write-Host "`n[START] Config Server (port 8888)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$BASE\infrastructure\config-server'; Write-Host 'Config Server' -ForegroundColor Cyan; mvn spring-boot:run"

Start-Sleep -Seconds 10

# Démarrer Discovery Server
Write-Host "[START] Discovery Server / Eureka (port 8761)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$BASE\infrastructure\discovery-server'; Write-Host 'Discovery Server' -ForegroundColor Green; mvn spring-boot:run"

Start-Sleep -Seconds 15

# Démarrer API Gateway
Write-Host "[START] API Gateway (port 8080)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$BASE\infrastructure\api-gateway'; Write-Host 'API Gateway' -ForegroundColor Magenta; mvn spring-boot:run"

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "  Infrastructure en cours de démarrage..." -ForegroundColor Cyan
Write-Host "" 
Write-Host "  Vérification dans 30 secondes :" -ForegroundColor White
Write-Host "  - Config Server  : http://localhost:8888/actuator/health" -ForegroundColor White
Write-Host "  - Eureka UI      : http://localhost:8761" -ForegroundColor White
Write-Host "  - API Gateway    : http://localhost:8080/actuator/health" -ForegroundColor White
Write-Host "============================================================" -ForegroundColor Cyan
