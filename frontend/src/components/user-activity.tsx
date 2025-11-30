
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Activity, TrendingUp, TrendingDown, DollarSign } from "lucide-react"

const activities = [
  {
    user: "john.doe@example.com",
    action: "Buy Order",
    stock: "AAPL",
    amount: "$5,234.50",
    time: "2 min ago",
    type: "buy",
  },
  {
    user: "jane.smith@example.com",
    action: "Sell Order",
    stock: "TSLA",
    amount: "$12,450.00",
    time: "5 min ago",
    type: "sell",
  },
  {
    user: "mike.wilson@example.com",
    action: "Buy Order",
    stock: "GOOGL",
    amount: "$3,890.75",
    time: "8 min ago",
    type: "buy",
  },
  {
    user: "sarah.johnson@example.com",
    action: "Deposit",
    stock: "-",
    amount: "$10,000.00",
    time: "12 min ago",
    type: "deposit",
  },
]

export function UserActivity() {
  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Activity className="h-5 w-5 text-primary" />
          Recent Activity
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        {activities.map((activity, index) => (
          <div key={index} className="flex items-start gap-3 p-3 bg-secondary/30 rounded-lg">
            <div
              className={`p-2 rounded-lg mt-1 ${
                activity.type === "buy" ? "bg-profit/10" : activity.type === "sell" ? "bg-loss/10" : "bg-primary/10"
              }`}
            >
              {activity.type === "buy" ? (
                <TrendingUp className="h-4 w-4 text-profit" />
              ) : activity.type === "sell" ? (
                <TrendingDown className="h-4 w-4 text-loss" />
              ) : (
                <DollarSign className="h-4 w-4 text-primary" />
              )}
            </div>
            <div className="flex-1 min-w-0">
              <div className="flex items-center justify-between gap-2 mb-1">
                <span className="text-sm font-medium truncate">{activity.user}</span>
                <Badge
                  variant={activity.type === "buy" ? "default" : activity.type === "sell" ? "destructive" : "secondary"}
                  className="text-xs shrink-0"
                >
                  {activity.action}
                </Badge>
              </div>
              <div className="flex items-center justify-between text-xs text-muted-foreground">
                <span>{activity.stock !== "-" ? `${activity.stock} • ${activity.amount}` : activity.amount}</span>
                <span>{activity.time}</span>
              </div>
            </div>
          </div>
        ))}
      </CardContent>
    </Card>
  )
}
