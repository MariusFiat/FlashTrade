import { DashboardHeader } from "@/components/dashboard-header"
import { AIOverview } from "@/components/ai-overview"
import { MarketForecasts } from "@/components/market-forecasts"
import { RiskAnalysis } from "@/components/risk-analysis"
import { TradingSignals } from "@/components/trading-signals"
import { AIChat } from "@/components/ai-chat"

export default function AIInsightsPage() {
  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="container mx-auto p-6 space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold">AI Insights</h1>
            <p className="text-muted-foreground mt-1">Powered predictions to maximize your profits</p>
          </div>
        </div>

        <AIOverview />

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <MarketForecasts />
            <TradingSignals />
          </div>
          <div className="space-y-6">
            <RiskAnalysis />
            <AIChat />
          </div>
        </div>
      </main>
    </div>
  )
}
