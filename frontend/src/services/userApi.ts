import { getCookie, deleteCookie } from '@/lib/cookies';
import type { 
  UserProfile, 
  UpdateProfileRequest, 
  MessageResponse, 
  WalletInfo, 
  TransactionHistory,
  PortfolioSummary,
  PortfolioPerformance
} from '@/types/user';

const API_BASE_URL = import.meta.env.VITE_USER_SERVICE_URL || 'http://localhost:8082';
const TOKEN_COOKIE_NAME = 'access_token';

class UserApiService {
  private baseURL: string;

  constructor() {
    this.baseURL = API_BASE_URL;
  }

  private getAuthHeaders(): HeadersInit {
    const token = getCookie(TOKEN_COOKIE_NAME);
    return {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    };
  }

  private handleUnauthorized(response: Response): void {
    if (response.status === 401 || response.status === 403) {
      // Clear the token
      deleteCookie(TOKEN_COOKIE_NAME);
      // Redirect to login
      window.location.href = '/login';
    }
  }

  async getUserProfile(): Promise<UserProfile> {
    const response = await fetch(`${this.baseURL}/user_info/list_user_details`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      this.handleUnauthorized(response);
      throw new Error('Failed to fetch user profile');
    }

    return response.json();
  }

  async updateUserProfile(data: UpdateProfileRequest): Promise<MessageResponse> {
    const response = await fetch(`${this.baseURL}/user_info/edit_user_details`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      this.handleUnauthorized(response);
      throw new Error('Failed to update user profile');
    }

    return response.json();
  }

  async getWalletInfo(): Promise<WalletInfo> {
    const response = await fetch(`${this.baseURL}/user_info/get_wallet_info`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      this.handleUnauthorized(response);
      throw new Error('Failed to fetch wallet info');
    }

    return response.json();
  }

  async deposit(amount: number): Promise<MessageResponse> {
    const response = await fetch(`${this.baseURL}/user_info/deposit?amount=${amount}`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      this.handleUnauthorized(response);
      const error = await response.json();
      throw new Error(error.message || 'Failed to process deposit');
    }

    return response.json();
  }

  async withdrawal(amount: number): Promise<MessageResponse> {
    const response = await fetch(`${this.baseURL}/user_info/withdrawal?amount=${amount}`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      this.handleUnauthorized(response);
      const error = await response.json();
      throw new Error(error.message || 'Failed to process withdrawal');
    }

    return response.json();
  }

  async getTransactions(): Promise<TransactionHistory[]> {
    const response = await fetch(`${this.baseURL}/user_info/get_transactions`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      this.handleUnauthorized(response);
      throw new Error('Failed to fetch transactions');
    }

    return response.json();
  }

  async getPortfolio(): Promise<PortfolioSummary> {
    const response = await fetch(`${this.baseURL}/user_info/get_portfolio`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      this.handleUnauthorized(response);
      throw new Error('Failed to fetch portfolio');
    }

    return response.json();
  }

  async getPortfolioPerformance(range: string = '1w'): Promise<PortfolioPerformance> {
    const response = await fetch(`${this.baseURL}/user_info/get_portfolio_performance?range=${range}`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      this.handleUnauthorized(response);
      throw new Error('Failed to fetch portfolio performance');
    }

    return response.json();
  }
}

export const userApi = new UserApiService();
