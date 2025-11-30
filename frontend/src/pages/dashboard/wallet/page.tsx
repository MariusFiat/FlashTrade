import { DashboardHeader } from "@/components/dashboard-header"
import { WalletOverview } from "@/components/wallet-overview"
import { TransactionHistory } from "@/components/transaction-history"
import { DepositWithdraw } from "@/components/deposit-withdraw"

export default function WalletPage() {
  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="container mx-auto p-6 space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold">Wallet</h1>
        </div>

        <WalletOverview />

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <TransactionHistory />
          </div>
          <div>
            <DepositWithdraw />
          </div>
        </div>
      </main>
    </div>
  )
}
