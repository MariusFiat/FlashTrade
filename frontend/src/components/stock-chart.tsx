
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { ResponsiveContainer, Tooltip, XAxis, YAxis, CartesianGrid, Area, AreaChart } from "recharts"
import { ArrowUp, ArrowDown, Check, ChevronsUpDown } from "lucide-react"
import { useState, useEffect } from "react"
import { cn } from "@/lib/utils"

const timeframes = ["1D", "1W", "1M", "3M", "1Y", "ALL"]

const fetchStocksFromBackend = async (): Promise<Array<{ symbol: string; name: string }>> => {
  // Simulating API call delay
  await new Promise((resolve) => setTimeout(resolve, 100))
  return [
    { symbol: "AAPL", name: "Apple Inc." },
    { symbol: "TSLA", name: "Tesla Inc." },
    { symbol: "GOOGL", name: "Alphabet Inc." },
    { symbol: "MSFT", name: "Microsoft Corp." },
    { symbol: "NVDA", name: "NVIDIA Corp." },
    { symbol: "AMZN", name: "Amazon.com Inc." },
    { symbol: "META", name: "Meta Platforms Inc." },
    { symbol: "NFLX", name: "Netflix Inc." },
    { symbol: "AMD", name: "Advanced Micro Devices" },
    { symbol: "INTC", name: "Intel Corporation" },
  ]
}

const generateMockData = (symbol: string, timeframe: string) => {
  const basePrice: Record<string, number> = {
    AAPL: 150,
    TSLA: 240,
    GOOGL: 140,
    MSFT: 390,
    NVDA: 480,
    AMZN: 180,
    META: 500,
    NFLX: 620,
    AMD: 160,
    INTC: 45,
  }

  const base = basePrice[symbol] || 100
  const volatility = base * 0.02

  const dataPoints: Record<string, { labels: string[]; count: number }> = {
    "1D": {
      labels: [
        "09:30",
        "10:00",
        "10:30",
        "11:00",
        "11:30",
        "12:00",
        "12:30",
        "13:00",
        "13:30",
        "14:00",
        "14:30",
        "15:00",
      ],
      count: 12,
    },
    "1W": { labels: ["Mon", "Tue", "Wed", "Thu", "Fri"], count: 5 },
    "1M": { labels: ["W1", "W2", "W3", "W4"], count: 4 },
    "3M": { labels: ["Jan", "Feb", "Mar"], count: 3 },
    "1Y": { labels: ["Jan", "Mar", "May", "Jul", "Sep", "Nov"], count: 6 },
    ALL: { labels: ["2020", "2021", "2022", "2023", "2024"], count: 5 },
  }

  const config = dataPoints[timeframe] || dataPoints["1D"]
  let currentPrice = base - volatility * 2

  return config.labels.map((time) => {
    currentPrice = currentPrice + (Math.random() - 0.4) * volatility
    return { time, price: Number(currentPrice.toFixed(2)) }
  })
}

export function StockChart() {
  const [selectedTimeframe, setSelectedTimeframe] = useState("1D")
  const [selectedStock, setSelectedStock] = useState("AAPL")
  const [open, setOpen] = useState(false)
  const [stockList, setStockList] = useState<Array<{ symbol: string; name: string }>>([])
  const [data, setData] = useState<Array<{ time: string; price: number }>>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const loadStocks = async () => {
      const stocks = await fetchStocksFromBackend()
      setStockList(stocks)
      setLoading(false)
    }
    loadStocks()
  }, [])

  useEffect(() => {
    const newData = generateMockData(selectedStock, selectedTimeframe)
    setData(newData)
  }, [selectedStock, selectedTimeframe])

  if (loading || data.length === 0) {
    return (
      <Card className="bg-card border-border/50">
        <CardContent className="flex items-center justify-center h-[450px]">
          <div className="text-muted-foreground">Loading chart...</div>
        </CardContent>
      </Card>
    )
  }

  const currentPrice = data[data.length - 1].price
  const previousPrice = data[0].price
  const priceChange = currentPrice - previousPrice
  const percentChange = ((priceChange / previousPrice) * 100).toFixed(2)
  const isPositive = priceChange >= 0

  const stockInfo = stockList.find((s) => s.symbol === selectedStock) || { symbol: selectedStock, name: "" }

  const lineColorRgb = isPositive ? "#22c55e" : "#ef4444"

  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="space-y-4 pb-4">
        <div className="flex items-center justify-between gap-4">
          <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
              <Button
                variant="outline"
                role="combobox"
                aria-expanded={open}
                className="w-[250px] justify-between bg-secondary border-border"
              >
                {selectedStock
                  ? stockList.find((stock) => stock.symbol === selectedStock)?.symbol
                  : "Select stock..."}
                <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-[250px] p-0">
              <Command>
                <CommandInput placeholder="Search stock..." />
                <CommandList>
                  <CommandEmpty>No stock found.</CommandEmpty>
                  <CommandGroup>
                    {stockList.map((stock) => (
                      <CommandItem
                        key={stock.symbol}
                        value={stock.symbol}
                        onSelect={(currentValue) => {
                          setSelectedStock(currentValue.toUpperCase())
                          setOpen(false)
                        }}
                      >
                        <Check
                          className={cn(
                            "mr-2 h-4 w-4",
                            selectedStock === stock.symbol ? "opacity-100" : "opacity-0"
                          )}
                        />
                        <span className="font-medium">{stock.symbol}</span>
                        <span className="text-muted-foreground ml-2">- {stock.name}</span>
                      </CommandItem>
                    ))}
                  </CommandGroup>
                </CommandList>
              </Command>
            </PopoverContent>
          </Popover>

          <div className="flex gap-1">
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
        </div>

        <div className="flex items-center justify-between">
          <div className="space-y-1">
            <CardTitle className="text-xl font-bold flex items-center gap-3">
              <span>{selectedStock}</span>
              <span className="text-muted-foreground text-sm font-normal">{stockInfo.name}</span>
            </CardTitle>
            <div className="flex items-baseline gap-3">
              <span className="text-3xl font-bold">${currentPrice.toFixed(2)}</span>
              <span
                className={`text-sm font-medium flex items-center gap-1 ${isPositive ? "text-green-500" : "text-red-500"}`}
              >
                {isPositive ? <ArrowUp className="h-4 w-4" /> : <ArrowDown className="h-4 w-4" />}
                {isPositive ? "+" : ""}
                {priceChange.toFixed(2)} ({isPositive ? "+" : ""}
                {percentChange}%)
              </span>
            </div>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={350}>
          <AreaChart data={data} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
            <defs>
              <linearGradient id="colorPrice" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor={lineColorRgb} stopOpacity={0.3} />
                <stop offset="95%" stopColor={lineColorRgb} stopOpacity={0} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="oklch(0.25 0.015 265)" opacity={0.3} />
            <XAxis dataKey="time" stroke="oklch(0.65 0.01 265)" fontSize={12} tickLine={false} axisLine={false} />
            <YAxis
              stroke="oklch(0.65 0.01 265)"
              fontSize={12}
              tickLine={false}
              axisLine={false}
              tickFormatter={(value) => `$${value}`}
              domain={["dataMin - 5", "dataMax + 5"]}
            />
            <Tooltip
              contentStyle={{
                backgroundColor: "oklch(0.13 0.015 265)",
                border: "1px solid oklch(0.25 0.015 265)",
                borderRadius: "8px",
                color: "oklch(0.98 0.01 265)",
              }}
              labelStyle={{ color: "oklch(0.98 0.01 265)" }}
              formatter={(value: number) => [`$${value.toFixed(2)}`, "Price"]}
            />
            <Area
              type="monotone"
              dataKey="price"
              stroke={lineColorRgb}
              strokeWidth={2}
              fill="url(#colorPrice)"
              animationDuration={300}
            />
          </AreaChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  )
}
