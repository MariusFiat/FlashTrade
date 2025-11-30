import { DashboardHeader } from "@/components/dashboard-header"
import { AccountSettings } from "@/components/account-settings"

export default function AccountPage() {
  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      <main className="container mx-auto p-6 space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold">Account Settings</h1>
        </div>

        <AccountSettings />
      </main>
    </div>
  )
}
