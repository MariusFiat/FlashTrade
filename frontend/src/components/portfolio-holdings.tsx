
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { MoreHorizontal, Loader2 } from "lucide-react"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useState, useEffect } from "react"
import { userApi } from "@/services/userApi"
import { orderApi } from "@/services/orderApi"
import { stockApi } from "@/services/stockApi"
import { useAuth } from "@/hooks/useAuth"
import { useToast } from "@/hooks/use-toast"
import type { PortfolioSummary, PortfolioItem } from "@/types/user"

export function PortfolioHoldings() {
  const [portfolio, setPortfolio] = useState<PortfolioSummary | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [sellDialogOpen, setSellDialogOpen] = useState(false)
  const [selectedHolding, setSelectedHolding] = useState<PortfolioItem | null>(null)
  const [sellQuantity, setSellQuantity] = useState("")
  const [currentPrice, setCurrentPrice] = useState(0)
  const [isPlacingOrder, setIsPlacingOrder] = useState(false)
  const { user } = useAuth()
  const { toast } = useToast()

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

  const handleOpenSellDialog = async (holding: PortfolioItem) => {
    setSelectedHolding(holding)
    setSellQuantity("")
    
    // Fetch current price
    try {
      const stockData = await stockApi.getStockPerformance(holding.stock, '1d')
      setCurrentPrice(stockData.currentPrice)
    } catch (err) {
      toast({
        title: "Warning",
        description: "Could not fetch current price",
        variant: "destructive",
      })
      setCurrentPrice(0)
    }
    
    setSellDialogOpen(true)
  }

  const handleSell = async () => {
    if (!user || !selectedHolding) return

    const qty = Number.parseFloat(sellQuantity)
    if (!qty || qty <= 0) {
      toast({
        title: "Error",
        description: "Please enter a valid quantity",
        variant: "destructive",
      })
      return
    }

    if (qty > selectedHolding.shares) {
      toast({
        title: "Error",
        description: `You only have ${selectedHolding.shares.toFixed(2)} shares available`,
        variant: "destructive",
      })
      return
    }

    setIsPlacingOrder(true)
    try {
      await orderApi.placeOrder({
        userId: user.email,
        symbol: selectedHolding.stock,
        side: "SELL",
        quantity: qty,
        price: currentPrice,
      })

      toast({
        title: "Order Placed",
        description: `SELL order for ${qty} ${selectedHolding.stock} shares sent to trading service`,
      })

      setSellDialogOpen(false)
      await fetchPortfolio()
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
                      <div className="font-bold">{holding.stock || 'N/A'}</div>
                    </td>
                    <td className="text-right py-4 px-4">{holding.shares != null ? holding.shares.toFixed(2) : '0.00'}</td>
                    <td className="text-right py-4 px-4 font-bold">${holding.portfolioValue != null ? holding.portfolioValue.toFixed(2) : '0.00'}</td>
                    <td className="text-right py-4 px-4">
                      <Badge variant="secondary">{holding.allocation != null ? holding.allocation.toFixed(2) : '0.00'}%</Badge>
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
                          <DropdownMenuItem onClick={() => handleOpenSellDialog(holding)}>Sell</DropdownMenuItem>
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

      <Dialog open={sellDialogOpen} onOpenChange={setSellDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Sell {selectedHolding?.stock}</DialogTitle>
            <DialogDescription>
              Enter the quantity you want to sell. You currently have {selectedHolding?.shares.toFixed(2)} shares.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="sell-quantity">Quantity</Label>
              <Input
                id="sell-quantity"
                type="number"
                placeholder="0"
                value={sellQuantity}
                onChange={(e) => setSellQuantity(e.target.value)}
                max={selectedHolding?.shares}
              />
              <p className="text-xs text-muted-foreground">
                Available: {selectedHolding?.shares.toFixed(2)} shares
              </p>
            </div>
            <div className="space-y-2">
              <Label htmlFor="current-price">Current Price</Label>
              <Input
                id="current-price"
                type="text"
                value={`$${currentPrice.toFixed(2)}`}
                disabled
                className="opacity-75"
              />
            </div>
            <div className="bg-secondary/50 p-3 rounded-lg">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Total Value</span>
                <span className="font-medium">
                  ${(Number.parseFloat(sellQuantity || "0") * currentPrice).toFixed(2)}
                </span>
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setSellDialogOpen(false)} disabled={isPlacingOrder}>
              Cancel
            </Button>
            <Button 
              onClick={handleSell} 
              disabled={isPlacingOrder}
              className="bg-destructive hover:bg-destructive/90"
            >
              {isPlacingOrder ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Placing Order...
                </>
              ) : (
                "Sell"
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </Card>
  )
}
