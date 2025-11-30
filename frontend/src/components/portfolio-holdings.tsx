
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { TrendingUp, TrendingDown, MoreHorizontal } from "lucide-react"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"

const holdings = [
  { stock: "AAPL", name: "Apple Inc.", shares: 50, avgPrice: 145.3, currentPrice: 151.89, allocation: 18.5 },
  { stock: "TSLA", name: "Tesla Inc.", shares: 25, avgPrice: 235.2, currentPrice: 245.8, allocation: 15.2 },
  { stock: "GOOGL", name: "Alphabet Inc.", shares: 40, avgPrice: 135.8, currentPrice: 142.5, allocation: 14.1 },
  { stock: "MSFT", name: "Microsoft Corp.", shares: 30, avgPrice: 380.5, currentPrice: 395.2, allocation: 29.3 },
  { stock: "NVDA", name: "NVIDIA Corp.", shares: 15, avgPrice: 450.0, currentPrice: 489.3, allocation: 18.1 },
  { stock: "AMZN", name: "Amazon.com Inc.", shares: 10, avgPrice: 165.8, currentPrice: 172.4, allocation: 4.8 },
]

export function PortfolioHoldings() {
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
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Avg Cost</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Current Price</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Total Cost</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Market Value</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">P/L</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Allocation</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Actions</th>
              </tr>
            </thead>
            <tbody>
              {holdings.map((holding, index) => {
                const totalCost = holding.avgPrice * holding.shares
                const marketValue = holding.currentPrice * holding.shares
                const profitLoss = marketValue - totalCost
                const profitLossPercent = ((profitLoss / totalCost) * 100).toFixed(2)
                const isProfit = profitLoss >= 0

                return (
                  <tr key={index} className="border-b border-border/50 hover:bg-secondary/30 transition-colors">
                    <td className="py-4 px-4">
                      <div>
                        <div className="font-bold">{holding.stock}</div>
                        <div className="text-xs text-muted-foreground">{holding.name}</div>
                      </div>
                    </td>
                    <td className="text-right py-4 px-4">{holding.shares}</td>
                    <td className="text-right py-4 px-4">${holding.avgPrice.toFixed(2)}</td>
                    <td className="text-right py-4 px-4 font-medium">${holding.currentPrice.toFixed(2)}</td>
                    <td className="text-right py-4 px-4">${totalCost.toFixed(2)}</td>
                    <td className="text-right py-4 px-4 font-bold">${marketValue.toFixed(2)}</td>
                    <td className="text-right py-4 px-4">
                      <div className="flex flex-col items-end">
                        <span
                          className={`font-medium flex items-center gap-1 ${isProfit ? "text-profit" : "text-loss"}`}
                        >
                          {isProfit ? <TrendingUp className="h-3 w-3" /> : <TrendingDown className="h-3 w-3" />}
                          {isProfit ? "+" : ""}
                          {profitLoss.toFixed(2)}
                        </span>
                        <span className={`text-xs ${isProfit ? "text-profit" : "text-loss"}`}>
                          {isProfit ? "+" : ""}
                          {profitLossPercent}%
                        </span>
                      </div>
                    </td>
                    <td className="text-right py-4 px-4">
                      <Badge variant="secondary">{holding.allocation}%</Badge>
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
