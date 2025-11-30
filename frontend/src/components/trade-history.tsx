
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Calendar, Download, Search } from "lucide-react"
import { useState } from "react"

const trades = [
  {
    id: 1,
    date: "2024-11-28",
    time: "14:32",
    stock: "AAPL",
    type: "Buy",
    quantity: 10,
    price: 150.25,
    total: 1502.5,
    status: "Completed",
    profit: null,
  },
  {
    id: 2,
    date: "2024-11-28",
    time: "11:15",
    stock: "TSLA",
    type: "Sell",
    quantity: 5,
    price: 245.8,
    total: 1229.0,
    status: "Completed",
    profit: 234.5,
  },
  {
    id: 3,
    date: "2024-11-27",
    time: "16:45",
    stock: "GOOGL",
    type: "Buy",
    quantity: 8,
    price: 142.5,
    total: 1140.0,
    status: "Completed",
    profit: null,
  },
  {
    id: 4,
    date: "2024-11-27",
    time: "09:22",
    stock: "MSFT",
    type: "Buy",
    quantity: 6,
    price: 388.75,
    total: 2332.5,
    status: "Completed",
    profit: null,
  },
  {
    id: 5,
    date: "2024-11-26",
    time: "13:30",
    stock: "NVDA",
    type: "Sell",
    quantity: 3,
    price: 485.2,
    total: 1455.6,
    status: "Completed",
    profit: 156.9,
  },
  {
    id: 6,
    date: "2024-11-26",
    time: "10:15",
    stock: "AMZN",
    type: "Buy",
    quantity: 10,
    price: 165.8,
    total: 1658.0,
    status: "Completed",
    profit: null,
  },
  {
    id: 7,
    date: "2024-11-25",
    time: "15:20",
    stock: "META",
    type: "Buy",
    quantity: 12,
    price: 325.5,
    total: 3906.0,
    status: "Completed",
    profit: null,
  },
  {
    id: 8,
    date: "2024-11-25",
    time: "11:05",
    stock: "AAPL",
    type: "Sell",
    quantity: 15,
    price: 148.9,
    total: 2233.5,
    status: "Completed",
    profit: -89.25,
  },
]

export function TradeHistory() {
  const [searchTerm, setSearchTerm] = useState("")

  const filteredTrades = trades.filter((trade) => trade.stock.toLowerCase().includes(searchTerm.toLowerCase()))

  const totalProfit = trades.reduce((sum, trade) => sum + (trade.profit || 0), 0)

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
