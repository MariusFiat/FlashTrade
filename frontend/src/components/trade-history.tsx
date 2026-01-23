
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Calendar, Download, Search, Loader2 } from "lucide-react"
import { useState, useEffect } from "react"
import { stockApi } from "@/services/stockApi"
import { useAuth } from "@/hooks/useAuth"
import { useToast } from "@/hooks/use-toast"
import type { TradeRecord } from "@/types/stock"

export function TradeHistory() {
  const [searchTerm, setSearchTerm] = useState("")
  const [trades, setTrades] = useState<any[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const { user } = useAuth()
  const { toast } = useToast()

  useEffect(() => {
    const fetchTrades = async () => {
      if (!user) return
      
      try {
        const response = await stockApi.getTradeHistory(user.email)
        // Transform backend data to component format
        const transformedTrades = response.trades.map((trade: TradeRecord, index: number) => ({
          id: trade.tradeId,
          date: new Date(trade.timestamp).toLocaleDateString(),
          time: new Date(trade.timestamp).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }),
          stock: trade.symbol,
          type: trade.side === 'BUY' ? 'Buy' : 'Sell',
          quantity: trade.quantity,
          price: Number(trade.price),
          total: trade.quantity * Number(trade.price),
          status: "Completed",
          profit: null // P/L calculation would need additional data
        }))
        setTrades(transformedTrades)
      } catch (error) {
        toast({
          title: "Error",
          description: "Failed to fetch trade history",
          variant: "destructive",
        })
      } finally {
        setIsLoading(false)
      }
    }
    fetchTrades()
  }, [user])

  const filteredTrades = trades.filter((trade) => trade.stock.toLowerCase().includes(searchTerm.toLowerCase()))

  const totalProfit = trades.reduce((sum, trade) => sum + (trade.profit || 0), 0)

  if (isLoading) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle>Trading History</CardTitle>
        </CardHeader>
        <CardContent className="flex justify-center items-center py-12">
          <Loader2 className="h-8 w-8 animate-spin" />
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle>Trading History</CardTitle>
        <div className="flex gap-2">
          <Button variant="outline" size="sm" className="gap-2 bg-transparent">
            <Calendar className="h-4 w-4" />
            Filter
          </Button>
          <Button variant="outline" size="sm" className="gap-2 bg-transparent">
            <Download className="h-4 w-4" />
            Export
          </Button>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-center justify-between">
          <div className="relative flex-1 max-w-sm">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Search by stock symbol..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-9 bg-secondary border-border"
            />
          </div>
          <div className="text-right">
            <div className="text-sm text-muted-foreground">Total P/L</div>
            <div className={`text-2xl font-bold ${totalProfit >= 0 ? "text-profit" : "text-loss"}`}>
              {totalProfit >= 0 ? "+" : ""}${totalProfit.toFixed(2)}
            </div>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-border">
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Date & Time</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Stock</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Type</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Quantity</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Price</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Total</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">P/L</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Status</th>
              </tr>
            </thead>
            <tbody>
              {filteredTrades.map((trade) => (
                <tr key={trade.id} className="border-b border-border/50 hover:bg-secondary/30 transition-colors">
                  <td className="py-4 px-4">
                    <div className="text-sm">{trade.date}</div>
                    <div className="text-xs text-muted-foreground">{trade.time}</div>
                  </td>
                  <td className="py-4 px-4">
                    <div className="font-bold">{trade.stock}</div>
                  </td>
                  <td className="py-4 px-4">
                    <Badge variant={trade.type === "Buy" ? "default" : "destructive"}>{trade.type}</Badge>
                  </td>
                  <td className="text-right py-4 px-4">{trade.quantity}</td>
                  <td className="text-right py-4 px-4">${trade.price.toFixed(2)}</td>
                  <td className="text-right py-4 px-4 font-medium">${trade.total.toFixed(2)}</td>
                  <td className="text-right py-4 px-4">
                    {trade.profit !== null ? (
                      <span className={`font-medium ${trade.profit >= 0 ? "text-profit" : "text-loss"}`}>
                        {trade.profit >= 0 ? "+" : ""}${trade.profit.toFixed(2)}
                      </span>
                    ) : (
                      <span className="text-muted-foreground text-sm">-</span>
                    )}
                  </td>
                  <td className="py-4 px-4">
                    <Badge variant="secondary" className="bg-profit/20 text-profit">
                      {trade.status}
                    </Badge>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  )
}
