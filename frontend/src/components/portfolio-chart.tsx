
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Area, AreaChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts"
import { useState, useMemo } from "react"

const timeframes = ["1W", "1M", "3M", "6M", "1Y", "ALL"]

const generateMockData = (timeframe: string) => {
  const baseValue = 100000
  const dataConfigs: Record<string, { labels: string[]; volatility: number }> = {
    "1W": { labels: ["Mon", "Tue", "Wed", "Thu", "Fri"], volatility: 0.02 },
    "1M": { labels: ["W1", "W2", "W3", "W4"], volatility: 0.05 },
    "3M": { labels: ["Month 1", "Month 2", "Month 3"], volatility: 0.08 },
    "6M": { labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun"], volatility: 0.12 },
    "1Y": { labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"], volatility: 0.25 },
    "ALL": { labels: ["2020", "2021", "2022", "2023", "2024", "2025"], volatility: 0.4 },
  }

  const config = dataConfigs[timeframe] || dataConfigs["1Y"]
  let currentValue = baseValue

  return config.labels.map((date) => {
    const change = (Math.random() - 0.3) * baseValue * config.volatility
    currentValue += change
    return { date, value: Math.round(currentValue) }
  })
}

export function PortfolioChart() {
  const [selectedTimeframe, setSelectedTimeframe] = useState("1Y")

  const mockData = useMemo(() => generateMockData(selectedTimeframe), [selectedTimeframe])
  const isPositive = mockData[mockData.length - 1].value > mockData[0].value
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
            >
              {tf}
            </Button>
          ))}
        </div>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={350}>
          <AreaChart data={mockData}>
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
      </CardContent>
    </Card>
  )
}
