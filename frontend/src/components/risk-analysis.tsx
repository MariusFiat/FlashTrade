import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Shield, AlertTriangle, TrendingUp, RefreshCw } from "lucide-react"
import { Progress } from "@/components/ui/progress"
import { Button } from "@/components/ui/button"
import { useEffect, useState } from "react"
import { aiApi, RiskAnalysisData } from "@/services/aiApi"

export function RiskAnalysis() {
  const [riskData, setRiskData] = useState<RiskAnalysisData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchRiskAnalysis = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await aiApi.getRiskAnalysis()
      setRiskData(data)
    } catch (err) {
      console.error('Error fetching risk analysis:', err)
      setError('Failed to load risk analysis')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchRiskAnalysis()
    // Refresh every 10 minutes
    const interval = setInterval(fetchRiskAnalysis, 10 * 60 * 1000)
    return () => clearInterval(interval)
  }, [])

  if (loading && !riskData) {
    return (
      <Card className="bg-card border-border/50 animate-pulse">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Shield className="h-5 w-5 text-primary" />
            Risk Analysis
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="h-40 bg-secondary/50 rounded"></div>
        </CardContent>
      </Card>
    )
  }

  if (error || !riskData) {
    return (
      <Card className="bg-card border-border/50">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Shield className="h-5 w-5 text-primary" />
            Risk Analysis
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground text-center mb-4">{error || 'No data available'}</p>
          <Button onClick={fetchRiskAnalysis} variant="outline" size="sm" className="w-full">
            <RefreshCw className="h-4 w-4 mr-2" />
            Retry
          </Button>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            <Shield className="h-5 w-5 text-primary" />
            Risk Analysis
          </CardTitle>
          <Button 
            onClick={fetchRiskAnalysis} 
            variant="ghost" 
            size="sm"
            disabled={loading}
          >
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          </Button>
        </div>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="space-y-4">
          {riskData.metrics.map((metric, index) => (
            <div key={index} className="space-y-2">
              <div className="flex items-center justify-between text-sm">
                <span className="text-muted-foreground">{metric.label}</span>
                <span className="font-medium">{metric.status}</span>
              </div>
              <Progress value={metric.value} className="h-2" />
            </div>
          ))}
        </div>

        <div className="space-y-3 pt-4 border-t border-border/50">
          <div className="text-sm font-medium">AI Recommendations</div>
          {riskData.recommendations.length > 0 ? (
            riskData.recommendations.map((rec, index) => (
              <div key={index} className="flex items-start gap-3 p-3 bg-secondary/30 rounded-lg">
                <AlertTriangle
                  className={`h-4 w-4 mt-0.5 ${
                    rec.type === "warning" ? "text-warning" : rec.type === "success" ? "text-profit" : "text-primary"
                  }`}
                />
                <p className="text-sm text-muted-foreground leading-relaxed">{rec.message}</p>
              </div>
            ))
          ) : (
            <p className="text-sm text-muted-foreground text-center py-2">No recommendations at this time</p>
          )}
        </div>

        <div className={`p-4 border rounded-lg ${
          riskData.overallRiskScore === 'Low' 
            ? 'bg-profit/5 border-profit/20' 
            : riskData.overallRiskScore === 'High'
            ? 'bg-loss/5 border-loss/20'
            : 'bg-primary/5 border-primary/20'
        }`}>
          <div className="flex items-center gap-2 mb-2">
            <TrendingUp className={`h-4 w-4 ${
              riskData.overallRiskScore === 'Low' 
                ? 'text-profit' 
                : riskData.overallRiskScore === 'High'
                ? 'text-loss'
                : 'text-primary'
            }`} />
            <span className="text-sm font-medium">Overall Risk Score</span>
          </div>
          <div className={`text-2xl font-bold ${
            riskData.overallRiskScore === 'Low' 
              ? 'text-profit' 
              : riskData.overallRiskScore === 'High'
              ? 'text-loss'
              : 'text-primary'
          }`}>
            {riskData.overallRiskScore}
          </div>
          <p className="text-xs text-muted-foreground mt-1">{riskData.riskDescription}</p>
        </div>
      </CardContent>
    </Card>
  )
}