
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Pencil, X } from "lucide-react"

const orders = [
  { id: 1, stock: "AAPL", type: "Buy", quantity: 10, price: 150.25, status: "Pending", time: "2 min ago" },
  { id: 2, stock: "TSLA", type: "Sell", quantity: 5, price: 245.8, status: "Executing", time: "5 min ago" },
  { id: 3, stock: "GOOGL", type: "Buy", quantity: 8, price: 142.5, status: "Pending", time: "12 min ago" },
]

export function ActiveOrders() {
  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle>Active Orders</CardTitle>
        <Button variant="ghost" size="sm">
          View All
        </Button>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {orders.map((order) => (
            <div key={order.id} className="flex items-center justify-between p-4 bg-secondary/50 rounded-lg">
              <div className="flex items-center gap-4">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold">{order.stock}</span>
                    <Badge variant={order.type === "Buy" ? "default" : "destructive"} className="text-xs">
                      {order.type}
                    </Badge>
                  </div>
                  <p className="text-sm text-muted-foreground mt-1">
                    {order.quantity} shares @ ${order.price}
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-4">
                <div className="text-right">
                  <Badge variant={order.status === "Executing" ? "default" : "secondary"} className="mb-1">
                    {order.status}
                  </Badge>
                  <p className="text-xs text-muted-foreground">{order.time}</p>
                </div>
                <div className="flex gap-2">
                  <Button variant="ghost" size="icon" className="h-8 w-8">
                    <Pencil className="h-4 w-4" />
                  </Button>
                  <Button variant="ghost" size="icon" className="h-8 w-8 text-destructive">
                    <X className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}
