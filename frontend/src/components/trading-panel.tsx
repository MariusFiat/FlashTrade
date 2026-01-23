
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { useState, useEffect } from "react"
import { orderApi } from "@/services/orderApi"
import { stockApi } from "@/services/stockApi"
import { userApi } from "@/services/userApi"
import { useAuth } from "@/hooks/useAuth"
import { useToast } from "@/hooks/use-toast"
import { Loader2 } from "lucide-react"

export function TradingPanel() {
  const [orderType, setOrderType] = useState<"buy" | "sell">("buy")
  const [quantity, setQuantity] = useState("")
  const [price, setPrice] = useState("0.00")
  const [availableBalance, setAvailableBalance] = useState(0)
  const [availableShares, setAvailableShares] = useState(0)
  const [isLoading, setIsLoading] = useState(false)
  const [isFetchingData, setIsFetchingData] = useState(true)
  const [symbol] = useState("AAPL") // This should be dynamic based on selected stock
  const { user } = useAuth()
  const { toast } = useToast()

  useEffect(() => {
    fetchTradingData()
  }, [symbol])

  const fetchTradingData = async () => {
    try {
      setIsFetchingData(true)
      const [stockData, walletData, portfolioData] = await Promise.all([
        stockApi.getStockPerformance(symbol, '1d'),
        userApi.getWalletInfo(),
        userApi.getPortfolio()
      ])
      
      setPrice(stockData.currentPrice.toFixed(2))
      setAvailableBalance(walletData.balance)
      
      // Find the available shares for this symbol
      const holding = portfolioData.items.find(item => item.stock === symbol)
      setAvailableShares(holding ? holding.shares : 0)
    } catch (error) {
      console.error('Failed to fetch trading data:', error)
      toast({
        title: "Error",
        description: "Failed to load trading data",
        variant: "destructive",
      })
    } finally {
      setIsFetchingData(false)
    }
  }

  const handleTrade = async () => {
    if (!user) {
      toast({
        title: "Error",
        description: "You must be logged in to place an order",
        variant: "destructive",
      })
      return
    }

    if (!quantity || Number.parseFloat(quantity) <= 0) {
      toast({
        title: "Error",
        description: "Please enter a valid quantity",
        variant: "destructive",
      })
      return
    }

    setIsLoading(true)
    try {
      await orderApi.placeOrder({
        userId: user.email,
        symbol: symbol,
        side: orderType === "buy" ? "BUY" : "SELL",
        quantity: Number.parseFloat(quantity),
        price: Number.parseFloat(price),
      })

      toast({
        title: "Order Placed",
        description: `${orderType.toUpperCase()} order for ${quantity} ${symbol} shares sent to trading service`,
      })

      // Reset form
      setQuantity("")
      // Refresh trading data
      fetchTradingData()
    } catch (error) {
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to place order",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  if (isFetchingData) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle>Quick Trade</CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center py-12">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle>Quick Trade</CardTitle>
      </CardHeader>
      <CardContent>
        <Tabs defaultValue="buy" className="w-full" onValueChange={(v) => setOrderType(v as "buy" | "sell")}>
          <TabsList className="grid w-full grid-cols-2 mb-4">
            <TabsTrigger
              value="buy"
              className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground"
            >
              Buy
            </TabsTrigger>
            <TabsTrigger
              value="sell"
              className="data-[state=active]:bg-destructive data-[state=active]:text-destructive-foreground"
            >
              Sell
            </TabsTrigger>
          </TabsList>
          <TabsContent value="buy" className="space-y-4 mt-0">
            <div className="space-y-2">
              <Label htmlFor="buy-quantity">Quantity</Label>
              <Input
                id="buy-quantity"
                type="number"
                placeholder="0"
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
                className="bg-secondary border-border"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="buy-price">Price per share</Label>
              <Input
                id="buy-price"
                type="number"
                placeholder="0.00"
                value={price}
                disabled
                className="bg-secondary border-border opacity-75 cursor-not-allowed"
              />
            </div>
            <div className="bg-secondary/50 p-3 rounded-lg space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Total Cost</span>
                <span className="font-medium">
                  ${(Number.parseFloat(quantity || "0") * Number.parseFloat(price || "0")).toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Available Balance</span>
                <span className="font-medium">${availableBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </div>
            </div>
            <Button className="w-full bg-primary hover:bg-green-700 text-white" onClick={handleTrade} disabled={isLoading}>
              {isLoading ? "Placing Order..." : `Buy ${symbol}`}
            </Button>
          </TabsContent>
          <TabsContent value="sell" className="space-y-4 mt-0">
            <div className="space-y-2">
              <Label htmlFor="sell-quantity">Quantity</Label>
              <Input
                id="sell-quantity"
                type="number"
                placeholder="0"
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
                className="bg-secondary border-border"
              />
              <p className="text-xs text-muted-foreground">Available: {availableShares.toFixed(2)} shares</p>
            </div>
            <div className="space-y-2">
              <Label htmlFor="sell-price">Price per share</Label>
              <Input
                id="sell-price"
                type="number"
                placeholder="0.00"
                value={price}
                disabled
                className="bg-secondary border-border opacity-75 cursor-not-allowed"
              />
            </div>
            <div className="bg-secondary/50 p-3 rounded-lg space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Total Value</span>
                <span className="font-medium">
                  ${(Number.parseFloat(quantity || "0") * Number.parseFloat(price || "0")).toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Available Balance</span>
                <span className="font-medium">${availableBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </div>
            </div>
            <Button
              className="w-full bg-primary hover:bg-red-700 text-white"
              onClick={handleTrade}
              disabled={isLoading}
            >
              {isLoading ? "Placing Order..." : `Sell ${symbol}`}
            </Button>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}
