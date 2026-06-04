from pydantic import BaseModel, UUID4
from typing import Optional, List
from datetime import datetime
from app.models import LoanStatus, RepaymentStatus


class LoanCreateRequest(BaseModel):
    customer_id: UUID4
    operator_id: Optional[UUID4] = None
    account_id: Optional[UUID4] = None
    amount: float
    interest_rate: float = 8.0
    duration_months: int
    purpose: Optional[str] = None


class LoanApproveRequest(BaseModel):
    interest_rate: float
    account_id: Optional[UUID4] = None


class LoanRejectRequest(BaseModel):
    reason: str


class RepaymentResponse(BaseModel):
    id: UUID4
    loan_id: UUID4
    due_date: datetime
    amount: float
    principal: float
    interest: float
    status: RepaymentStatus
    paid_at: Optional[datetime] = None

    class Config:
        from_attributes = True


class LoanResponse(BaseModel):
    id: UUID4
    customer_id: UUID4
    operator_id: Optional[UUID4] = None
    account_id: Optional[UUID4] = None
    amount: float
    interest_rate: float
    duration_months: int
    monthly_payment: Optional[float] = None
    total_amount: Optional[float] = None
    status: LoanStatus
    credit_score: int
    purpose: Optional[str] = None
    rejection_reason: Optional[str] = None
    requested_at: datetime
    approved_at: Optional[datetime] = None
    repayments: List[RepaymentResponse] = []

    class Config:
        from_attributes = True
