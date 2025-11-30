
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { TrendingUp, TrendingDown, Award } from "lucide-react"

export function PerformanceMetrics() {
  const metrics = [
    {
      label: "Best Performer",
      stock: "NVDA",
      value: "+8.73%",
      isPositive: true,
    },
    {
      label: "Worst Performer",
      stock: "AMZN",
      value: "-2.15%",
      isPositive: false,
    },
    {
      label: "Win Rate",
      value: "73.5%",
      subtitle: "48 of 65 trades",
    },
    {
      label: "Largest Gain",
      value: "+$1,245.80",
      subtitle: "TSLA on Oct 15",
    },
  ]

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Award className="h-5 w-5 text-primary" />
          Performance Metrics
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {metrics.map((metric, index) => (
          <div key={index} className="p-4 bg-secondary/30 rounded-lg border border-border/50">
            <div className="text-sm text-muted-foreground mb-1">{metric.label}</div>
            <div className="flex items-center justify-between">
              <div>
                {metric.stock && <div className="font-bold text-lg">{metric.stock}</div>}
                <div
                  className={`text-xl font-bold ${
                    metric.isPositive === true
                      ? "text-profit"
                      : metric.isPositive === false
                        ? "text-loss"
                        : "text-foreground"
                  }`}
                >
                  {metric.value}
                </div>
                {metric.subtitle && <div className="text-xs text-muted-foreground mt-1">{metric.subtitle}</div>}
              </div>
              {metric.isPositive !== undefined &&
                (metric.isPositive ? (
                  <TrendingUp className="h-6 w-6 text-profit" />
                ) : (
                  <TrendingDown className="h-6 w-6 text-loss" />
                ))}
            </div>
          </div>
        ))}
      </CardContent>
    </Card>
  )
}
