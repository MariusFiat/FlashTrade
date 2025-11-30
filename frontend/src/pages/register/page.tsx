import { RegisterForm } from "@/components/register-form"
import { TrendingUp } from "lucide-react"

export default function RegisterPage() {
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
              {"Start Your "}
              <span className="text-primary">Trading Journey</span>
            </h1>
            <p className="text-xl text-muted-foreground max-w-md">
              {"Join thousands of traders using AI-powered insights to maximize their profits."}
            </p>
          </div>
        </div>
        <div className="relative z-10 space-y-4">
          <div className="flex items-start gap-3">
            <div className="bg-primary/20 p-2 rounded-lg mt-1">
              <TrendingUp className="h-4 w-4 text-primary" />
            </div>
            <div>
              <div className="font-semibold text-foreground">{"AI-Powered Predictions"}</div>
              <div className="text-sm text-muted-foreground">{"Get real-time buy/sell recommendations"}</div>
            </div>
          </div>
          <div className="flex items-start gap-3">
            <div className="bg-primary/20 p-2 rounded-lg mt-1">
              <TrendingUp className="h-4 w-4 text-primary" />
            </div>
            <div>
              <div className="font-semibold text-foreground">{"Real-Time Data"}</div>
              <div className="text-sm text-muted-foreground">{"Live market prices and portfolio updates"}</div>
            </div>
          </div>
          <div className="flex items-start gap-3">
            <div className="bg-primary/20 p-2 rounded-lg mt-1">
              <TrendingUp className="h-4 w-4 text-primary" />
            </div>
            <div>
              <div className="font-semibold text-foreground">{"Instant Execution"}</div>
              <div className="text-sm text-muted-foreground">{"Lightning-fast trade execution"}</div>
            </div>
          </div>
        </div>
      </div>

      {/* Right side - Register Form */}
      <div className="flex-1 flex items-center justify-center p-8 bg-background">
        <div className="w-full max-w-md">
          <div className="lg:hidden mb-8 flex items-center gap-2">
            <div className="bg-primary p-2 rounded-lg">
              <TrendingUp className="h-6 w-6 text-primary-foreground" />
            </div>
            <span className="text-2xl font-bold">FlashTrade</span>
          </div>
          <RegisterForm />
        </div>
      </div>
    </div>
  )
}
