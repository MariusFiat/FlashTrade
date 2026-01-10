
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ArrowUpRight, ArrowDownRight, Loader2 } from "lucide-react"
import { useState, useEffect, useMemo } from "react"
import { userApi } from "@/services/userApi"
import type { TransactionHistory } from "@/types/user"
import { getCurrencySymbol } from "@/lib/currency"

interface TransactionHistoryProps {
  currency: string;
}

export function TransactionHistory({ currency }: TransactionHistoryProps) {
  const [transactions, setTransactions] = useState<TransactionHistory[]>([])
  const currencySymbol = useMemo(() => getCurrencySymbol(currency), [currency])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchTransactions()
  }, [])

  const fetchTransactions = async () => {
    try {
      setIsLoading(true)
      setError(null)
      const data = await userApi.getTransactions()
      // Sort by timestamp descending (newest first)
      const sortedData = data.sort((a, b) => 
        new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
      )
      setTransactions(sortedData)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load transactions')
    } finally {
      setIsLoading(false)
    }
  }

  const formatDate = (timestamp: string) => {
    const date = new Date(timestamp)
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
  }

  const formatTime = (timestamp: string) => {
    const date = new Date(timestamp)
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
  }

  if (isLoading) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle>Transaction History</CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center py-12">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </CardContent>
      </Card>
    )
  }

  if (error) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle>Transaction History</CardTitle>
        </CardHeader>
        <CardContent className="text-center py-12 text-muted-foreground">
          {error}
        </CardContent>
      </Card>
    )
  }

  if (transactions.length === 0) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle>Transaction History</CardTitle>
        </CardHeader>
        <CardContent className="text-center py-12 text-muted-foreground">
          No transactions yet
        </CardContent>
      </Card>
    )
  }

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
                <div className={`p-3 rounded-lg ${transaction.actionType === "DEPOSIT" ? "bg-profit/10" : "bg-loss/10"}`}>
                  {transaction.actionType === "DEPOSIT" ? (
                    <ArrowDownRight className="h-5 w-5 text-profit" />
                  ) : (
                    <ArrowUpRight className="h-5 w-5 text-loss" />
                  )}
                </div>
                <div>
                  <div className="font-bold">{transaction.actionType}</div>
                  <p className="text-sm text-muted-foreground">
                    {formatDate(transaction.timestamp)} at {formatTime(transaction.timestamp)}
                  </p>
                </div>
              </div>
              <div className="text-right">
                <div className={`text-lg font-bold ${transaction.actionType === "DEPOSIT" ? "text-profit" : "text-loss"}`}>
                  {transaction.actionType === "DEPOSIT" ? "+" : "-"}{currencySymbol}{transaction.amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </div>
                <Badge 
                  variant="secondary" 
                  className={transaction.status === "COMPLETED" ? "bg-profit/20 text-profit" : "bg-loss/20 text-loss"}
                >
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
