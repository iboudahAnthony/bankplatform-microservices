from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.database import engine, Base
from app.routers import loans

# Créer les tables
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Loan Service - Plateforme Bancaire",
    description="Service de gestion des prêts",
    version="1.0.0"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Health check AVANT les routes (ordre important)
@app.get("/loans/health")
def health():
    return {"status": "UP", "service": "loan-service"}

@app.get("/")
def root():
    return {"service": "loan-service", "status": "running"}

# Routes (après le health check)
app.include_router(loans.router, prefix="/loans", tags=["loans"])
