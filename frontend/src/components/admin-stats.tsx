
import { Card, CardContent } from "@/components/ui/card"
import { Users, Activity, DollarSign, TrendingUp } from "lucide-react"

export function AdminStats() {
  const stats = [
    {
      label: "Total Users",
      value: "12,458",
      change: "+234 this month",
      icon: Users,
    },
    {
      label: "Active Stocks",
      value: "2,847",
      change: "+45 this week",
      icon: Activity,
    },
    {
      label: "Trading Volume",
      value: "$45.2M",
      change: "+12.5% today",
      icon: DollarSign,
    },
    {
      label: "Total Trades",
      value: "156,789",
      change: "+3,456 today",
      icon: TrendingUp,
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
