
import { Card, CardContent } from "@/components/ui/card"
import { TrendingUp, Wallet, Activity } from "lucide-react"

export function MarketOverview() {
  const stats = [
    {
      label: "Portfolio Value",
      value: "$124,563.89",
      change: "+12.5%",
      isPositive: true,
      icon: Wallet,
    },
    {
      label: "Total Profit/Loss",
      value: "+$8,234.12",
      change: "+6.8%",
      isPositive: true,
      icon: TrendingUp,
    },
    {
      label: "Active Positions",
      value: "12",
      change: "3 pending",
      isPositive: null,
      icon: Activity,
    },
    {
      label: "Available Balance",
      value: "$25,430.00",
      change: "Ready to trade",
      isPositive: null,
      icon: Wallet,
    },
  ]

  return (
    <Card className="bg-card border-border/50">
      <CardContent className="p-4">
        <div className="flex items-center justify-between gap-6">
          {stats.map((stat, index) => {
            const Icon = stat.icon
            return (
              <div key={index} className="flex items-center gap-3">
                <div className="bg-primary/10 p-2 rounded-lg">
                  <Icon className="h-4 w-4 text-primary" />
                </div>
                <div>
                  <p className="text-xs text-muted-foreground">{stat.label}</p>
                  <div className="flex items-baseline gap-2">
                    <p className="text-lg font-bold">{stat.value}</p>
                    <p
                      className={`text-xs ${
                        stat.isPositive === true
                          ? "text-profit"
                          : stat.isPositive === false
                            ? "text-loss"
                            : "text-muted-foreground"
                      }`}
                    >
                      {stat.change}
                    </p>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </CardContent>
    </Card>
  )
}
