
import { Card, CardContent } from "@/components/ui/card"
import { TrendingUp, TrendingDown, DollarSign, Percent, Loader2 } from "lucide-react"
import { useState, useEffect } from "react"
import { userApi } from "@/services/userApi"
import type { PortfolioSummary } from "@/types/user"

export function PortfolioOverview() {
  const [portfolio, setPortfolio] = useState<PortfolioSummary | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchPortfolio()
  }, [])

  const fetchPortfolio = async () => {
    try {
      setIsLoading(true)
      setError(null)
      const data = await userApi.getPortfolio()
      setPortfolio(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load portfolio')
    } finally {
      setIsLoading(false)
    }
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    )
  }

  if (error || !portfolio) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        {error || 'No portfolio data available'}
      </div>
    )
  }

  const totalInvested = portfolio.totalInvested ?? 0
  const totalReturn = portfolio.totalReturn ?? 0
  const totalValue = portfolio.totalValue ?? 0
  
  const returnPercent = totalInvested > 0 
    ? ((totalReturn / totalInvested) * 100).toFixed(2)
    : '0.00'
  const isPositive = totalReturn >= 0

  const stats = [
    {
      label: "Total Value",
      value: `$${totalValue.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
      change: `$${totalReturn.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
      changePercent: `${isPositive ? '+' : ''}${returnPercent}%`,
      isPositive: isPositive,
      icon: DollarSign,
    },
    {
      label: "Total Invested",
      value: `$${totalInvested.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
      change: "Initial capital",
      changePercent: "",
      isPositive: null,
      icon: DollarSign,
    },
    {
      label: "Total Return",
      value: `${isPositive ? '+' : ''}$${Math.abs(totalReturn).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
      change: "All-time",
      changePercent: `${isPositive ? '+' : ''}${returnPercent}%`,
      isPositive: isPositive,
      icon: TrendingUp,
    },
    {
      label: "Holdings",
      value: `${portfolio.items?.length ?? 0}`,
      change: "Active positions",
      changePercent: "",
      isPositive: null,
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
