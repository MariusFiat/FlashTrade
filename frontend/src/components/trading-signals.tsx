import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { TrendingUp, TrendingDown, Circle, Clock, RefreshCw } from "lucide-react"
import { useEffect, useState } from "react"
import { aiApi } from "@/services/aiApi"
import { stockApi } from "@/services/stockApi"

const SIGNAL_STOCKS = ["AAPL", "GOOGL", "AMZN", "MSFT"];

interface TradingSignal {
  stock: string;
  action: string;
  price: number;
  target: number;
  stopLoss: number;
  confidence: number;
  time: string;
  status: string;
  type: "buy" | "sell" | "hold";
}

export function TradingSignals() {
  const [signals, setSignals] = useState<TradingSignal[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchSignals = async () => {
    setLoading(true);
    
    try {
      const results = await Promise.all(
        SIGNAL_STOCKS.map(async (symbol) => {
          const [prediction, sentiment, stockData] = await Promise.all([
            aiApi.getPricePrediction(symbol),
            aiApi.getSentimentAnalysis(symbol),
            stockApi.getStockPerformance(symbol, '1d').catch(() => null)
          ]);

          const currentPrice = stockData?.currentPrice || prediction.predictedPrice * 0.98;
          const targetPrice = prediction.predictedPrice;
          
          let action = "Hold";
          let type: "buy" | "sell" | "hold" = "hold";
          
          if (sentiment.classification === "POSITIVE" && prediction.confidence > 80) {
            action = "Strong Buy";
            type = "buy";
          } else if (sentiment.classification === "POSITIVE" && prediction.confidence > 65) {
            action = "Buy";
            type = "buy";
          } else if (sentiment.classification === "NEGATIVE") {
            action = "Sell";
            type = "sell";
          }

          const stopLoss = type === "buy" 
            ? currentPrice * 0.97 
            : currentPrice * 1.03;

          return {
            stock: symbol,
            action,
            price: currentPrice,
            target: targetPrice,
            stopLoss,
            confidence: prediction.confidence,
            time: "Just now",
            status: prediction.confidence > 70 ? "active" : "monitoring",
            type
          };
        })
      );

      setSignals(results);
    } catch (err) {
      console.error("Failed to load signals:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSignals();
    const interval = setInterval(fetchSignals, 3 * 60 * 1000); // 3 minutes
    return () => clearInterval(interval);
  }, []);

  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle className="flex items-center gap-2">
          <Circle className="h-5 w-5 text-primary fill-primary animate-pulse" />
          Live Trading Signals
        </CardTitle>
        <div className="flex items-center gap-2">
          <Badge variant="secondary" className="bg-profit/20 text-profit">
            {signals.filter((s) => s.status === "active").length} Active
          </Badge>
          <Button 
            variant="ghost" 
            size="icon" 
            onClick={fetchSignals}
            disabled={loading}
          >
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          </Button>
        </div>
      </CardHeader>
      <CardContent className="space-y-3">
        {loading && signals.length === 0 ? (
          <div className="text-center text-muted-foreground py-8">Loading signals...</div>
        ) : (
          signals.map((signal, index) => (
            <div key={index} className="p-4 bg-secondary/30 rounded-lg border border-border/50">
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div
                    className={`p-2 rounded-lg ${
                      signal.type === "buy" ? "bg-profit/10" : signal.type === "sell" ? "bg-loss/10" : "bg-secondary"
                    }`}
                  >
                    {signal.type === "buy" ? (
                      <TrendingUp className="h-5 w-5 text-profit" />
                    ) : signal.type === "sell" ? (
                      <TrendingDown className="h-5 w-5 text-loss" />
                    ) : (
                      <Circle className="h-5 w-5 text-muted-foreground" />
                    )}
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-bold">{signal.stock}</span>
                      <Badge
                        variant={
                          signal.action.includes("Buy")
                            ? "default"
                            : signal.action === "Sell"
                              ? "destructive"
                              : "secondary"
                        }
                        className="text-xs"
                      >
                        {signal.action}
                      </Badge>
                      <Badge variant="outline" className="text-xs">
                        {signal.confidence.toFixed(0)}% confidence
                      </Badge>
                    </div>
                    <div className="flex items-center gap-1 text-xs text-muted-foreground mt-1">
                      <Clock className="h-3 w-3" />
                      {signal.time}
                    </div>
                  </div>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-4 text-sm">
                <div>
                  <div className="text-muted-foreground text-xs">Entry</div>
                  <div className="font-medium">${signal.price.toFixed(2)}</div>
                </div>
                <div>
                  <div className="text-muted-foreground text-xs">Target</div>
                  <div className="font-medium text-profit">${signal.target.toFixed(2)}</div>
                </div>
                <div>
                  <div className="text-muted-foreground text-xs">Stop Loss</div>
                  <div className="font-medium text-loss">${signal.stopLoss.toFixed(2)}</div>
                </div>
              </div>

              <div className="flex items-center justify-between mt-4 pt-3 border-t border-border/50">
                <Badge variant={signal.status === "active" ? "default" : "secondary"} className="text-xs">
                  {signal.status}
                </Badge>
                <Button variant="ghost" size="sm" className="h-8">
                  Execute Trade
                </Button>
              </div>
            </div>
          ))
        )}
      </CardContent>
    </Card>
  );
}