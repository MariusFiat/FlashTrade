
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { TrendingUp, TrendingDown, MoreHorizontal, Loader2 } from "lucide-react"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { useState, useEffect } from "react"
import { userApi } from "@/services/userApi"
import type { PortfolioSummary } from "@/types/user"

export function PortfolioHoldings() {
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
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle>Holdings Details</CardTitle>
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
          <CardTitle>Holdings Details</CardTitle>
        </CardHeader>
        <CardContent className="text-center py-12 text-muted-foreground">
          {error || 'No holdings data available'}
        </CardContent>
      </Card>
    )
  }

  if (portfolio.items.length === 0) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle>Holdings Details</CardTitle>
        </CardHeader>
        <CardContent className="text-center py-12 text-muted-foreground">
          No holdings yet. Start trading to build your portfolio!
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle>Holdings Details</CardTitle>
        <Button variant="outline" size="sm">
          Export
        </Button>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-border">
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Stock</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Shares</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Market Value</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Allocation</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Actions</th>
              </tr>
            </thead>
            <tbody>
              {portfolio.items.map((holding) => {
                return (
                  <tr key={holding.id} className="border-b border-border/50 hover:bg-secondary/30 transition-colors">
                    <td className="py-4 px-4">
                      <div className="font-bold">{holding.stock}</div>
                    </td>
                    <td className="text-right py-4 px-4">{holding.shares.toFixed(2)}</td>
                    <td className="text-right py-4 px-4 font-bold">${holding.portfolioValue.toFixed(2)}</td>
                    <td className="text-right py-4 px-4">
                      <Badge variant="secondary">{holding.allocation.toFixed(2)}%</Badge>
                    </td>
                    <td className="text-right py-4 px-4">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-8 w-8">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem>Buy More</DropdownMenuItem>
                          <DropdownMenuItem>Sell</DropdownMenuItem>
                          <DropdownMenuItem>View Details</DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
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
