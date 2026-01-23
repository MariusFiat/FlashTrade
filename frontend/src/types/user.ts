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

export interface PortfolioItem {
  id: number;
  stock: string;
  shares: number;
  portfolioValue: number;
  allocation: number;
}

export interface PortfolioSummary {
  items: PortfolioItem[];
  totalInvested: number;
  totalValue: number;
  totalReturn: number;
}

export interface PortfolioHistoryPoint {
  value: number;
  date: string;
}

export interface PortfolioPerformance {
  history: PortfolioHistoryPoint[];
  currentTotalValue: number;
}
