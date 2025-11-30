
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ArrowUpRight, ArrowDownRight } from "lucide-react"

const transactions = [
  { id: 1, type: "Deposit", amount: 5000, status: "Completed", date: "2024-11-28", time: "14:32" },
  { id: 2, type: "Withdrawal", amount: 2500, status: "Completed", date: "2024-11-25", time: "09:15" },
  { id: 3, type: "Deposit", amount: 10000, status: "Completed", date: "2024-11-20", time: "16:45" },
  { id: 4, type: "Withdrawal", amount: 1500, status: "Completed", date: "2024-11-18", time: "11:22" },
  { id: 5, type: "Deposit", amount: 7500, status: "Completed", date: "2024-11-15", time: "10:00" },
  { id: 6, type: "Withdrawal", amount: 3200, status: "Completed", date: "2024-11-12", time: "13:30" },
]

export function TransactionHistory() {
  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle>Transaction History</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {transactions.map((transaction) => (
            <div key={transaction.id} className="flex items-center justify-between p-4 bg-secondary/50 rounded-lg">
              <div className="flex items-center gap-4">
                <div className={`p-3 rounded-lg ${transaction.type === "Deposit" ? "bg-profit/10" : "bg-loss/10"}`}>
                  {transaction.type === "Deposit" ? (
                    <ArrowDownRight className="h-5 w-5 text-profit" />
                  ) : (
                    <ArrowUpRight className="h-5 w-5 text-loss" />
                  )}
                </div>
                <div>
                  <div className="font-bold">{transaction.type}</div>
                  <p className="text-sm text-muted-foreground">
                    {transaction.date} at {transaction.time}
                  </p>
                </div>
              </div>
              <div className="text-right">
                <div className={`text-lg font-bold ${transaction.type === "Deposit" ? "text-profit" : "text-loss"}`}>
                  {transaction.type === "Deposit" ? "+" : "-"}${transaction.amount.toLocaleString()}
                </div>
                <Badge variant="secondary" className="mt-1">
                  {transaction.status}
                </Badge>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}
