
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { TrendingUp, TrendingDown, Brain } from "lucide-react"

const predictions = [
  {
    stock: "AAPL",
    action: "Buy",
    confidence: 87,
    prediction: "+4.2%",
    reason: "Strong earnings expected",
    isPositive: true,
  },
  {
    stock: "TSLA",
    action: "Hold",
    confidence: 65,
    prediction: "+1.8%",
    reason: "Market volatility",
    isPositive: true,
  },
  {
    stock: "NVDA",
    action: "Buy",
    confidence: 92,
    prediction: "+6.5%",
    reason: "AI boom continues",
    isPositive: true,
  },
]

export function AIPredictions() {
  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Brain className="h-5 w-5 text-primary" />
          AI Predictions
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        {predictions.map((prediction, index) => (
          <div key={index} className="p-4 bg-secondary/30 rounded-lg border border-border/50">
            <div className="flex items-start justify-between mb-2">
              <div className="flex items-center gap-2">
                <span className="font-bold">{prediction.stock}</span>
                <Badge
                  variant={
                    prediction.action === "Buy" ? "default" : prediction.action === "Sell" ? "destructive" : "secondary"
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
                <span className={prediction.isPositive ? "text-profit" : "text-loss"}>{prediction.prediction}</span>
              </div>
            </div>
            <p className="text-xs text-muted-foreground mb-2">{prediction.reason}</p>
            <div className="flex items-center gap-2">
              <div className="flex-1 bg-secondary rounded-full h-2">
                <div
                  className="bg-primary h-2 rounded-full transition-all"
                  style={{ width: `${prediction.confidence}%` }}
                />
              </div>
              <span className="text-xs text-muted-foreground">{prediction.confidence}%</span>
            </div>
          </div>
        ))}
      </CardContent>
    </Card>
  )
}
