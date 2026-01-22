
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Search, Star, TrendingUp, TrendingDown, Loader2 } from "lucide-react"
import { useState, useEffect } from "react"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { stockApi } from "@/services/stockApi"
import { useToast } from "@/hooks/use-toast"
import type { Stock } from "@/types/stock"

export function MarketsList() {
  const [searchTerm, setSearchTerm] = useState("")
  const [watchlist, setWatchlist] = useState<string[]>(["AAPL", "TSLA", "NVDA"])
  const [allStocks, setAllStocks] = useState<any[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const { toast } = useToast()

  useEffect(() => {
    const fetchStocks = async () => {
      try {
        const response = await stockApi.getAllStocks()
        // Transform backend Stock data to match component format
        const transformedStocks = response.stocks.map((stock: Stock) => ({
          symbol: stock.symbol,
          name: stock.name,
          price: stock.price,
          change: stock.change,
          changePercent: (stock.change / (stock.price - stock.change)) * 100,
          volume: "N/A",
          marketCap: "N/A",
          sector: "Technology"
        }))
        setAllStocks(transformedStocks)
      } catch (error) {
        toast({
          title: "Error",
          description: "Failed to fetch market data",
          variant: "destructive",
        })
        // Keep empty array or use fallback data
      } finally {
        setIsLoading(false)
      }
    }
    fetchStocks()
  }, [])

  if (isLoading) {
    return (
      <Card className="bg-card border-border/50">
        <CardContent className="flex justify-center items-center py-12">
          <Loader2 className="h-8 w-8 animate-spin" />
        </CardContent>
      </Card>
    )
  }

  const filteredStocks = allStocks.filter(
    (stock) =>
      stock.symbol.toLowerCase().includes(searchTerm.toLowerCase()) ||
      stock.name.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  const toggleWatchlist = (symbol: string) => {
    setWatchlist((prev) => (prev.includes(symbol) ? prev.filter((s) => s !== symbol) : [...prev, symbol]))
  }

  const gainers = allStocks
    .filter((s) => s.change > 0)
    .sort((a, b) => b.changePercent - a.changePercent)
    .slice(0, 5)
  const losers = allStocks
    .filter((s) => s.change < 0)
    .sort((a, b) => a.changePercent - b.changePercent)
    .slice(0, 5)
  const watchlistStocks = allStocks.filter((s) => watchlist.includes(s.symbol))

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Search stocks..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-9 bg-secondary border-border"
          />
        </div>
      </CardHeader>
      <CardContent>
        <Tabs defaultValue="all" className="w-full">
          <TabsList className="grid w-full grid-cols-4 mb-6">
            <TabsTrigger value="all">All Stocks</TabsTrigger>
            <TabsTrigger value="watchlist">Watchlist ({watchlistStocks.length})</TabsTrigger>
            <TabsTrigger value="gainers">Top Gainers</TabsTrigger>
            <TabsTrigger value="losers">Top Losers</TabsTrigger>
          </TabsList>

          <TabsContent value="all" className="mt-0">
            <StockTable stocks={filteredStocks} watchlist={watchlist} onToggleWatchlist={toggleWatchlist} />
          </TabsContent>

          <TabsContent value="watchlist" className="mt-0">
            <StockTable stocks={watchlistStocks} watchlist={watchlist} onToggleWatchlist={toggleWatchlist} />
          </TabsContent>

          <TabsContent value="gainers" className="mt-0">
            <StockTable stocks={gainers} watchlist={watchlist} onToggleWatchlist={toggleWatchlist} />
          </TabsContent>

          <TabsContent value="losers" className="mt-0">
            <StockTable stocks={losers} watchlist={watchlist} onToggleWatchlist={toggleWatchlist} />
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}

function StockTable({
  stocks,
  watchlist,
  onToggleWatchlist,
}: {
  stocks: any[]
  watchlist: string[]
  onToggleWatchlist: (symbol: string) => void
}) {
  return (
    <div className="overflow-x-auto">
      <table className="w-full">
        <thead>
          <tr className="border-b border-border">
            <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Symbol</th>
            <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Name</th>
            <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Price</th>
            <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Change</th>
            <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Volume</th>
            <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Market Cap</th>
            <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Sector</th>
            <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Actions</th>
          </tr>
        </thead>
        <tbody>
          {stocks.map((stock) => {
            const isPositive = stock.change >= 0
            const isInWatchlist = watchlist.includes(stock.symbol)
            return (
              <tr key={stock.symbol} className="border-b border-border/50 hover:bg-secondary/30 transition-colors">
                <td className="py-4 px-4">
                  <div className="font-bold">{stock.symbol}</div>
                </td>
                <td className="py-4 px-4">
                  <div className="text-sm">{stock.name}</div>
                </td>
                <td className="text-right py-4 px-4 font-bold">${stock.price.toFixed(2)}</td>
                <td className="text-right py-4 px-4">
                  <div className={`flex flex-col items-end ${isPositive ? "text-profit" : "text-loss"}`}>
                    <span className="font-medium flex items-center gap-1">
                      {isPositive ? <TrendingUp className="h-3 w-3" /> : <TrendingDown className="h-3 w-3" />}
                      {isPositive ? "+" : ""}
                      {stock.change.toFixed(2)}
                    </span>
                    <span className="text-xs">
                      {isPositive ? "+" : ""}
                      {stock.changePercent.toFixed(2)}%
                    </span>
                  </div>
                </td>
                <td className="text-right py-4 px-4 text-sm text-muted-foreground">{stock.volume}</td>
                <td className="text-right py-4 px-4 text-sm text-muted-foreground">{stock.marketCap}</td>
                <td className="py-4 px-4">
                  <Badge variant="outline" className="text-xs">
                    {stock.sector}
                  </Badge>
                </td>
                <td className="text-right py-4 px-4">
                  <div className="flex items-center justify-end gap-2">
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-8 w-8"
                      onClick={() => onToggleWatchlist(stock.symbol)}
                    >
                      <Star className={`h-4 w-4 ${isInWatchlist ? "fill-primary text-primary" : ""}`} />
                    </Button>
                    <Button variant="default" size="sm" className="bg-primary hover:bg-primary/90 text-primary-foreground">
                      Trade
                    </Button>
                  </div>
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
