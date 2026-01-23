
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { ResponsiveContainer, Tooltip, XAxis, YAxis, CartesianGrid, Area, AreaChart } from "recharts"
import { ArrowUp, ArrowDown, Check, ChevronsUpDown } from "lucide-react"
import { useState, useEffect, useCallback } from "react"
import { cn } from "@/lib/utils"
import { useWebSocket } from "@/hooks/useWebSocket"
import { TransactionUpdate } from "@/services/websocket"

const timeframes = ["1D", "1W", "1M", "3M", "1Y", "ALL"]

import { stockApi } from "@/services/stockApi"
import { useToast } from "@/hooks/use-toast"
import type { StockHistoryPoint } from "@/types/stock"

const fetchStocksFromBackend = async (): Promise<Array<{ symbol: string; name: string }>> => {
  const response = await stockApi.getAllStocks()
  return response.stocks.map(stock => ({ symbol: stock.symbol, name: stock.name }))
}

export function StockChart() {
  const [selectedTimeframe, setSelectedTimeframe] = useState("1D")
  const [selectedStock, setSelectedStock] = useState("AAPL")
  const [open, setOpen] = useState(false)
  const [stockList, setStockList] = useState<Array<{ symbol: string; name: string }>>([])
  const [data, setData] = useState<Array<{ time: string; price: number }>>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const { toast } = useToast()

  const handleTransactionUpdate = useCallback((update: TransactionUpdate) => {
    console.log('Transaction update received in stock chart:', update);
    // Only update if the transaction is for the currently selected stock
    if (update.symbol === selectedStock && update.status === 'SUCCESS') {
      setData(prev => {
        if (prev.length === 0) return prev;
        const newData = [...prev];
        const lastPoint = newData[newData.length - 1];
        // Update the latest price point with the transaction price
        newData[newData.length - 1] = {
          ...lastPoint,
          price: update.price
        };
        return newData;
      });
    }
  }, [selectedStock]);

  useWebSocket(handleTransactionUpdate);

  useEffect(() => {
    const loadStocks = async () => {
      try {
        const stocks = await fetchStocksFromBackend()
        setStockList(stocks)
      } catch (error) {
        setError('Failed to load stock list')
        toast({
          title: "Error",
          description: "Failed to load available stocks",
          variant: "destructive",
        })
      } finally {
        setLoading(false)
      }
    }
    loadStocks()
  }, [])

  useEffect(() => {
    const fetchPerformance = async () => {
      setError(null)
      try {
        const rangeMap: Record<string, string> = {
          '1D': '1d',
          '1W': '1w',
          '1M': '1m',
          '3M': '3m',
          '1Y': '1y',
          'ALL': 'all'
        }
        const range = rangeMap[selectedTimeframe] || '1d'
        const response = await stockApi.getStockPerformance(selectedStock, range)
        
        // Transform backend data to chart format with timeframe-appropriate formatting
        const chartData = response.history.map((point: StockHistoryPoint) => {
          const date = new Date(point.timestamp)
          let formattedTime: string
          
          if (selectedTimeframe === '1D') {
            // For 1D: show time (e.g., "2:30 PM")
            formattedTime = date.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit' })
          } else if (selectedTimeframe === '1W') {
            // For 1W: show day of week (e.g., "Mon", "Tue")
            formattedTime = date.toLocaleDateString('en-US', { weekday: 'short' })
          } else if (selectedTimeframe === '1M') {
            // For 1M: show month and day (e.g., "Jan 15")
            formattedTime = date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
          } else {
            // For 3M, 1Y, ALL: show month and day (e.g., "Jan 15")
            formattedTime = date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
          }
          
          return {
            time: formattedTime,
            price: point.price
          }
        })
        
        setData(chartData)
      } catch (error) {
        setError(error instanceof Error ? error.message : 'Failed to load stock performance')
        setData([])
        toast({
          title: "Error",
          description: "Could not fetch stock performance data",
          variant: "destructive",
        })
      }
    }
    fetchPerformance()
  }, [selectedStock, selectedTimeframe])

  if (loading) {
    return (
      <Card className="bg-card border-border/50">
        <CardContent className="flex items-center justify-center h-[450px]">
          <div className="text-muted-foreground">Loading chart...</div>
        </CardContent>
      </Card>
    )
  }

  if (error || stockList.length === 0) {
    return (
      <Card className="bg-card border-border/50">
        <CardContent className="flex flex-col items-center justify-center h-[450px] gap-4">
          <div className="text-destructive font-medium">Unable to load stock data</div>
          <div className="text-sm text-muted-foreground">{error || 'No stocks available'}</div>
        </CardContent>
      </Card>
    )
  }

  // Check if we have valid data before rendering the chart
  if (data.length === 0) {
    return (
      <Card className="bg-card border-border/50">
        <CardContent className="flex flex-col items-center justify-center h-[450px] gap-4">
          <div className="text-muted-foreground">No chart data available</div>
        </CardContent>
      </Card>
    )
  }

  const currentPrice = data[data.length - 1]?.price ?? 0
  const previousPrice = data[0]?.price ?? 0
  const priceChange = currentPrice - previousPrice
  const percentChange = previousPrice > 0 ? ((priceChange / previousPrice) * 100).toFixed(2) : '0.00'
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
