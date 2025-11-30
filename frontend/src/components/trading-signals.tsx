
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { TrendingUp, TrendingDown, Circle, Clock } from "lucide-react"

const signals = [
  {
    stock: "AAPL",
    action: "Strong Buy",
    price: 151.89,
    target: 165.2,
    stopLoss: 148.5,
    confidence: 87,
    time: "5 minutes ago",
    status: "active",
    type: "buy",
  },
  {
    stock: "GOOGL",
    action: "Buy",
    price: 142.5,
    target: 152.0,
    stopLoss: 138.0,
    confidence: 78,
    time: "12 minutes ago",
    status: "active",
    type: "buy",
  },
  {
    stock: "AMZN",
    action: "Sell",
    price: 172.4,
    target: 165.8,
    stopLoss: 175.0,
    confidence: 71,
    time: "25 minutes ago",
    status: "active",
    type: "sell",
  },
  {
    stock: "MSFT",
    action: "Hold",
    price: 395.2,
    target: 405.0,
    stopLoss: 390.0,
    confidence: 62,
    time: "1 hour ago",
    status: "monitoring",
    type: "hold",
  },
]

export function TradingSignals() {
  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle className="flex items-center gap-2">
          <Circle className="h-5 w-5 text-primary fill-primary animate-pulse" />
          Live Trading Signals
        </CardTitle>
        <Badge variant="secondary" className="bg-profit/20 text-profit">
          {signals.filter((s) => s.status === "active").length} Active
        </Badge>
      </CardHeader>
      <CardContent className="space-y-3">
        {signals.map((signal, index) => (
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
                      {signal.confidence}% confidence
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
        ))}
      </CardContent>
    </Card>
  )
}
