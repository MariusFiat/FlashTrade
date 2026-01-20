
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle, Loader2 } from "lucide-react"
import { useState, useEffect } from "react"
import { userApi } from "@/services/userApi"
import type { WalletInfo } from "@/types/user"
import { getCurrencySymbol } from "@/lib/currency"

interface DepositWithdrawProps {
  walletInfo: WalletInfo;
  onTransactionComplete?: () => void;
}

export function DepositWithdraw({ walletInfo: initialWalletInfo, onTransactionComplete }: DepositWithdrawProps) {
  const [amount, setAmount] = useState("")
  const [walletInfo, setWalletInfo] = useState<WalletInfo>(initialWalletInfo)
  const [currencySymbol, setCurrencySymbol] = useState(() => getCurrencySymbol(initialWalletInfo.currency))
  const [isProcessing, setIsProcessing] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  useEffect(() => {
    setWalletInfo(initialWalletInfo)
    setCurrencySymbol(getCurrencySymbol(initialWalletInfo.currency))
  }, [initialWalletInfo])

  const fetchWalletInfo = async () => {
    try {
      const data = await userApi.getWalletInfo()
      setWalletInfo(data)
      setCurrencySymbol(getCurrencySymbol(data.currency))
    } catch (err) {
      // Silent fail, parent will handle refresh
    }
  }

  const handleDeposit = async () => {
    const amountValue = Number.parseFloat(amount)
    if (!amountValue || amountValue <= 0) {
      setError("Please enter a valid amount")
      return
    }

    try {
      setIsProcessing(true)
      setError(null)
      setSuccess(null)
      
      const response = await userApi.deposit(amountValue)
      setSuccess(response.message)
      setAmount("")
      await fetchWalletInfo()
      onTransactionComplete?.()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to process deposit')
    } finally {
      setIsProcessing(false)
    }
  }

  const handleWithdrawal = async () => {
    const amountValue = Number.parseFloat(amount)
    if (!amountValue || amountValue <= 0) {
      setError("Please enter a valid amount")
      return
    }

    if (walletInfo && amountValue > walletInfo.balance) {
      setError("Insufficient funds")
      return
    }

    try {
      setIsProcessing(true)
      setError(null)
      setSuccess(null)
      
      const response = await userApi.withdrawal(amountValue)
      setSuccess(response.message)
      setAmount("")
      await fetchWalletInfo()
      onTransactionComplete?.()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to process withdrawal')
    } finally {
      setIsProcessing(false)
    }
  }

  const currentBalance = walletInfo?.balance || 0
  const amountValue = Number.parseFloat(amount || "0")

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle>Manage Funds</CardTitle>
      </CardHeader>
      <CardContent>
        {error && (
          <Alert variant="destructive" className="mb-4">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}
        {success && (
          <Alert className="border-green-500/50 bg-green-500/10 mb-4">
            <AlertDescription className="text-green-600 dark:text-green-400">{success}</AlertDescription>
          </Alert>
        )}
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
                disabled={isProcessing}
                className="bg-secondary border-border"
              />
            </div>
            <div className="flex gap-2">
              <Button variant="outline" size="sm" onClick={() => setAmount("1000")} disabled={isProcessing}>
                {currencySymbol}1K
              </Button>
              <Button variant="outline" size="sm" onClick={() => setAmount("5000")} disabled={isProcessing}>
                {currencySymbol}5K
              </Button>
              <Button variant="outline" size="sm" onClick={() => setAmount("10000")} disabled={isProcessing}>
                {currencySymbol}10K
              </Button>
            </div>
            <div className="bg-secondary/50 p-3 rounded-lg space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Current Balance</span>
                <span className="font-medium">{currencySymbol}{currentBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">After Deposit</span>
                <span className="font-medium">{currencySymbol}{(currentBalance + amountValue).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </div>
            </div>
            <Button className="w-full" onClick={handleDeposit} disabled={isProcessing}>
              {isProcessing ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Processing...
                </>
              ) : (
                'Deposit Funds'
              )}
            </Button>
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
                disabled={isProcessing}
                className="bg-secondary border-border"
              />
              <p className="text-xs text-muted-foreground">Available: {currencySymbol}{currentBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</p>
            </div>
            <div className="flex gap-2">
              <Button variant="outline" size="sm" onClick={() => setAmount("1000")} disabled={isProcessing}>
                {currencySymbol}1K
              </Button>
              <Button variant="outline" size="sm" onClick={() => setAmount("5000")} disabled={isProcessing}>
                {currencySymbol}5K
              </Button>
              <Button variant="outline" size="sm" onClick={() => setAmount(currentBalance.toString())} disabled={isProcessing}>
                Max
              </Button>
            </div>
            <div className="bg-secondary/50 p-3 rounded-lg space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Current Balance</span>
                <span className="font-medium">{currencySymbol}{currentBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">After Withdrawal</span>
                <span className="font-medium">{currencySymbol}{Math.max(0, currentBalance - amountValue).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</span>
              </div>
            </div>
            <Button className="w-full" onClick={handleWithdrawal} disabled={isProcessing}>
              {isProcessing ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Processing...
                </>
              ) : (
                'Withdraw Funds'
              )}
            </Button>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}
