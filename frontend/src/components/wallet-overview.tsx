
import { Card, CardContent } from "@/components/ui/card"
import { DollarSign, ArrowUpRight, ArrowDownRight, Clock } from "lucide-react"

export function WalletOverview() {
  const stats = [
    {
      label: "Available Balance",
      value: "$25,430.00",
      change: "Ready to trade",
      icon: DollarSign,
    },
    {
      label: "Total Deposits",
      value: "$150,000.00",
      change: "All-time",
      icon: ArrowDownRight,
    },
    {
      label: "Total Withdrawals",
      value: "$33,670.23",
      change: "All-time",
      icon: ArrowUpRight,
    },
    {
      label: "Pending",
      value: "$0.00",
      change: "No pending transactions",
      icon: Clock,
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
