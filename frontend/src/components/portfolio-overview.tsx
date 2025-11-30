
import { Card, CardContent } from "@/components/ui/card"
import { TrendingUp, TrendingDown, DollarSign, Percent } from "lucide-react"

export function PortfolioOverview() {
  const stats = [
    {
      label: "Total Value",
      value: "$124,563.89",
      change: "+$8,234.12",
      changePercent: "+7.08%",
      isPositive: true,
      icon: DollarSign,
    },
    {
      label: "Total Invested",
      value: "$116,329.77",
      change: "Initial capital",
      changePercent: "",
      isPositive: null,
      icon: DollarSign,
    },
    {
      label: "Total Return",
      value: "+$8,234.12",
      change: "All-time",
      changePercent: "+7.08%",
      isPositive: true,
      icon: TrendingUp,
    },
    {
      label: "Today's Change",
      value: "+$1,523.45",
      change: "Last 24h",
      changePercent: "+1.24%",
      isPositive: true,
      icon: Percent,
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
                <div className="space-y-1 flex-1">
                  <p className="text-sm text-muted-foreground">{stat.label}</p>
                  <p className="text-2xl font-bold">{stat.value}</p>
                  <div className="flex items-center gap-2">
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
                    {stat.changePercent && (
                      <p
                        className={`text-xs font-medium flex items-center gap-1 ${
                          stat.isPositive ? "text-profit" : "text-loss"
                        }`}
                      >
                        {stat.isPositive ? <TrendingUp className="h-3 w-3" /> : <TrendingDown className="h-3 w-3" />}
                        {stat.changePercent}
                      </p>
                    )}
                  </div>
                </div>
                <div className="bg-primary/10 p-3 rounded-lg">
                  <Icon className="h-5 w-5 text-primary" />
                </div>
              </div>
            </CardContent>
          </Card>
        )
      })}
    </div>
  )
}
