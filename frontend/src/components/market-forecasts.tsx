import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { TrendingUp, TrendingDown, Brain, ArrowRight, RefreshCw } from "lucide-react"
import { Progress } from "@/components/ui/progress"
import { useEffect, useState } from "react"
import { aiApi, PredictionResponse, SentimentResponse } from "@/services/aiApi"
import { stockApi } from "@/services/stockApi"

const FORECAST_STOCKS = ["AAPL", "TSLA", "NVDA", "AMZN"];

interface Forecast {
  stock: string;
  name: string;
  currentPrice: number;
  prediction: string;
  targetPrice: number;
  confidence: number;
  timeframe: string;
  sentiment: string;
  isPositive: boolean;
}

export function MarketForecasts() {
  const [forecasts, setForecasts] = useState<Forecast[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchForecasts = async () => {
    setLoading(true);
    
    try {
      const results = await Promise.all(
        FORECAST_STOCKS.map(async (symbol) => {
          const [prediction, sentiment, stockData] = await Promise.all([
            aiApi.getPricePrediction(symbol),
            aiApi.getSentimentAnalysis(symbol),
            stockApi.getStockPerformance(symbol, '1d').catch(() => null)
          ]);

          const currentPrice = stockData?.currentPrice || 0;
          const targetPrice = prediction.predictedPrice;
          const isPositive = targetPrice > currentPrice;
          
          let predictionLabel = "Neutral";
          if (sentiment.classification === "POSITIVE" && prediction.confidence > 75) {
            predictionLabel = "Bullish";
          } else if (sentiment.classification === "NEGATIVE") {
            predictionLabel = "Bearish";
          }

          return {
            stock: symbol,
            name: `${symbol} Inc.`,
            currentPrice,
            prediction: predictionLabel,
            targetPrice,
            confidence: prediction.confidence,
            timeframe: "30 days",
            sentiment: sentiment.classification,
            isPositive
          };
        })
      );

      setForecasts(results);
    } catch (err) {
      console.error("Failed to load forecasts:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchForecasts();
    const interval = setInterval(fetchForecasts, 10 * 60 * 1000); // 10 minutes
    return () => clearInterval(interval);
  }, []);

  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle className="flex items-center gap-2">
          <Brain className="h-5 w-5 text-primary" />
          Market Forecasts
        </CardTitle>
        <Button 
          variant="ghost" 
          size="icon" 
          onClick={fetchForecasts}
          disabled={loading}
        >
          <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
        </Button>
      </CardHeader>
      <CardContent className="space-y-4">
        {loading && forecasts.length === 0 ? (
          <div className="text-center text-muted-foreground py-8">Loading forecasts...</div>
        ) : (
          forecasts.map((forecast, index) => {
            const priceChange = forecast.targetPrice - forecast.currentPrice;
            const percentChange = forecast.currentPrice > 0 
              ? ((priceChange / forecast.currentPrice) * 100).toFixed(2)
              : "0.00";

            return (
              <div
                key={index}
                className="p-5 bg-secondary/30 rounded-lg border border-border/50 hover:border-primary/50 transition-colors"
              >
                <div className="flex items-start justify-between mb-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                      <div>
                        <div className="font-bold text-lg">{forecast.stock}</div>
                        <div className="text-xs text-muted-foreground">{forecast.name}</div>
                      </div>
                      <Badge
                        variant={
                          forecast.prediction === "Bullish"
                            ? "default"
                            : forecast.prediction === "Bearish"
                              ? "destructive"
                              : "secondary"
                        }
                      >
                        {forecast.prediction}
                      </Badge>
                    </div>
                    <div className="flex items-center gap-4 text-sm">
                      <div>
                        <span className="text-muted-foreground">Current: </span>
                        <span className="font-medium">${forecast.currentPrice.toFixed(2)}</span>
                      </div>
                      <ArrowRight className="h-4 w-4 text-muted-foreground" />
                      <div>
                        <span className="text-muted-foreground">Target: </span>
                        <span className="font-bold">${forecast.targetPrice.toFixed(2)}</span>
                      </div>
                      <div
                        className={`flex items-center gap-1 font-medium ${forecast.isPositive ? "text-profit" : "text-loss"}`}
                      >
                        {forecast.isPositive ? <TrendingUp className="h-4 w-4" /> : <TrendingDown className="h-4 w-4" />}
                        {forecast.isPositive ? "+" : ""}
                        {percentChange}%
                      </div>
                    </div>
                  </div>
                </div>

                <div className="space-y-3">
                  <div>
                    <div className="flex items-center justify-between text-sm mb-2">
                      <span className="text-muted-foreground">AI Confidence</span>
                      <span className="font-medium">{forecast.confidence.toFixed(1)}%</span>
                    </div>
                    <Progress value={forecast.confidence} className="h-2" />
                  </div>

                  <div className="flex items-center justify-between text-xs text-muted-foreground pt-2 border-t border-border/50">
                    <span>Timeframe: {forecast.timeframe}</span>
                    <span>Sentiment: {forecast.sentiment}</span>
                  </div>
                </div>
              </div>
            );
          })
        )}
      </CardContent>
    </Card>
  );
}