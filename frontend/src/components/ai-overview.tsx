
import { Card, CardContent } from "@/components/ui/card"
import { Brain, TrendingUp, Shield, Zap } from "lucide-react"

export function AIOverview() {
  const stats = [
    {
      label: "Prediction Accuracy",
      value: "87.3%",
      change: "+2.1% this month",
      icon: Brain,
      color: "text-primary",
    },
    {
      label: "Profitable Signals",
      value: "42/48",
      change: "87.5% success rate",
      icon: TrendingUp,
      color: "text-profit",
    },
    {
      label: "Risk Score",
      value: "Medium",
      change: "Well-diversified",
      icon: Shield,
      color: "text-warning",
    },
    {
      label: "Active Alerts",
      value: "7",
      change: "3 high priority",
      icon: Zap,
      color: "text-primary",
    },
  ]

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      {stats.map((stat, index) => {
        const Icon = stat.icon
        return (
          <Card key={index} className="bg-card border-border/50">
            <CardContent className="p-6">
              <div className="flex items-start justify-between">
                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">{stat.label}</p>
                  <p className="text-2xl font-bold">{stat.value}</p>
                  <p className="text-xs text-muted-foreground">{stat.change}</p>
                </div>
                <div className="bg-primary/10 p-3 rounded-lg">
                  <Icon className={`h-5 w-5 ${stat.color}`} />
                </div>
              </div>
            </CardContent>
          </Card>
        )
      })}
    </div>
  )
}
