
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { useState } from "react"

export function DepositWithdraw() {
  const [amount, setAmount] = useState("")

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle>Manage Funds</CardTitle>
      </CardHeader>
      <CardContent>
        <Tabs defaultValue="deposit" className="w-full">
          <TabsList className="grid w-full grid-cols-2 mb-4">
            <TabsTrigger value="deposit">Deposit</TabsTrigger>
            <TabsTrigger value="withdraw">Withdraw</TabsTrigger>
          </TabsList>
          <TabsContent value="deposit" className="space-y-4 mt-0">
            <div className="space-y-2">
              <Label htmlFor="deposit-amount">Amount</Label>
              <Input
                id="deposit-amount"
                type="number"
                placeholder="0.00"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="bg-secondary border-border"
              />
            </div>
            <div className="flex gap-2">
              <Button variant="outline" size="sm" onClick={() => setAmount("1000")}>
                $1K
              </Button>
              <Button variant="outline" size="sm" onClick={() => setAmount("5000")}>
                $5K
              </Button>
              <Button variant="outline" size="sm" onClick={() => setAmount("10000")}>
                $10K
              </Button>
            </div>
            <div className="bg-secondary/50 p-3 rounded-lg space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Current Balance</span>
                <span className="font-medium">$25,430.00</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">After Deposit</span>
                <span className="font-medium">${(25430 + Number.parseFloat(amount || "0")).toFixed(2)}</span>
              </div>
            </div>
            <Button className="w-full">Deposit Funds</Button>
          </TabsContent>
          <TabsContent value="withdraw" className="space-y-4 mt-0">
            <div className="space-y-2">
              <Label htmlFor="withdraw-amount">Amount</Label>
              <Input
                id="withdraw-amount"
                type="number"
                placeholder="0.00"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="bg-secondary border-border"
              />
              <p className="text-xs text-muted-foreground">Available: $25,430.00</p>
            </div>
            <div className="flex gap-2">
              <Button variant="outline" size="sm" onClick={() => setAmount("1000")}>
                $1K
              </Button>
              <Button variant="outline" size="sm" onClick={() => setAmount("5000")}>
                $5K
              </Button>
              <Button variant="outline" size="sm" onClick={() => setAmount("25430")}>
                Max
              </Button>
            </div>
            <div className="bg-secondary/50 p-3 rounded-lg space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Current Balance</span>
                <span className="font-medium">$25,430.00</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">After Withdrawal</span>
                <span className="font-medium">${(25430 - Number.parseFloat(amount || "0")).toFixed(2)}</span>
              </div>
            </div>
            <Button className="w-full">Withdraw Funds</Button>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}
