import { LoginForm } from "@/components/login-form"
import { TrendingUp } from "lucide-react"

export default function LoginPage() {
  return (
    <div className="min-h-screen flex">
      {/* Left side - Branding */}
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-card via-background to-card p-12 flex-col justify-between relative overflow-hidden">
        <div className="absolute inset-0 bg-[url('/grid.svg')] opacity-10" />
        <div className="relative z-10">
          <div className="flex items-center gap-2 mb-12">
            <div className="bg-primary p-2 rounded-lg">
              <TrendingUp className="h-6 w-6 text-primary-foreground" />
            </div>
            <span className="text-2xl font-bold text-foreground">FlashTrade</span>
          </div>
          <div className="space-y-4">
            <h1 className="text-5xl font-bold text-balance leading-tight">
              {"Trade Stocks at "}
              <span className="text-primary">Lightning Speed</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-md">
              {"Real-time market data, AI-powered insights, and instant execution for professional traders."}
            </p>
          </div>
        </div>
        <div className="relative z-10 grid grid-cols-3 gap-6">
          <div className="space-y-1">
            <div className="text-3xl font-bold text-primary">{"$0"}</div>
            <div className="text-sm text-muted-foreground">{"Commission Fees"}</div>
          </div>
          <div className="space-y-1">
            <div className="text-3xl font-bold text-primary">{"24/7"}</div>
            <div className="text-sm text-muted-foreground">{"Market Access"}</div>
          </div>
          <div className="space-y-1">
            <div className="text-3xl font-bold text-primary">{"AI-Driven"}</div>
            <div className="text-sm text-muted-foreground">{"Predictions"}</div>
          </div>
        </div>
      </div>

      {/* Right side - Login Form */}
      <div className="flex-1 flex items-center justify-center p-8 bg-background">
        <div className="w-full max-w-md">
          <div className="lg:hidden mb-8 flex items-center gap-2">
            <div className="bg-primary p-2 rounded-lg">
              <TrendingUp className="h-6 w-6 text-primary-foreground" />
            </div>
            <span className="text-2xl font-bold">FlashTrade</span>
          </div>
          <LoginForm />
        </div>
      </div>
    </div>
  )
}
