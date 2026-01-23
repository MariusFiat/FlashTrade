import { Card, CardContent } from "@/components/ui/card"
import { Brain, TrendingUp, Shield, Zap, RefreshCw } from "lucide-react"
import { useEffect, useState } from "react"
import { aiApi, AIOverviewStats } from "@/services/aiApi"
import { Button } from "@/components/ui/button"

export function AIOverview() {
  const [stats, setStats] = useState<AIOverviewStats | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchStats = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await aiApi.getOverviewStats()
      setStats(data)
    } catch (err) {
      console.error('Error fetching AI overview stats:', err)
      setError('Failed to load AI statistics')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchStats()
    // Refresh every 5 minutes
    const interval = setInterval(fetchStats, 5 * 60 * 1000)
    return () => clearInterval(interval)
  }, [])

  if (loading && !stats) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {[1, 2, 3, 4].map((i) => (
          <Card key={i} className="bg-card border-border/50 animate-pulse">
            <CardContent className="p-6">
              <div className="h-20 bg-secondary/50 rounded"></div>
            </CardContent>
          </Card>
        ))}
      </div>
    )
  }

  if (error || !stats) {
    return (
      <Card className="bg-card border-border/50">
        <CardContent className="p-6 text-center">
          <p className="text-muted-foreground mb-4">{error || 'No data available'}</p>
          <Button onClick={fetchStats} variant="outline" size="sm">
            <RefreshCw className="h-4 w-4 mr-2" />
            Retry
          </Button>
        </CardContent>
      </Card>
    )
  }

  const displayStats = [
    {
      label: "Prediction Accuracy",
      value: `${stats.predictionAccuracy.toFixed(1)}%`,
      change: `${stats.accuracyChange >= 0 ? '+' : ''}${stats.accuracyChange.toFixed(1)}% this month`,
      icon: Brain,
      color: "text-primary",
    },
    {
      label: "Profitable Signals",
      value: `${stats.profitableSignals.successful}/${stats.profitableSignals.total}`,
      change: `${stats.profitableSignals.successRate.toFixed(1)}% success rate`,
      icon: TrendingUp,
      color: "text-profit",
    },
    {
      label: "Risk Score",
      value: stats.riskScore,
      change: stats.riskDescription,
      icon: Shield,
      color: stats.riskScore === 'Low' ? "text-profit" : stats.riskScore === 'High' ? "text-loss" : "text-warning",
    },
    {
      label: "Active Alerts",
      value: stats.activeAlerts.total.toString(),
      change: `${stats.activeAlerts.highPriority} high priority`,
      icon: Zap,
      color: "text-primary",
    },
  ]

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button 
          onClick={fetchStats} 
          variant="ghost" 
          size="sm"
          disabled={loading}
        >
          <RefreshCw className={`h-4 w-4 mr-2 ${loading ? 'animate-spin' : ''}`} />
          Refresh
        </Button>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {displayStats.map((stat, index) => {
          const Icon = stat.icon
          return (
            <Card key={index} className="bg-card border-border/50">
              <CardContent className="p-6">
                <div className="flex items-start justify-between">
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">{stat.label}</p>
                    <p className="text-2xl font-bold">{stat.value}</p>
                    <p className="text-xs text-muted-foreground">{stat.change}</p>
                  </div>
                  <div className="bg-primary/10 p-3 rounded-lg">
                    <Icon className={`h-5 w-5 ${stat.color}`} />
                  </div>
                </div>
              </CardContent>
            </Card>
          )
        })}
      </div>
    </div>
  )
}