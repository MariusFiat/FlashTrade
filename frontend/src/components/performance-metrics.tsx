
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { TrendingUp, TrendingDown, Award, Loader2 } from "lucide-react"
import { useState, useEffect } from "react"
import { userApi } from "@/services/userApi"

interface PerformanceMetric {
  label: string
  stock?: string
  value: string
  subtitle?: string
  isPositive?: boolean
}

export function PerformanceMetrics() {
  const [metrics, setMetrics] = useState<PerformanceMetric[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    fetchMetrics()
  }, [])

  const fetchMetrics = async () => {
    try {
      setIsLoading(true)
      const portfolio = await userApi.getPortfolio()
      
      const calculatedMetrics: PerformanceMetric[] = []

      if (portfolio.items && portfolio.items.length > 0) {
        // Calculate best and worst performers based on allocation
        const sortedByAllocation = [...portfolio.items].sort((a, b) => (b.allocation ?? 0) - (a.allocation ?? 0))
        const bestPerformer = sortedByAllocation[0]
        const worstPerformer = sortedByAllocation[sortedByAllocation.length - 1]

        calculatedMetrics.push({
          label: "Best Performer",
          stock: bestPerformer.stock,
          value: `${(bestPerformer.allocation ?? 0).toFixed(2)}%`,
          isPositive: true,
        })

        if (portfolio.items.length > 1) {
          calculatedMetrics.push({
            label: "Worst Performer",
            stock: worstPerformer.stock,
            value: `${(worstPerformer.allocation ?? 0).toFixed(2)}%`,
            isPositive: (worstPerformer.allocation ?? 0) < 0,
          })
        }

        // Portfolio diversity
        calculatedMetrics.push({
          label: "Portfolio Diversity",
          value: `${portfolio.items.length} Stock${portfolio.items.length !== 1 ? 's' : ''}`,
          subtitle: `Total holdings`,
        })

        // Total Return
        if (portfolio.totalReturn !== undefined && portfolio.totalReturn !== null) {
          const totalInvested = portfolio.totalInvested ?? 0
          const totalReturn = portfolio.totalReturn ?? 0
          const returnPercent = totalInvested > 0 
            ? ((totalReturn / totalInvested) * 100) 
            : 0
          calculatedMetrics.push({
            label: "Total Return",
            value: returnPercent >= 0 ? `+${returnPercent.toFixed(2)}%` : `${returnPercent.toFixed(2)}%`,
            subtitle: `$${totalReturn.toFixed(2)}`,
            isPositive: returnPercent >= 0,
          })
        }
      } else {
        // No portfolio data
        calculatedMetrics.push(
          {
            label: "Best Performer",
            value: "N/A",
            subtitle: "No holdings yet",
          },
          {
            label: "Worst Performer",
            value: "N/A",
            subtitle: "No holdings yet",
          },
          {
            label: "Portfolio Diversity",
            value: "0 Stocks",
            subtitle: "Start trading",
          },
          {
            label: "Total Return",
            value: "$0.00",
            subtitle: "No investments",
          }
        )
      }

      setMetrics(calculatedMetrics)
    } catch (error) {
      console.error('Failed to fetch performance metrics:', error)
      // Set default metrics on error
      setMetrics([
        {
          label: "Best Performer",
          value: "N/A",
          subtitle: "Data unavailable",
        },
        {
          label: "Worst Performer",
          value: "N/A",
          subtitle: "Data unavailable",
        },
        {
          label: "Portfolio Diversity",
          value: "N/A",
          subtitle: "Data unavailable",
        },
        {
          label: "Total Return",
          value: "N/A",
          subtitle: "Data unavailable",
        },
      ])
    } finally {
      setIsLoading(false)
    }
  }

  if (isLoading) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Award className="h-5 w-5 text-primary" />
            Performance Metrics
          </CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center py-12">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Award className="h-5 w-5 text-primary" />
          Performance Metrics
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {metrics.map((metric, index) => (
          <div key={index} className="p-4 bg-secondary/30 rounded-lg border border-border/50">
            <div className="text-sm text-muted-foreground mb-1">{metric.label}</div>
            <div className="flex items-center justify-between">
              <div>
                {metric.stock && <div className="font-bold text-lg">{metric.stock}</div>}
                <div
                  className={`text-xl font-bold ${
                    metric.isPositive === true
                      ? "text-profit"
                      : metric.isPositive === false
                        ? "text-loss"
                        : "text-foreground"
                  }`}
                >
                  {metric.value}
                </div>
                {metric.subtitle && <div className="text-xs text-muted-foreground mt-1">{metric.subtitle}</div>}
              </div>
              {metric.isPositive !== undefined &&
                (metric.isPositive ? (
                  <TrendingUp className="h-6 w-6 text-profit" />
                ) : (
                  <TrendingDown className="h-6 w-6 text-loss" />
                ))}
            </div>
          </div>
        ))}
      </CardContent>
    </Card>
  )
}
