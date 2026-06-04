from sqlalchemy import Column, String, Float, Integer, Enum, DateTime, ForeignKey
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship
from app.database import Base
from datetime import datetime
import uuid
import enum


class LoanStatus(str, enum.Enum):
    DRAFT = "DRAFT"
    SUBMITTED = "SUBMITTED"
    UNDER_REVIEW = "UNDER_REVIEW"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
    ACTIVE = "ACTIVE"
    CLOSED = "CLOSED"
    DEFAULTED = "DEFAULTED"


class RepaymentStatus(str, enum.Enum):
    PENDING = "PENDING"
    PAID = "PAID"
    OVERDUE = "OVERDUE"
    PARTIAL = "PARTIAL"


class Loan(Base):
    __tablename__ = "loans"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    customer_id = Column(UUID(as_uuid=True), nullable=False)
    operator_id = Column(UUID(as_uuid=True), nullable=True)
    account_id = Column(UUID(as_uuid=True), nullable=True)
    amount = Column(Float, nullable=False)
    interest_rate = Column(Float, nullable=False, default=8.0)
    duration_months = Column(Integer, nullable=False)
    monthly_payment = Column(Float, nullable=True)
    total_amount = Column(Float, nullable=True)
    status = Column(Enum(LoanStatus), default=LoanStatus.SUBMITTED)
    credit_score = Column(Integer, default=500)
    purpose = Column(String, nullable=True)
    rejection_reason = Column(String, nullable=True)
    requested_at = Column(DateTime, default=datetime.utcnow)
    approved_at = Column(DateTime, nullable=True)
    closed_at = Column(DateTime, nullable=True)

    repayments = relationship("Repayment", back_populates="loan")


class Repayment(Base):
    __tablename__ = "repayments"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    loan_id = Column(UUID(as_uuid=True), ForeignKey("loans.id"), nullable=False)
    due_date = Column(DateTime, nullable=False)
    amount = Column(Float, nullable=False)
    principal = Column(Float, nullable=False)
    interest = Column(Float, nullable=False)
    status = Column(Enum(RepaymentStatus), default=RepaymentStatus.PENDING)
    paid_at = Column(DateTime, nullable=True)

    loan = relationship("Loan", back_populates="repayments")
