
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Plus, Search, Pencil, Trash2, MoreHorizontal } from "lucide-react"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { useState } from "react"

const stocks = [
  {
    id: 1,
    symbol: "AAPL",
    name: "Apple Inc.",
    price: 151.89,
    volume: "52.4M",
    change: "+2.34%",
    status: "Active",
    sector: "Technology",
  },
  {
    id: 2,
    symbol: "TSLA",
    name: "Tesla Inc.",
    price: 245.8,
    volume: "89.2M",
    change: "+4.12%",
    status: "Active",
    sector: "Automotive",
  },
  {
    id: 3,
    symbol: "GOOGL",
    name: "Alphabet Inc.",
    price: 142.5,
    volume: "28.7M",
    change: "-0.85%",
    status: "Active",
    sector: "Technology",
  },
  {
    id: 4,
    symbol: "MSFT",
    name: "Microsoft Corp.",
    price: 395.2,
    volume: "31.5M",
    change: "+1.67%",
    status: "Active",
    sector: "Technology",
  },
  {
    id: 5,
    symbol: "NVDA",
    name: "NVIDIA Corp.",
    price: 489.3,
    volume: "42.1M",
    change: "+5.23%",
    status: "Active",
    sector: "Technology",
  },
  {
    id: 6,
    symbol: "AMZN",
    name: "Amazon.com Inc.",
    price: 172.4,
    volume: "38.9M",
    change: "+0.92%",
    status: "Active",
    sector: "E-commerce",
  },
]

export function StockManagement() {
  const [searchTerm, setSearchTerm] = useState("")
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false)

  const filteredStocks = stocks.filter(
    (stock) =>
      stock.symbol.toLowerCase().includes(searchTerm.toLowerCase()) ||
      stock.name.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle>Stock Management</CardTitle>
        <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
          <DialogTrigger asChild>
            <Button className="gap-2">
              <Plus className="h-4 w-4" />
              Add Stock
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-[500px]">
            <DialogHeader>
              <DialogTitle>Add New Stock</DialogTitle>
              <DialogDescription>Add a new stock to the trading platform</DialogDescription>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="symbol">Stock Symbol</Label>
                  <Input id="symbol" placeholder="AAPL" className="bg-secondary border-border" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="price">Initial Price</Label>
                  <Input id="price" type="number" placeholder="0.00" className="bg-secondary border-border" />
                </div>
              </div>
              <div className="space-y-2">
                <Label htmlFor="name">Company Name</Label>
                <Input id="name" placeholder="Apple Inc." className="bg-secondary border-border" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="sector">Sector</Label>
                <Input id="sector" placeholder="Technology" className="bg-secondary border-border" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="description">Description</Label>
                <Input id="description" placeholder="Brief description" className="bg-secondary border-border" />
              </div>
            </div>
            <DialogFooter>
              <Button variant="outline" onClick={() => setIsAddDialogOpen(false)}>
                Cancel
              </Button>
              <Button onClick={() => setIsAddDialogOpen(false)}>Add Stock</Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Search stocks..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-9 bg-secondary border-border"
          />
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-border">
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Symbol</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Name</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Price</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Volume</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Change</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Sector</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Status</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredStocks.map((stock) => {
                const isPositive = stock.change.startsWith("+")
                return (
                  <tr key={stock.id} className="border-b border-border/50 hover:bg-secondary/30 transition-colors">
                    <td className="py-4 px-4">
                      <div className="font-bold">{stock.symbol}</div>
                    </td>
                    <td className="py-4 px-4">
                      <div className="text-sm">{stock.name}</div>
                    </td>
                    <td className="text-right py-4 px-4 font-medium">${stock.price.toFixed(2)}</td>
                    <td className="text-right py-4 px-4 text-sm text-muted-foreground">{stock.volume}</td>
                    <td className="text-right py-4 px-4">
                      <span className={`text-sm font-medium ${isPositive ? "text-profit" : "text-loss"}`}>
                        {stock.change}
                      </span>
                    </td>
                    <td className="py-4 px-4">
                      <Badge variant="outline" className="text-xs">
                        {stock.sector}
                      </Badge>
                    </td>
                    <td className="py-4 px-4">
                      <Badge variant="secondary" className="bg-profit/20 text-profit">
                        {stock.status}
                      </Badge>
                    </td>
                    <td className="text-right py-4 px-4">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-8 w-8">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem>
                            <Pencil className="h-4 w-4 mr-2" />
                            Edit Stock
                          </DropdownMenuItem>
                          <DropdownMenuItem>View Details</DropdownMenuItem>
                          <DropdownMenuItem>Suspend Trading</DropdownMenuItem>
                          <DropdownMenuItem className="text-destructive">
                            <Trash2 className="h-4 w-4 mr-2" />
                            Delete Stock
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  )
}
