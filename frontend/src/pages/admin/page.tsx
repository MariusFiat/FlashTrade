import { DashboardHeader } from "@/components/dashboard-header"
import { AdminStats } from "@/components/admin-stats"
import { StockManagement } from "@/components/stock-management"
import { UserActivity } from "@/components/user-activity"
import { SystemHealth } from "@/components/system-health"

export default function AdminPage() {
  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="container mx-auto p-6 space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold">Admin Dashboard</h1>
            <p className="text-muted-foreground mt-1">Manage stocks, users, and system health</p>
          </div>
        </div>

        <AdminStats />

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <StockManagement />
          </div>
          <div className="space-y-6">
            <SystemHealth />
            <UserActivity />
          </div>
        </div>
      </main>
    </div>
  )
}
