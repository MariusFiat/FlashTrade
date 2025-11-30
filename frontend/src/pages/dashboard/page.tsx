import { DashboardHeader } from "@/components/dashboard-header"
import { MarketOverview } from "@/components/market-overview"
import { TradingPanel } from "@/components/trading-panel"
import { ActiveOrders } from "@/components/active-orders"
import { PortfolioSummary } from "@/components/portfolio-summary"
import { StockChart } from "@/components/stock-chart"
import { AIPredictions } from "@/components/ai-predictions"

export default function DashboardPage() {
  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="container mx-auto p-6 space-y-6">
        {/* Top Stats */}
        <MarketOverview />

        {/* Main Trading Area */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left: Chart and Trading */}
          <div className="lg:col-span-2 space-y-6">
            <StockChart />
            <ActiveOrders />
          </div>

          {/* Right: Trading Panel and AI */}
          <div className="space-y-6">
            <TradingPanel />
            <AIPredictions />
          </div>
        </div>

        {/* Portfolio Summary */}
        <PortfolioSummary />
      </main>
    </div>
  )
}
