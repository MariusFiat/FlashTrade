
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Area, AreaChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts"
import { useState, useEffect, useCallback } from "react"
import { userApi } from "@/services/userApi"
import { Loader2 } from "lucide-react"
import { useWebSocket } from "@/hooks/useWebSocket"
import { TransactionUpdate } from "@/services/websocket"

const timeframes = ["1W", "1M", "3M", "6M", "1Y", "ALL"]

export function PortfolioChart() {
  const [selectedTimeframe, setSelectedTimeframe] = useState("1Y")
  const [data, setData] = useState<Array<{ date: string; value: number }>>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const handleTransactionUpdate = useCallback((update: TransactionUpdate) => {
    console.log('Transaction update received in portfolio chart:', update);
    // Update the latest data point with the transaction impact
    setData(prev => {
      if (prev.length === 0) return prev;
      const newData = [...prev];
      const lastPoint = newData[newData.length - 1];
      // Calculate impact: for buy orders add, for sell orders depends on profit/loss
      const impact = update.side.toLowerCase() === 'buy' 
        ? -(update.quantity * update.price) // Buying decreases cash
        : (update.quantity * update.price); // Selling increases cash
      newData[newData.length - 1] = {
        ...lastPoint,
        value: lastPoint.value + impact
      };
      return newData;
    });
  }, []);

  useWebSocket(handleTransactionUpdate);

  useEffect(() => {
    fetchPerformance()
  }, [selectedTimeframe])

  const fetchPerformance = async () => {
    try {
      setIsLoading(true)
      setError(null)
      const rangeMap: Record<string, string> = {
        '1W': '1w',
        '1M': '1m',
        '3M': '3m',
        '6M': '6m',
        '1Y': '1y',
        'ALL': 'all'
      }
      const range = rangeMap[selectedTimeframe] || '1y'
      const response = await userApi.getPortfolioPerformance(range)
      
      const chartData = response.history.map(point => ({
        date: new Date(point.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }),
        value: Math.round(point.value)
      }))
      
      setData(chartData)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load portfolio performance')
      setData([])
    } finally {
      setIsLoading(false)
    }
  }

  const isPositive = data.length > 1 && data[data.length - 1].value > data[0].value
  const lineColor = isPositive ? "#22c55e" : "#ef4444"

  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle>Portfolio Performance</CardTitle>
        <div className="flex gap-2">
          {timeframes.map((tf) => (
            <Button
              key={tf}
              variant={selectedTimeframe === tf ? "default" : "ghost"}
              size="sm"
              onClick={() => setSelectedTimeframe(tf)}
              className="h-8 px-3"
              disabled={isLoading}
            >
              {tf}
            </Button>
          ))}
        </div>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="flex items-center justify-center h-[350px]">
            <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
          </div>
        ) : error || data.length === 0 ? (
          <div className="flex items-center justify-center h-[350px] text-muted-foreground">
            {error || 'No performance data available'}
          </div>
        ) : (
          <ResponsiveContainer width="100%" height={350}>
            <AreaChart data={data}>
              <defs>
                <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor={lineColor} stopOpacity={0.3} />
                  <stop offset="95%" stopColor={lineColor} stopOpacity={0} />
                </linearGradient>
              </defs>
              <XAxis
                dataKey="date"
                stroke="oklch(0.65 0.01 265)"
                fontSize={12}
                tickLine={false}
                axisLine={false}
              />
              <YAxis
                stroke="oklch(0.65 0.01 265)"
                fontSize={12}
                tickLine={false}
                axisLine={false}
                tickFormatter={(value) => `$${(value / 1000).toFixed(0)}k`}
              />
              <Tooltip
                contentStyle={{
                  backgroundColor: "oklch(0.13 0.015 265)",
                  border: "1px solid oklch(0.25 0.015 265)",
                  borderRadius: "8px",
                  color: "oklch(0.98 0.01 265)",
                }}
                labelStyle={{ color: "oklch(0.98 0.01 265)" }}
                formatter={(value: number) => [`$${value.toLocaleString()}`, "Portfolio Value"]}
              />
              <Area
                type="monotone"
                dataKey="value"
                stroke={lineColor}
                strokeWidth={2}
                fillOpacity={1}
                fill="url(#colorValue)"
              />
            </AreaChart>
          </ResponsiveContainer>
        )}
      </CardContent>
    </Card>
  )
}
