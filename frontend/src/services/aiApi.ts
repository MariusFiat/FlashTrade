const API_BASE_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8083';

export interface PredictionResponse {
  symbol: string;
  predictedPrice: number;
  confidence: number;
  timestamp: number;
}

export interface SentimentResponse {
  symbol: string;
  score: number;
  classification: 'POSITIVE' | 'NEGATIVE' | 'NEUTRAL';
  articleCount: number;
  timestamp: number;
}

export interface PredictionHistory {
  id: number;
  symbol: string;
  predictedPrice: number;
  confidence: number;
  createdAt: string;
}

export interface SentimentHistory {
  id: number;
  symbol: string;
  score: number;
  classification: string;
  articleCount: number;
  createdAt: string;
}

class AIApiService {
  private baseURL: string;

  constructor() {
    this.baseURL = API_BASE_URL;
  }

  // Price Prediction
  async getPricePrediction(symbol: string): Promise<PredictionResponse> {
    const response = await fetch(`${this.baseURL}/api/ai/predictions/${symbol}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to get prediction for ${symbol}`);
    }

    return response.json();
  }

  // Sentiment Analysis
  async getSentimentAnalysis(symbol: string): Promise<SentimentResponse> {
    const response = await fetch(`${this.baseURL}/api/ai/sentiment/${symbol}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to get sentiment for ${symbol}`);
    }

    return response.json();
  }

  // Health Check
  async healthCheck(): Promise<{ status: string; service: string; timestamp: string }> {
    const response = await fetch(`${this.baseURL}/api/ai/health`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error('AI service health check failed');
    }

    return response.json();
  }
}

export const aiApi = new AIApiService();