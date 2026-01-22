
import { Card, CardContent } from "@/components/ui/card"
import { TrendingUp, Wallet, Activity, Loader2 } from "lucide-react"
import { useState, useEffect } from "react"
import { userApi } from "@/services/userApi"
import type { WalletInfo, PortfolioSummary } from "@/types/user"

export function MarketOverview() {
  const [walletInfo, setWalletInfo] = useState<WalletInfo | null>(null)
  const [portfolio, setPortfolio] = useState<PortfolioSummary | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      setIsLoading(true)
      const [walletData, portfolioData] = await Promise.all([
        userApi.getWalletInfo(),
        userApi.getPortfolio()
      ])
      setWalletInfo(walletData)
      setPortfolio(portfolioData)
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error)
    } finally {
      setIsLoading(false)
    }
  }

  if (isLoading) {
    return (
      <Card className="bg-card border-border/50">
        <CardContent className="p-4 flex items-center justify-center h-24">
          <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
        </CardContent>
      </Card>
    )
  }

  if (!walletInfo || !portfolio) {
    return null
  }

  const returnPercent = portfolio.totalInvested > 0 
    ? ((portfolio.totalReturn / portfolio.totalInvested) * 100).toFixed(1)
    : '0.0'
  const isPositive = portfolio.totalReturn >= 0

  const stats = [
    {
      label: "Portfolio Value",
      value: `$${portfolio.totalValue.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
      change: `${isPositive ? '+' : ''}${returnPercent}%`,
      isPositive: isPositive,
      icon: Wallet,
    },
    {
      label: "Total Profit/Loss",
      value: `${portfolio.totalReturn >= 0 ? '+' : ''}$${Math.abs(portfolio.totalReturn).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
      change: `${isPositive ? '+' : ''}${returnPercent}%`,
      isPositive: isPositive,
      icon: TrendingUp,
    },
    {
      label: "Active Positions",
      value: `${portfolio.items.length}`,
      change: "Holdings",
      isPositive: null,
      icon: Activity,
    },
    {
      label: "Available Balance",
      value: `$${walletInfo.balance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
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
