import { DashboardHeader } from "@/components/dashboard-header"
import { WalletOverview } from "@/components/wallet-overview"
import { TransactionHistory } from "@/components/transaction-history"
import { DepositWithdraw } from "@/components/deposit-withdraw"
import { useState, useEffect } from "react"
import { userApi } from "@/services/userApi"
import type { WalletInfo } from "@/types/user"
import { Loader2 } from "lucide-react"

export default function WalletPage() {
  const [walletInfo, setWalletInfo] = useState<WalletInfo | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [refreshKey, setRefreshKey] = useState(0)

  useEffect(() => {
    fetchWalletInfo()
  }, [refreshKey])

  const fetchWalletInfo = async () => {
    try {
      setIsLoading(true)
      const data = await userApi.getWalletInfo()
      setWalletInfo(data)
    } catch (err) {
      console.error('Failed to fetch wallet info:', err)
    } finally {
      setIsLoading(false)
    }
  }

  const handleTransactionComplete = () => {
    // Force re-render of components by updating key
    setRefreshKey(prev => prev + 1)
  }

  if (!walletInfo && !isLoading) {
    return (
      <div className="min-h-screen bg-background">
        <DashboardHeader />
        <main className="container mx-auto p-6">
          <div className="text-center py-12 text-muted-foreground">
            Failed to load wallet information
          </div>
        </main>
      </div>
    )
  }

  if (!walletInfo) {
    return (
       <div className="min-h-screen bg-background">
         <DashboardHeader />
         <main className="container mx-auto p-6">
            <div className="flex items-center justify-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
            </div>
         </main>
       </div>
    )
  }

  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="container mx-auto p-6 space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold">Wallet</h1>
        </div>

        <WalletOverview walletInfo={walletInfo} />

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <TransactionHistory currency={walletInfo.currency} key={`transactions-${refreshKey}`} />
          </div>
          <div>
            <DepositWithdraw walletInfo={walletInfo} onTransactionComplete={handleTransactionComplete} />
          </div>
        </div>
      </main>
    </div>
  )
}
