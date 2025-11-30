import { DashboardHeader } from "@/components/dashboard-header"
import { MarketsList } from "@/components/markets-list"

export default function MarketsPage() {
  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="container mx-auto p-6 space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold">Markets</h1>
        </div>
        <MarketsList />
      </main>
    </div>
  )
}
