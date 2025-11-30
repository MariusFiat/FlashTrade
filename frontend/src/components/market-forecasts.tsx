
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { TrendingUp, TrendingDown, Brain, ArrowRight } from "lucide-react"
import { Progress } from "@/components/ui/progress"

const forecasts = [
  {
    stock: "AAPL",
    name: "Apple Inc.",
    currentPrice: 151.89,
    prediction: "Bullish",
    targetPrice: 165.2,
    confidence: 87,
    timeframe: "30 days",
    factors: ["Strong earnings", "Product launches", "Market sentiment"],
    isPositive: true,
  },
  {
    stock: "TSLA",
    name: "Tesla Inc.",
    currentPrice: 245.8,
    prediction: "Neutral",
    targetPrice: 252.4,
    confidence: 65,
    timeframe: "30 days",
    factors: ["Market volatility", "Production targets", "Competition"],
    isPositive: true,
  },
  {
    stock: "NVDA",
    name: "NVIDIA Corp.",
    currentPrice: 489.3,
    prediction: "Bullish",
    targetPrice: 525.0,
    confidence: 92,
    timeframe: "30 days",
    factors: ["AI boom", "Strong demand", "Innovation lead"],
    isPositive: true,
  },
  {
    stock: "AMZN",
    name: "Amazon.com Inc.",
    currentPrice: 172.4,
    prediction: "Bearish",
    targetPrice: 165.8,
    confidence: 71,
    timeframe: "30 days",
    factors: ["Economic slowdown", "Competition", "Cost pressures"],
    isPositive: false,
  },
]

export function MarketForecasts() {
  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle className="flex items-center gap-2">
          <Brain className="h-5 w-5 text-primary" />
          Market Forecasts
        </CardTitle>
        <Button variant="ghost" size="sm">
          View All
        </Button>
      </CardHeader>
      <CardContent className="space-y-4">
        {forecasts.map((forecast, index) => {
          const priceChange = forecast.targetPrice - forecast.currentPrice
          const percentChange = ((priceChange / forecast.currentPrice) * 100).toFixed(2)

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
                    <span className="font-medium">{forecast.confidence}%</span>
                  </div>
                  <Progress value={forecast.confidence} className="h-2" />
                </div>

                <div>
                  <div className="text-sm text-muted-foreground mb-2">Key Factors:</div>
                  <div className="flex flex-wrap gap-2">
                    {forecast.factors.map((factor, idx) => (
                      <Badge key={idx} variant="outline" className="text-xs">
                        {factor}
                      </Badge>
                    ))}
                  </div>
                </div>

                <div className="flex items-center justify-between text-xs text-muted-foreground pt-2 border-t border-border/50">
                  <span>Timeframe: {forecast.timeframe}</span>
                  <Button variant="link" size="sm" className="h-auto p-0 text-primary">
                    View Details →
                  </Button>
                </div>
              </div>
            </div>
          )
        })}
      </CardContent>
    </Card>
  )
}
