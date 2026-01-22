
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Loader2 } from "lucide-react"
import { useState, useEffect } from "react"
import { userApi } from "@/services/userApi"
import type { PortfolioSummary as PortfolioSummaryType } from "@/types/user"

export function PortfolioSummary() {
  const [portfolio, setPortfolio] = useState<PortfolioSummaryType | null>(null)
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
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle>Portfolio Holdings</CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center py-12">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </CardContent>
      </Card>
    )
  }

  if (error || !portfolio) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle>Portfolio Holdings</CardTitle>
        </CardHeader>
        <CardContent className="text-center py-12 text-muted-foreground">
          {error || 'No portfolio data available'}
        </CardContent>
      </Card>
    )
  }

  if (portfolio.items.length === 0) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle>Portfolio Holdings</CardTitle>
        </CardHeader>
        <CardContent className="text-center py-12 text-muted-foreground">
          No holdings yet. Start trading to build your portfolio!
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle>Portfolio Holdings</CardTitle>
      </CardHeader>
      <CardContent className="p-0">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[600px]">
            <thead>
              <tr className="border-b border-border">
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Stock</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Shares</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Value</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Allocation</th>
              </tr>
            </thead>
            <tbody>
              {portfolio.items.map((holding) => {
                return (
                  <tr key={holding.id} className="border-b border-border/50 hover:bg-secondary/30 transition-colors">
                    <td className="py-3 px-4">
                      <div className="font-bold text-sm">{holding.stock || 'N/A'}</div>
                    </td>
                    <td className="text-right py-3 px-4 text-sm">{holding.shares != null ? holding.shares.toFixed(2) : '0.00'}</td>
                    <td className="text-right py-3 px-4 text-sm font-bold">${holding.portfolioValue != null ? holding.portfolioValue.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) : '0.00'}</td>
                    <td className="text-right py-3 px-4 text-sm">
                      <span className="text-muted-foreground">{holding.allocation != null ? holding.allocation.toFixed(1) : '0.0'}%</span>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  )
}
