import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { TrendingUp, TrendingDown, Brain, RefreshCw } from "lucide-react"
import { useEffect, useState } from "react"
import { aiApi, PredictionResponse, SentimentResponse } from "@/services/aiApi"

const WATCHED_STOCKS = ["AAPL", "TSLA", "NVDA"];

interface AIPrediction {
  stock: string;
  predictedPrice: number;
  confidence: number;
  sentiment: string;
  sentimentScore: number;
  isPositive: boolean;
  action: string;
}

export function AIPredictions() {
  const [predictions, setPredictions] = useState<AIPrediction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPredictions = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const results = await Promise.all(
        WATCHED_STOCKS.map(async (symbol) => {
          const [prediction, sentiment] = await Promise.all([
            aiApi.getPricePrediction(symbol),
            aiApi.getSentimentAnalysis(symbol)
          ]);

          const action = sentiment.classification === "POSITIVE" && prediction.confidence > 70 
            ? "Buy" 
            : sentiment.classification === "NEGATIVE" 
            ? "Sell" 
            : "Hold";

          return {
            stock: symbol,
            predictedPrice: prediction.predictedPrice,
            confidence: prediction.confidence,
            sentiment: sentiment.classification,
            sentimentScore: sentiment.score,
            isPositive: sentiment.classification === "POSITIVE",
            action
          };
        })
      );

      setPredictions(results);
    } catch (err) {
      setError("Failed to load AI predictions");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPredictions();
    // Refresh every 5 minutes
    const interval = setInterval(fetchPredictions, 5 * 60 * 1000);
    return () => clearInterval(interval);
  }, []);

  if (loading && predictions.length === 0) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Brain className="h-5 w-5 text-primary" />
            AI Predictions
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center text-muted-foreground py-8">Loading predictions...</div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Brain className="h-5 w-5 text-primary" />
            AI Predictions
          </div>
          <Button 
            variant="ghost" 
            size="icon" 
            onClick={fetchPredictions}
            disabled={loading}
          >
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          </Button>
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        {error && (
          <div className="text-sm text-destructive p-3 bg-destructive/10 rounded-lg">
            {error}
          </div>
        )}
        
        {predictions.map((prediction, index) => (
          <div key={index} className="p-4 bg-secondary/30 rounded-lg border border-border/50">
            <div className="flex items-start justify-between mb-2">
              <div className="flex items-center gap-2">
                <span className="font-bold">{prediction.stock}</span>
                <Badge
                  variant={
                    prediction.action === "Buy" ? "default" : 
                    prediction.action === "Sell" ? "destructive" : "secondary"
                  }
                  className="text-xs"
                >
                  {prediction.action}
                </Badge>
              </div>
              <div className="flex items-center gap-1 text-sm">
                {prediction.isPositive ? (
                  <TrendingUp className="h-4 w-4 text-profit" />
                ) : (
                  <TrendingDown className="h-4 w-4 text-loss" />
                )}
                <span className={prediction.isPositive ? "text-profit" : "text-loss"}>
                  {prediction.sentiment}
                </span>
              </div>
            </div>
            <div className="space-y-1 text-sm">
              <div className="flex justify-between">
                <span className="text-muted-foreground">Predicted Price:</span>
                <span className="font-medium">${prediction.predictedPrice.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Confidence:</span>
                <span className="font-medium">{prediction.confidence.toFixed(1)}%</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Sentiment Score:</span>
                <span className="font-medium">{prediction.sentimentScore.toFixed(2)}</span>
              </div>
            </div>
          </div>
        ))}
      </CardContent>
    </Card>
  );
}