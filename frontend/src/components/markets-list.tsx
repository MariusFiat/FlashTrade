import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Search, Star, TrendingUp, TrendingDown, Loader2 } from "lucide-react"
import { useState, useEffect } from "react"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { stockApi } from "@/services/stockApi"
import { orderApi } from "@/services/orderApi"
import { userApi } from "@/services/userApi"
import { useAuth } from "@/hooks/useAuth"
import { useToast } from "@/hooks/use-toast"
import type { Stock } from "@/types/stock"

export function MarketsList() {
  const [searchTerm, setSearchTerm] = useState("")
  const [watchlist, setWatchlist] = useState<string[]>(["AAPL", "TSLA", "NVDA"])
  const [allStocks, setAllStocks] = useState<any[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [tradeDialogOpen, setTradeDialogOpen] = useState(false)
  const [selectedStock, setSelectedStock] = useState<any | null>(null)
  const [buyQuantity, setBuyQuantity] = useState("")
  const [sellQuantity, setSellQuantity] = useState("")
  const [availableBalance, setAvailableBalance] = useState(0)
  const [availableShares, setAvailableShares] = useState(0)
  const [isPlacingOrder, setIsPlacingOrder] = useState(false)
  const [tradeType, setTradeType] = useState<"buy" | "sell">("buy")
  const { user } = useAuth()
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

  const handleOpenTradeDialog = async (stock: any) => {
    setSelectedStock(stock)
    setBuyQuantity("")
    setSellQuantity("")
    setTradeType("buy")
    
    // Fetch wallet balance and portfolio holdings
    if (user) {
      try {
        const walletData = await userApi.getWalletInfo()
        setAvailableBalance(walletData.balance)
        
        // Fetch portfolio to get available shares
        const portfolioData = await userApi.getPortfolio()
        const holding = portfolioData.items.find((h: any) => h.stock === stock.symbol)
        setAvailableShares(holding?.shares || 0)
      } catch (err) {
        toast({
          title: "Warning",
          description: "Could not fetch account information",
          variant: "destructive",
        })
        setAvailableBalance(0)
        setAvailableShares(0)
      }
    }
    
    setTradeDialogOpen(true)
  }

  const handlePlaceOrder = async () => {
    if (!user || !selectedStock) return

    const qty = Number.parseFloat(tradeType === "buy" ? buyQuantity : sellQuantity)
    if (!qty || qty <= 0) {
      toast({
        title: "Error",
        description: "Please enter a valid quantity",
        variant: "destructive",
      })
      return
    }

    if (tradeType === "buy") {
      const totalCost = qty * selectedStock.price
      if (totalCost > availableBalance) {
        toast({
          title: "Insufficient Funds",
          description: `You need $${totalCost.toFixed(2)} but only have $${availableBalance.toFixed(2)}`,
          variant: "destructive",
        })
        return
      }
    } else {
      if (qty > availableShares) {
        toast({
          title: "Insufficient Shares",
          description: `You only have ${availableShares} shares available`,
          variant: "destructive",
        })
        return
      }
    }

    setIsPlacingOrder(true)
    try {
      await orderApi.placeOrder({
        userId: user.email,
        symbol: selectedStock.symbol,
        side: tradeType.toUpperCase() as 'BUY' | 'SELL',
        quantity: qty,
        price: selectedStock.price,
      })

      toast({
        title: "Order Placed",
        description: `${tradeType.toUpperCase()} order for ${qty} ${selectedStock.symbol} shares sent to trading service`,
      })

      setTradeDialogOpen(false)
      setBuyQuantity("")
      setSellQuantity("")
    } catch (error) {
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to place order",
        variant: "destructive",
      })
    } finally {
      setIsPlacingOrder(false)
    }
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
            <StockTable stocks={filteredStocks} watchlist={watchlist} onToggleWatchlist={toggleWatchlist} onTrade={handleOpenTradeDialog} />
          </TabsContent>

          <TabsContent value="watchlist" className="mt-0">
            <StockTable stocks={watchlistStocks} watchlist={watchlist} onToggleWatchlist={toggleWatchlist} onTrade={handleOpenTradeDialog} />
          </TabsContent>

          <TabsContent value="gainers" className="mt-0">
            <StockTable stocks={gainers} watchlist={watchlist} onToggleWatchlist={toggleWatchlist} onTrade={handleOpenTradeDialog} />
          </TabsContent>

          <TabsContent value="losers" className="mt-0">
            <StockTable stocks={losers} watchlist={watchlist} onToggleWatchlist={toggleWatchlist} onTrade={handleOpenTradeDialog} />
          </TabsContent>
        </Tabs>

        <Dialog open={tradeDialogOpen} onOpenChange={setTradeDialogOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Trade {selectedStock?.symbol}</DialogTitle>
              <DialogDescription>
                Current price: ${selectedStock?.price.toFixed(2)} per share
              </DialogDescription>
            </DialogHeader>
            <Tabs value={tradeType} onValueChange={(value) => setTradeType(value as "buy" | "sell")} className="w-full">
              <TabsList className="grid w-full grid-cols-2">
                <TabsTrigger value="buy">Buy</TabsTrigger>
                <TabsTrigger value="sell">Sell</TabsTrigger>
              </TabsList>

              <TabsContent value="buy" className="space-y-4 py-4">
                <div className="space-y-2">
                  <Label htmlFor="buy-quantity">Quantity</Label>
                  <Input
                    id="buy-quantity"
                    type="number"
                    placeholder="0"
                    value={buyQuantity}
                    onChange={(e) => setBuyQuantity(e.target.value)}
                    min="0"
                    step="1"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="buy-price">Price per Share</Label>
                  <Input
                    id="buy-price"
                    type="text"
                    value={`$${selectedStock?.price.toFixed(2)}`}
                    disabled
                    className="opacity-75"
                  />
                </div>
                <div className="bg-secondary/50 p-3 rounded-lg space-y-2">
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Total Cost</span>
                    <span className="font-medium">
                      ${((Number.parseFloat(buyQuantity || "0")) * (selectedStock?.price || 0)).toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Available Balance</span>
                    <span className="font-medium">${availableBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
                  </div>
                </div>
              </TabsContent>

              <TabsContent value="sell" className="space-y-4 py-4">
                <div className="space-y-2">
                  <Label htmlFor="sell-quantity">Quantity</Label>
                  <Input
                    id="sell-quantity"
                    type="number"
                    placeholder="0"
                    value={sellQuantity}
                    onChange={(e) => setSellQuantity(e.target.value)}
                    min="0"
                    step="1"
                    max={availableShares}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="sell-price">Price per Share</Label>
                  <Input
                    id="sell-price"
                    type="text"
                    value={`$${selectedStock?.price.toFixed(2)}`}
                    disabled
                    className="opacity-75"
                  />
                </div>
                <div className="bg-secondary/50 p-3 rounded-lg space-y-2">
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Total Proceeds</span>
                    <span className="font-medium text-profit">
                      ${((Number.parseFloat(sellQuantity || "0")) * (selectedStock?.price || 0)).toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Available Shares</span>
                    <span className="font-medium">{availableShares}</span>
                  </div>
                </div>
              </TabsContent>
            </Tabs>
            <DialogFooter>
              <Button variant="outline" onClick={() => setTradeDialogOpen(false)} disabled={isPlacingOrder}>
                Cancel
              </Button>
              <Button 
                onClick={handlePlaceOrder} 
                disabled={isPlacingOrder}
                className={tradeType === "buy" ? "bg-primary hover:bg-primary/90" : "bg-destructive hover:bg-destructive/90"}
              >
                {isPlacingOrder ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Placing Order...
                  </>
                ) : (
                  tradeType === "buy" ? "Buy" : "Sell"
                )}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </CardContent>
    </Card>
  )
}

function StockTable({
  stocks,
  watchlist,
  onToggleWatchlist,
  onTrade,
}: {
  stocks: any[]
  watchlist: string[]
  onToggleWatchlist: (symbol: string) => void
  onTrade: (stock: any) => void
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
                    <Button 
                      variant="default" 
                      size="sm" 
                      className="bg-primary hover:bg-primary/90 text-primary-foreground"
                      onClick={() => onTrade(stock)}
                    >
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
