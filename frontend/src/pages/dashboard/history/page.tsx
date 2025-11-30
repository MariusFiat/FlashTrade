import { DashboardHeader } from "@/components/dashboard-header"
import { TradeHistory } from "@/components/trade-history"

export default function HistoryPage() {
  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="container mx-auto p-6 space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold">Trade History</h1>
        </div>
        <TradeHistory />
      </main>
    </div>
  )
}
