
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { TrendingUp, TrendingDown } from "lucide-react"

const holdings = [
  { stock: "AAPL", name: "Apple Inc.", shares: 50, avgPrice: 145.3, currentPrice: 151.89, value: 7594.5 },
  { stock: "TSLA", name: "Tesla Inc.", shares: 25, avgPrice: 235.2, currentPrice: 245.8, value: 6145.0 },
  { stock: "GOOGL", name: "Alphabet Inc.", shares: 40, avgPrice: 135.8, currentPrice: 142.5, value: 5700.0 },
  { stock: "MSFT", name: "Microsoft Corp.", shares: 30, avgPrice: 380.5, currentPrice: 395.2, value: 11856.0 },
  { stock: "NVDA", name: "NVIDIA Corp.", shares: 15, avgPrice: 450.0, currentPrice: 489.3, value: 7339.5 },
]

export function PortfolioSummary() {
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
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Avg</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Current</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">P/L</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Value</th>
              </tr>
            </thead>
            <tbody>
              {holdings.map((holding, index) => {
                const profitLoss = (holding.currentPrice - holding.avgPrice) * holding.shares
                const profitLossPercent = (
                  ((holding.currentPrice - holding.avgPrice) / holding.avgPrice) *
                  100
                ).toFixed(2)
                const isProfit = profitLoss >= 0

                return (
                  <tr key={index} className="border-b border-border/50 hover:bg-secondary/30 transition-colors">
                    <td className="py-3 px-4">
                      <div>
                        <div className="font-bold text-sm">{holding.stock}</div>
                        <div className="text-xs text-muted-foreground truncate max-w-[100px]">{holding.name}</div>
                      </div>
                    </td>
                    <td className="text-right py-3 px-4 text-sm">{holding.shares}</td>
                    <td className="text-right py-3 px-4 text-sm">${holding.avgPrice.toFixed(0)}</td>
                    <td className="text-right py-3 px-4 text-sm font-medium">${holding.currentPrice.toFixed(0)}</td>
                    <td className="text-right py-3 px-4">
                      <div className="flex flex-col items-end">
                        <span
                          className={`text-sm font-medium flex items-center gap-1 ${isProfit ? "text-green-500" : "text-red-500"}`}
                        >
                          {isProfit ? <TrendingUp className="h-3 w-3" /> : <TrendingDown className="h-3 w-3" />}
                          {isProfit ? "+" : ""}
                          {profitLossPercent}%
                        </span>
                      </div>
                    </td>
                    <td className="text-right py-3 px-4 text-sm font-bold">${holding.value.toLocaleString()}</td>
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
