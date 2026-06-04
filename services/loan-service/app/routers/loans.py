from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import Loan, Repayment, LoanStatus, RepaymentStatus
from app.schemas import LoanCreateRequest, LoanResponse, LoanApproveRequest, LoanRejectRequest
from datetime import datetime, timedelta
from typing import List
import uuid
import math

router = APIRouter()


def calculate_monthly_payment(principal: float, annual_rate: float, months: int) -> float:
    """Formule: M = P * [r(1+r)^n] / [(1+r)^n - 1]"""
    if annual_rate == 0:
        return principal / months
    r = annual_rate / 100 / 12
    return principal * (r * math.pow(1 + r, months)) / (math.pow(1 + r, months) - 1)


def generate_repayment_schedule(loan: Loan) -> List[Repayment]:
    """Génère l'échéancier de remboursement"""
    repayments = []
    r = loan.interest_rate / 100 / 12
    monthly = loan.monthly_payment
    balance = loan.amount

    for i in range(1, loan.duration_months + 1):
        interest = balance * r
        principal = monthly - interest
        balance -= principal

        repayment = Repayment(
            id=uuid.uuid4(),
            loan_id=loan.id,
            due_date=datetime.utcnow() + timedelta(days=30 * i),
            amount=round(monthly, 2),
            principal=round(principal, 2),
            interest=round(interest, 2),
            status=RepaymentStatus.PENDING
        )
        repayments.append(repayment)
    return repayments


def calculate_credit_score(amount: float, duration_months: int) -> int:
    """Scoring simplifié basé sur le montant et la durée"""
    score = 700
    if amount > 1000000:
        score -= 50
    if duration_months > 36:
        score -= 30
    return max(300, min(850, score))


@router.post("", response_model=LoanResponse)
def apply_for_loan(request: LoanCreateRequest, db: Session = Depends(get_db)):
    credit_score = calculate_credit_score(request.amount, request.duration_months)

    loan = Loan(
        id=uuid.uuid4(),
        customer_id=request.customer_id,
        operator_id=request.operator_id,
        account_id=request.account_id,
        amount=request.amount,
        interest_rate=request.interest_rate,
        duration_months=request.duration_months,
        purpose=request.purpose,
        credit_score=credit_score,
        status=LoanStatus.SUBMITTED,
        requested_at=datetime.utcnow()
    )

    db.add(loan)
    db.commit()
    db.refresh(loan)
    return loan


@router.get("", response_model=List[LoanResponse])
def get_all_loans(db: Session = Depends(get_db)):
    return db.query(Loan).all()


@router.get("/{loan_id}", response_model=LoanResponse)
def get_loan(loan_id: uuid.UUID, db: Session = Depends(get_db)):
    loan = db.query(Loan).filter(Loan.id == loan_id).first()
    if not loan:
        raise HTTPException(status_code=404, detail="Prêt introuvable")
    return loan


@router.get("/customer/{customer_id}", response_model=List[LoanResponse])
def get_loans_by_customer(customer_id: uuid.UUID, db: Session = Depends(get_db)):
    return db.query(Loan).filter(Loan.customer_id == customer_id).all()


@router.put("/{loan_id}/approve", response_model=LoanResponse)
def approve_loan(loan_id: uuid.UUID, request: LoanApproveRequest, db: Session = Depends(get_db)):
    loan = db.query(Loan).filter(Loan.id == loan_id).first()
    if not loan:
        raise HTTPException(status_code=404, detail="Prêt introuvable")
    if loan.status not in [LoanStatus.SUBMITTED, LoanStatus.UNDER_REVIEW]:
        raise HTTPException(status_code=400, detail="Ce prêt ne peut pas être approuvé")

    loan.interest_rate = request.interest_rate
    if request.account_id:
        loan.account_id = request.account_id

    monthly = calculate_monthly_payment(loan.amount, loan.interest_rate, loan.duration_months)
    loan.monthly_payment = round(monthly, 2)
    loan.total_amount = round(monthly * loan.duration_months, 2)
    loan.status = LoanStatus.APPROVED
    loan.approved_at = datetime.utcnow()

    # Générer l'échéancier
    repayments = generate_repayment_schedule(loan)
    for r in repayments:
        db.add(r)

    db.commit()
    db.refresh(loan)
    return loan


@router.put("/{loan_id}/reject", response_model=LoanResponse)
def reject_loan(loan_id: uuid.UUID, request: LoanRejectRequest, db: Session = Depends(get_db)):
    loan = db.query(Loan).filter(Loan.id == loan_id).first()
    if not loan:
        raise HTTPException(status_code=404, detail="Prêt introuvable")

    loan.status = LoanStatus.REJECTED
    loan.rejection_reason = request.reason
    db.commit()
    db.refresh(loan)
    return loan


@router.post("/{loan_id}/repay/{repayment_id}", response_model=LoanResponse)
def make_repayment(loan_id: uuid.UUID, repayment_id: uuid.UUID, db: Session = Depends(get_db)):
    repayment = db.query(Repayment).filter(
        Repayment.id == repayment_id,
        Repayment.loan_id == loan_id
    ).first()

    if not repayment:
        raise HTTPException(status_code=404, detail="Échéance introuvable")
    if repayment.status == RepaymentStatus.PAID:
        raise HTTPException(status_code=400, detail="Échéance déjà payée")

    repayment.status = RepaymentStatus.PAID
    repayment.paid_at = datetime.utcnow()

    # Vérifier si toutes les échéances sont payées
    loan = db.query(Loan).filter(Loan.id == loan_id).first()
    all_paid = all(r.status == RepaymentStatus.PAID for r in loan.repayments)
    if all_paid:
        loan.status = LoanStatus.CLOSED
        loan.closed_at = datetime.utcnow()

    db.commit()
    db.refresh(loan)
    return loan
