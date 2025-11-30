
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Shield, AlertTriangle, TrendingUp } from "lucide-react"
import { Progress } from "@/components/ui/progress"

export function RiskAnalysis() {
  const riskMetrics = [
    { label: "Portfolio Volatility", value: 45, status: "Medium", color: "bg-warning" },
    { label: "Diversification Score", value: 78, status: "Good", color: "bg-profit" },
    { label: "Exposure Risk", value: 38, status: "Low", color: "bg-profit" },
  ]

  const recommendations = [
    { type: "warning", message: "High concentration in tech sector (65%)" },
    { type: "success", message: "Well-balanced risk/reward ratio" },
    { type: "info", message: "Consider rebalancing TSLA position" },
  ]

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Shield className="h-5 w-5 text-primary" />
          Risk Analysis
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="space-y-4">
          {riskMetrics.map((metric, index) => (
            <div key={index} className="space-y-2">
              <div className="flex items-center justify-between text-sm">
                <span className="text-muted-foreground">{metric.label}</span>
                <span className="font-medium">{metric.status}</span>
              </div>
              <Progress value={metric.value} className="h-2" />
            </div>
          ))}
        </div>

        <div className="space-y-3 pt-4 border-t border-border/50">
          <div className="text-sm font-medium">Recommendations</div>
          {recommendations.map((rec, index) => (
            <div key={index} className="flex items-start gap-3 p-3 bg-secondary/30 rounded-lg">
              <AlertTriangle
                className={`h-4 w-4 mt-0.5 ${
                  rec.type === "warning" ? "text-warning" : rec.type === "success" ? "text-profit" : "text-primary"
                }`}
              />
              <p className="text-sm text-muted-foreground leading-relaxed">{rec.message}</p>
            </div>
          ))}
        </div>

        <div className="p-4 bg-primary/5 border border-primary/20 rounded-lg">
          <div className="flex items-center gap-2 mb-2">
            <TrendingUp className="h-4 w-4 text-primary" />
            <span className="text-sm font-medium">Overall Risk Score</span>
          </div>
          <div className="text-2xl font-bold text-primary">Medium</div>
          <p className="text-xs text-muted-foreground mt-1">Your portfolio has balanced risk exposure</p>
        </div>
      </CardContent>
    </Card>
  )
}
