import { DashboardHeader } from "@/components/dashboard-header"
import { PortfolioOverview } from "@/components/portfolio-overview"
import { PortfolioChart } from "@/components/portfolio-chart"
import { PortfolioHoldings } from "@/components/portfolio-holdings"
import { PerformanceMetrics } from "@/components/performance-metrics"

export default function PortfolioPage() {
  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="container mx-auto p-6 space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold">Portfolio</h1>
        </div>

        <PortfolioOverview />

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <PortfolioChart />
          </div>
          <div>
            <PerformanceMetrics />
          </div>
        </div>

        <PortfolioHoldings />
      </main>
    </div>
  )
}
