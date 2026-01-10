export interface UserProfile {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string | null;
}

export interface UpdateProfileRequest {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string | null;
}

export interface MessageResponse {
  message: string;
}

export interface WalletInfo {
  balance: number;
  totalDeposit: number;
  totalWithdrawal: number;
  pandingBalance: number;
  currency: string;
  totalInvested: number;
}

export interface TransactionHistory {
  id: number;
  actionType: string;
  amount: number;
  status: string;
  timestamp: string;
}

export interface DepositRequest {
  amount: number;
}

export interface WithdrawalRequest {
  amount: number;
}
