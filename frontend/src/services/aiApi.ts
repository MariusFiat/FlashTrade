const API_BASE_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8083';

export interface AIOverviewStats {
  predictionAccuracy: number;
  accuracyChange: number;
  profitableSignals: {
    successful: number;
    total: number;
    successRate: number;
  };
  riskScore: string;
  riskDescription: string;
  activeAlerts: {
    total: number;
    highPriority: number;
  };
}

export interface RiskMetric {
  label: string;
  value: number;
  status: string;
  color: string;
}

export interface RiskRecommendation {
  type: 'warning' | 'success' | 'info';
  message: string;
}

export interface RiskAnalysisData {
  overallRiskScore: string;
  riskDescription: string;
  metrics: RiskMetric[];
  recommendations: RiskRecommendation[];
}

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp?: string;
}

export interface ChatResponse {
  response: string;
  suggestions?: string[];
}

export interface PredictionResponse {
  symbol: string;
  predictedPrice: number;
  currentPrice: number;
  confidence: number;
  timestamp: number;
}

export interface SentimentResponse {
  symbol: string;
  score: number;
  sentiment: 'POSITIVE' | 'NEGATIVE' | 'NEUTRAL';
  articleCount: number;
  timestamp: number;
}

export interface PredictionHistory {
  id: number;
  symbol: string;
  predictedPrice: number;
  currentPrice: number;
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

  async getOverviewStats(symbols: string[] = ['AAPL', 'GOOGL', 'MSFT', 'TSLA', 'AMZN']): Promise<AIOverviewStats> {
    try {
      // Fetch predictions and sentiments for multiple symbols
      const predictions = await Promise.all(
        symbols.map(symbol => this.getPricePrediction(symbol).catch(() => null))
      );
      
      const sentiments = await Promise.all(
        symbols.map(symbol => this.getSentimentAnalysis(symbol).catch(() => null))
      );

      // Calculate prediction accuracy (based on confidence scores)
      const validPredictions = predictions.filter(p => p !== null);
      const avgConfidence = validPredictions.length > 0
        ? validPredictions.reduce((sum, p) => sum + (p?.confidence || 0), 0) / validPredictions.length
        : 0;

      // Calculate profitable signals (based on sentiment and predictions)
      const positiveSignals = sentiments.filter(s => s && s.sentiment === 'POSITIVE').length;
      const totalSignals = sentiments.filter(s => s !== null).length;
      const successRate = totalSignals > 0 ? (positiveSignals / totalSignals) * 100 : 0;

      // Calculate risk score based on sentiment volatility
      const sentimentScores = sentiments.filter(s => s !== null).map(s => s?.score || 0);
      const avgSentiment = sentimentScores.length > 0
        ? sentimentScores.reduce((sum, score) => sum + score, 0) / sentimentScores.length
        : 0;
      
      let riskScore = 'Medium';
      let riskDescription = 'Balanced portfolio';
      if (avgSentiment > 0.6) {
        riskScore = 'Low';
        riskDescription = 'Well-diversified';
      } else if (avgSentiment < 0.3) {
        riskScore = 'High';
        riskDescription = 'High volatility';
      }

      // Count active alerts (based on significant price movements)
      const significantMovements = validPredictions.filter(p => 
        p && Math.abs(p.predictedPrice - p.currentPrice) / p.currentPrice > 0.05
      ).length;

      return {
        predictionAccuracy: avgConfidence,
        accuracyChange: Math.random() * 5 - 2.5, // Mock change
        profitableSignals: {
          successful: positiveSignals,
          total: totalSignals,
          successRate: successRate
        },
        riskScore,
        riskDescription,
        activeAlerts: {
          total: significantMovements,
          highPriority: Math.floor(significantMovements / 2)
        }
      };
    } catch (error) {
      console.error('Error fetching overview stats:', error);
      throw error;
    }
  }

  async getRiskAnalysis(symbols: string[] = ['AAPL', 'GOOGL', 'MSFT', 'TSLA', 'AMZN']): Promise<RiskAnalysisData> {
    try {
      const predictions = await Promise.all(
        symbols.map(symbol => this.getPricePrediction(symbol).catch(() => null))
      );
      
      const sentiments = await Promise.all(
        symbols.map(symbol => this.getSentimentAnalysis(symbol).catch(() => null))
      );

      const validPredictions = predictions.filter(p => p !== null);
      const validSentiments = sentiments.filter(s => s !== null);

      // Calculate portfolio volatility based on prediction variance
      const priceChanges = validPredictions.map(p => 
        p ? Math.abs(p.predictedPrice - p.currentPrice) / p.currentPrice : 0
      );
      const avgVolatility = priceChanges.length > 0
        ? (priceChanges.reduce((sum, change) => sum + change, 0) / priceChanges.length) * 100
        : 0;

      // Calculate diversification score
      const sentimentDistribution = {
        positive: validSentiments.filter(s => s?.sentiment === 'POSITIVE').length,
        neutral: validSentiments.filter(s => s?.sentiment === 'NEUTRAL').length,
        negative: validSentiments.filter(s => s?.sentiment === 'NEGATIVE').length
      };
      const diversificationScore = validSentiments.length > 0
        ? (1 - Math.max(...Object.values(sentimentDistribution)) / validSentiments.length) * 100
        : 0;

      // Calculate exposure risk
      const highConfidencePredictions = validPredictions.filter(p => p && p.confidence > 0.7).length;
      const exposureRisk = validPredictions.length > 0
        ? (1 - highConfidencePredictions / validPredictions.length) * 100
        : 0;

      // Determine overall risk score
      const avgRisk = (avgVolatility + (100 - diversificationScore) + exposureRisk) / 3;
      let overallRiskScore = 'Medium';
      let riskDescription = 'Your portfolio has balanced risk exposure';
      
      if (avgRisk < 30) {
        overallRiskScore = 'Low';
        riskDescription = 'Your portfolio has low risk exposure';
      } else if (avgRisk > 60) {
        overallRiskScore = 'High';
        riskDescription = 'Your portfolio has high risk exposure';
      }

      // Generate recommendations
      const recommendations: RiskRecommendation[] = [];
      
      if (avgVolatility > 50) {
        recommendations.push({
          type: 'warning',
          message: `High portfolio volatility detected (${avgVolatility.toFixed(1)}%)`
        });
      }
      
      if (diversificationScore > 60) {
        recommendations.push({
          type: 'success',
          message: 'Well-balanced risk/reward ratio across assets'
        });
      } else {
        recommendations.push({
          type: 'warning',
          message: 'Consider diversifying your portfolio further'
        });
      }
      
      if (exposureRisk > 40) {
        recommendations.push({
          type: 'info',
          message: 'Some positions have lower confidence - consider rebalancing'
        });
      }

      // Check for concentrated positions
      const negativeCount = sentimentDistribution.negative;
      if (negativeCount > validSentiments.length * 0.4) {
        recommendations.push({
          type: 'warning',
          message: `High concentration in negative sentiment stocks (${negativeCount}/${validSentiments.length})`
        });
      }

      return {
        overallRiskScore,
        riskDescription,
        metrics: [
          {
            label: 'Portfolio Volatility',
            value: Math.min(avgVolatility, 100),
            status: avgVolatility < 30 ? 'Low' : avgVolatility < 60 ? 'Medium' : 'High',
            color: avgVolatility < 30 ? 'bg-profit' : avgVolatility < 60 ? 'bg-warning' : 'bg-loss'
          },
          {
            label: 'Diversification Score',
            value: Math.min(diversificationScore, 100),
            status: diversificationScore > 60 ? 'Good' : diversificationScore > 40 ? 'Fair' : 'Poor',
            color: diversificationScore > 60 ? 'bg-profit' : diversificationScore > 40 ? 'bg-warning' : 'bg-loss'
          },
          {
            label: 'Exposure Risk',
            value: Math.min(exposureRisk, 100),
            status: exposureRisk < 30 ? 'Low' : exposureRisk < 60 ? 'Medium' : 'High',
            color: exposureRisk < 30 ? 'bg-profit' : exposureRisk < 60 ? 'bg-warning' : 'bg-loss'
          }
        ],
        recommendations
      };
    } catch (error) {
      console.error('Error fetching risk analysis:', error);
      throw error;
    }
  }

  async sendChatMessage(message: string, context?: { symbols?: string[] }): Promise<ChatResponse> {
    try {
      // For now, we'll create intelligent responses based on the message content
      // In production, this would call a real AI chat endpoint
      
      const lowerMessage = message.toLowerCase();
      let response = '';
      const suggestions: string[] = [];

      if (lowerMessage.includes('buy') || lowerMessage.includes('recommend')) {
        // Fetch top predictions
        const symbols = context?.symbols || ['AAPL', 'GOOGL', 'MSFT', 'TSLA', 'AMZN'];
        const predictions = await Promise.all(
          symbols.slice(0, 3).map(symbol => 
            this.getPricePrediction(symbol).catch(() => null)
          )
        );

        const validPredictions = predictions.filter(p => p !== null);
        const topPredictions = validPredictions
          .sort((a, b) => (b?.confidence || 0) - (a?.confidence || 0))
          .slice(0, 3);

        if (topPredictions.length > 0) {
          response = `Based on current AI analysis, here are my top recommendations:\n\n`;
          topPredictions.forEach((pred, idx) => {
            if (pred) {
              const change = ((pred.predictedPrice - pred.currentPrice) / pred.currentPrice * 100).toFixed(2);
              response += `${idx + 1}. ${pred.symbol}: Predicted ${change}% ${change > '0' ? 'gain' : 'loss'} (${pred.confidence.toFixed(1)}% confidence)\n`;
            }
          });
        } else {
          response = 'I recommend diversifying across tech and stable stocks. Would you like specific stock analysis?';
        }

        suggestions.push('Analyze AAPL', 'Show risk analysis', 'Market trends today');
      } else if (lowerMessage.includes('risk') || lowerMessage.includes('portfolio')) {
        const riskData = await this.getRiskAnalysis(context?.symbols);
        response = `Your portfolio risk analysis:\n\n`;
        response += `Overall Risk: ${riskData.overallRiskScore}\n`;
        response += `${riskData.riskDescription}\n\n`;
        response += `Key Metrics:\n`;
        riskData.metrics.forEach(metric => {
          response += `- ${metric.label}: ${metric.status} (${metric.value.toFixed(1)}%)\n`;
        });

        suggestions.push('How to reduce risk?', 'Diversification tips', 'Rebalance portfolio');
      } else if (lowerMessage.includes('predict') || lowerMessage.includes('price')) {
        const symbol = lowerMessage.match(/[A-Z]{2,5}/)?.[0] || 'AAPL';
        const prediction = await this.getPricePrediction(symbol);
        
        const change = ((prediction.predictedPrice - prediction.currentPrice) / prediction.currentPrice * 100).toFixed(2);
        response = `${symbol} Price Prediction:\n\n`;
        response += `Current: $${prediction.currentPrice.toFixed(2)}\n`;
        response += `Predicted: $${prediction.predictedPrice.toFixed(2)}\n`;
        response += `Expected change: ${change}%\n`;
        response += `Confidence: ${prediction.confidence.toFixed(1)}%\n`;
        response += `Timestamp: ${prediction.timestamp}`;

        suggestions.push(`Sentiment for ${symbol}`, 'Compare with GOOGL', 'Historical accuracy');
      } else if (lowerMessage.includes('sentiment') || lowerMessage.includes('news')) {
        const symbol = lowerMessage.match(/[A-Z]{2,5}/)?.[0] || 'AAPL';
        const sentiment = await this.getSentimentAnalysis(symbol);
        
        response = `${symbol} Sentiment Analysis:\n\n`;
        response += `Overall Sentiment: ${sentiment.sentiment}\n`;
        response += `Confidence: ${(sentiment.score * 100).toFixed(1)}%\n`;
        response += `Based on ${sentiment.articleCount} recent articles\n\n`;
        response += sentiment.sentiment === 'POSITIVE' 
          ? '📈 Market sentiment is bullish' 
          : sentiment.sentiment === 'NEGATIVE'
          ? '📉 Market sentiment is bearish'
          : '➡️ Market sentiment is neutral';

        suggestions.push(`Price prediction for ${symbol}`, 'Latest news', 'Compare sentiment');
      } else {
        response = `I'm your AI trading assistant. I can help you with:\n\n`;
        response += `• Stock price predictions\n`;
        response += `• Sentiment analysis\n`;
        response += `• Portfolio risk assessment\n`;
        response += `• Trading recommendations\n\n`;
        response += `What would you like to know?`;

        suggestions.push('Recommend stocks to buy', 'Analyze my portfolio risk', 'Predict AAPL price');
      }

      return { response, suggestions };
    } catch (error) {
      console.error('Error in chat:', error);
      return {
        response: 'I apologize, but I encountered an error processing your request. Please try again or rephrase your question.',
        suggestions: ['Check system status', 'Try a different question']
      };
    }
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