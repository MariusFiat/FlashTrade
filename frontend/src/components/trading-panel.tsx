
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { useState } from "react"

export function TradingPanel() {
  const [orderType, setOrderType] = useState<"buy" | "sell">("buy")
  const [quantity, setQuantity] = useState("")
  const [price, setPrice] = useState("151.89")

  const handleTrade = () => {
    console.log(`${orderType} order:`, { quantity, price })
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
                onChange={(e) => setPrice(e.target.value)}
                className="bg-secondary border-border"
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
                <span className="font-medium">$25,430.00</span>
              </div>
            </div>
            <Button className="w-full bg-primary hover:bg-green-700 text-white" onClick={handleTrade}>
              Buy AAPL
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
              <p className="text-xs text-muted-foreground">Available: 50 shares</p>
            </div>
            <div className="space-y-2">
              <Label htmlFor="sell-price">Price per share</Label>
              <Input
                id="sell-price"
                type="number"
                placeholder="0.00"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                className="bg-secondary border-border"
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
                <span className="text-muted-foreground">Est. Profit/Loss</span>
                <span className="font-medium text-green-500">+$234.50</span>
              </div>
            </div>
            <Button
              className="w-full bg-primary hover:bg-red-700 text-white"
              onClick={handleTrade}
            >
              Sell AAPL
            </Button>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}
