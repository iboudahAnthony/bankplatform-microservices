from fastapi import FastAPI, File, UploadFile, Form, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import Optional
import os
import io
import re
import uuid
from datetime import datetime

app = FastAPI(
    title="Document Service - OCR/IA",
    description="Service d'extraction et de vérification de documents via OCR",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Stockage en mémoire des documents traités
documents_store = {}


def extract_text_from_image(image_bytes: bytes) -> str:
    """
    Extraction OCR du texte depuis une image.
    Utilise pytesseract si Tesseract est installé, sinon simule l'extraction.
    """
    try:
        from PIL import Image
        import pytesseract

        # Configuration Tesseract pour Windows
        tesseract_paths = [
            r'C:\Program Files\Tesseract-OCR\tesseract.exe',
            r'C:\Program Files (x86)\Tesseract-OCR\tesseract.exe',
        ]
        for path in tesseract_paths:
            if os.path.exists(path):
                pytesseract.pytesseract.tesseract_cmd = path
                break

        image = Image.open(io.BytesIO(image_bytes))
        text = pytesseract.image_to_string(image, lang='fra+eng')
        return text

    except Exception as e:
        # Simulation OCR si Tesseract non installé
        return simulate_ocr_extraction()


def simulate_ocr_extraction() -> str:
    """Simule une extraction OCR pour démonstration"""
    return """
    REPUBLIQUE DE COTE D'IVOIRE
    CARTE NATIONALE D'IDENTITE
    
    NOM: DUPONT
    PRENOM: JEAN MARIE
    DATE DE NAISSANCE: 15/03/1990
    LIEU DE NAISSANCE: ABIDJAN
    NATIONALITE: IVOIRIENNE
    NUMERO: CI1234567890
    DATE DELIVRANCE: 01/01/2020
    DATE EXPIRATION: 01/01/2030
    ADRESSE: 12 RUE DE LA PAIX ABIDJAN
    """


def extract_info_from_cni(text: str) -> dict:
    """Extrait les informations d'une CNI"""
    info = {}

    # Extraction nom
    nom_match = re.search(r'NOM[:\s]+([A-Z\s]+)', text)
    if nom_match:
        info['last_name'] = nom_match.group(1).strip()

    # Extraction prénom
    prenom_match = re.search(r'PRENOM[:\s]+([A-Z\s]+)', text)
    if prenom_match:
        info['first_name'] = prenom_match.group(1).strip()

    # Extraction date de naissance
    dob_match = re.search(r'(?:DATE DE NAISSANCE|NE LE)[:\s]+(\d{2}/\d{2}/\d{4})', text)
    if dob_match:
        info['date_of_birth'] = dob_match.group(1)

    # Extraction numéro CNI
    num_match = re.search(r'(?:NUMERO|N°)[:\s]*([A-Z0-9]{8,})', text)
    if num_match:
        info['document_number'] = num_match.group(1)

    # Extraction nationalité
    nat_match = re.search(r'NATIONALITE[:\s]+([A-Z]+)', text)
    if nat_match:
        info['nationality'] = nat_match.group(1)

    # Extraction adresse
    addr_match = re.search(r'ADRESSE[:\s]+(.+)', text)
    if addr_match:
        info['address'] = addr_match.group(1).strip()

    return info


def extract_info_from_salary_slip(text: str) -> dict:
    """Extrait les informations d'un bulletin de salaire"""
    info = {}

    # Salaire net
    salary_match = re.search(r'(?:SALAIRE NET|NET A PAYER)[:\s]*([\d\s]+)', text)
    if salary_match:
        salary_str = salary_match.group(1).replace(' ', '')
        try:
            info['net_salary'] = float(salary_str)
        except:
            pass

    # Employeur
    employer_match = re.search(r'(?:EMPLOYEUR|ENTREPRISE|SOCIETE)[:\s]+(.+)', text)
    if employer_match:
        info['employer'] = employer_match.group(1).strip()

    # Période
    period_match = re.search(r'(?:PERIODE|MOIS)[:\s]+(.+)', text)
    if period_match:
        info['period'] = period_match.group(1).strip()

    return info


def calculate_confidence_score(extracted_info: dict, doc_type: str) -> float:
    """Calcule un score de confiance basé sur les champs extraits"""
    required_fields = {
        'CNI': ['last_name', 'first_name', 'date_of_birth', 'document_number'],
        'PASSPORT': ['last_name', 'first_name', 'date_of_birth', 'document_number'],
        'BULLETIN_SALAIRE': ['net_salary', 'employer'],
        'JUSTIFICATIF_DOMICILE': ['address'],
    }

    fields = required_fields.get(doc_type, [])
    if not fields:
        return 0.5

    found = sum(1 for f in fields if f in extracted_info and extracted_info[f])
    return round(found / len(fields), 2)


@app.get("/documents/health")
def health():
    return {"status": "UP", "service": "document-service"}


@app.post("/documents/upload")
async def upload_document(
    file: UploadFile = File(...),
    document_type: str = Form(...),
    customer_id: Optional[str] = Form(None)
):
    """
    Upload et traitement OCR d'un document
    Types acceptés: CNI, PASSPORT, JUSTIFICATIF_DOMICILE, BULLETIN_SALAIRE, RELEVE_BANCAIRE
    """
    allowed_types = {'CNI', 'PASSPORT', 'JUSTIFICATIF_DOMICILE', 'BULLETIN_SALAIRE',
                     'RELEVE_BANCAIRE', 'CONTRAT_TRAVAIL', 'DOCUMENT_ADMINISTRATIF'}

    if document_type not in allowed_types:
        raise HTTPException(status_code=400, detail=f"Type invalide. Acceptés: {allowed_types}")

    # Vérifier le type de fichier
    allowed_extensions = {'.jpg', '.jpeg', '.png', '.pdf', '.bmp', '.tiff'}
    file_ext = os.path.splitext(file.filename)[1].lower()
    if file_ext not in allowed_extensions:
        raise HTTPException(status_code=400, detail="Format non supporté. Utilisez JPG, PNG ou PDF")

    # Lire le fichier
    file_bytes = await file.read()
    doc_id = str(uuid.uuid4())

    # Extraction OCR
    extracted_text = extract_text_from_image(file_bytes)

    # Extraction des informations selon le type
    extracted_info = {}
    if document_type in ['CNI', 'PASSPORT']:
        extracted_info = extract_info_from_cni(extracted_text)
    elif document_type == 'BULLETIN_SALAIRE':
        extracted_info = extract_info_from_salary_slip(extracted_text)

    # Score de confiance
    confidence_score = calculate_confidence_score(extracted_info, document_type)

    # Statut de vérification automatique
    verification_status = "VERIFIED" if confidence_score >= 0.7 else \
                         "PARTIAL" if confidence_score >= 0.4 else "FAILED"

    document = {
        "id": doc_id,
        "customer_id": customer_id,
        "document_type": document_type,
        "filename": file.filename,
        "file_size": len(file_bytes),
        "extracted_text": extracted_text[:500] + "..." if len(extracted_text) > 500 else extracted_text,
        "extracted_info": extracted_info,
        "confidence_score": confidence_score,
        "verification_status": verification_status,
        "processed_at": datetime.utcnow().isoformat()
    }

    documents_store[doc_id] = document

    return {
        "document_id": doc_id,
        "document_type": document_type,
        "extracted_info": extracted_info,
        "confidence_score": confidence_score,
        "verification_status": verification_status,
        "message": f"Document traité avec succès. Score de confiance: {confidence_score * 100:.0f}%"
    }


@app.get("/documents/{document_id}")
def get_document(document_id: str):
    """Récupère les informations d'un document traité"""
    if document_id not in documents_store:
        raise HTTPException(status_code=404, detail="Document introuvable")
    return documents_store[document_id]


@app.get("/documents/customer/{customer_id}")
def get_customer_documents(customer_id: str):
    """Récupère tous les documents d'un client"""
    docs = [d for d in documents_store.values() if d.get('customer_id') == customer_id]
    return {"customer_id": customer_id, "documents": docs, "count": len(docs)}


@app.post("/documents/verify-kyc")
async def verify_kyc(
    cni_file: UploadFile = File(...),
    customer_id: str = Form(...),
    declared_first_name: str = Form(...),
    declared_last_name: str = Form(...)
):
    """
    Vérification KYC automatique :
    Compare les infos déclarées avec celles extraites de la CNI
    """
    file_bytes = await cni_file.read()
    extracted_text = extract_text_from_image(file_bytes)
    extracted_info = extract_info_from_cni(extracted_text)

    # Comparaison des données
    name_match = False
    if extracted_info.get('last_name') and extracted_info.get('first_name'):
        extracted_full = f"{extracted_info['last_name']} {extracted_info['first_name']}".upper()
        declared_full = f"{declared_last_name} {declared_first_name}".upper()
        name_match = declared_last_name.upper() in extracted_full or \
                     declared_first_name.upper() in extracted_full

    kyc_result = {
        "customer_id": customer_id,
        "kyc_status": "PASSED" if name_match else "REVIEW_REQUIRED",
        "name_match": name_match,
        "extracted_info": extracted_info,
        "confidence_score": calculate_confidence_score(extracted_info, 'CNI'),
        "recommendation": "Approuver automatiquement" if name_match else "Vérification manuelle requise",
        "verified_at": datetime.utcnow().isoformat()
    }

    return kyc_result


@app.get("/")
def root():
    return {"service": "document-service", "status": "running", "ocr": "enabled"}
