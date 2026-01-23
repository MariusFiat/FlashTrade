import type { StockPerformanceResponse, MarketDataResponse, TradeHistoryResponse } from '@/types/stock';

const API_BASE_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8080';

class StockApiService {
  private baseURL: string;

  constructor() {
    this.baseURL = API_BASE_URL;
  }

  async getStockPerformance(symbol: string, range: string = '1d'): Promise<StockPerformanceResponse> {
    const response = await fetch(`${this.baseURL}/api/stocks/${symbol}/performance?range=${range}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to fetch performance for ${symbol}`);
    }

    return response.json();
  }

  async getAllStocks(): Promise<MarketDataResponse> {
    const response = await fetch(`${this.baseURL}/api/market/stocks`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error('Failed to fetch market data');
    }

    return response.json();
  }

  async getTradeHistory(userId: string): Promise<TradeHistoryResponse> {
    const response = await fetch(`${this.baseURL}/api/trades/history/${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error('Failed to fetch trade history');
    }

    return response.json();
  }
}

export const stockApi = new StockApiService();
