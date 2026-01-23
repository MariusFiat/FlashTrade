
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { X, Loader2 } from "lucide-react"
import { useState, useEffect } from "react"
import { orderApi } from "@/services/orderApi"
import { useAuth } from "@/hooks/useAuth"
import { useToast } from "@/hooks/use-toast"
import type { ActiveOrder } from "@/types/order"

export function ActiveOrders() {
  const [orders, setOrders] = useState<ActiveOrder[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [cancellingOrderId, setCancellingOrderId] = useState<number | null>(null)
  const { user } = useAuth()
  const { toast } = useToast()

  const fetchOrders = async () => {
    if (!user) return

    try {
      const data = await orderApi.getActiveOrders(user.email)
      setOrders(data)
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to fetch active orders",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    fetchOrders()
  }, [user])

  const handleCancelOrder = async (orderId: number) => {
    if (!user) return

    setCancellingOrderId(orderId)
    try {
      await orderApi.cancelOrder(orderId, user.email)
      toast({
        title: "Order Cancelled",
        description: "Cancel order command sent to trading service",
      })
      // Refresh orders
      await fetchOrders()
    } catch (error) {
      toast({
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to cancel order",
        variant: "destructive",
      })
    } finally {
      setCancellingOrderId(null)
    }
  }

  const formatTime = (timestamp: string) => {
    const date = new Date(timestamp)
    const now = new Date()
    const diff = Math.floor((now.getTime() - date.getTime()) / 1000 / 60)
    if (diff < 1) return "Just now"
    if (diff < 60) return `${diff} min ago`
    if (diff < 1440) return `${Math.floor(diff / 60)} hr ago`
    return `${Math.floor(diff / 1440)} day ago`
  }
  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle>Active Orders</CardTitle>
        <Button variant="ghost" size="sm" onClick={fetchOrders} disabled={isLoading}>
          {isLoading ? "Loading..." : "Refresh"}
        </Button>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="flex justify-center items-center py-8">
            <Loader2 className="h-6 w-6 animate-spin" />
          </div>
        ) : orders.length === 0 ? (
          <div className="text-center py-8 text-muted-foreground">
            No active orders
          </div>
        ) : (
          <div className="space-y-3">
            {orders.map((order) => (
              <div key={order.orderId} className="flex items-center justify-between p-4 bg-secondary/50 rounded-lg">
                <div className="flex items-center gap-4">
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-bold">{order.symbol}</span>
                      <Badge variant={order.side === "BUY" ? "default" : "destructive"} className="text-xs">
                        {order.side}
                      </Badge>
                    </div>
                    <p className="text-sm text-muted-foreground mt-1">
                      {order.originalQty} shares @ ${order.price.toFixed(2)}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-4">
                  <div className="text-right">
                    <Badge variant={order.status === "EXECUTING" ? "default" : "secondary"} className="mb-1">
                      {order.status}
                    </Badge>
                    <p className="text-xs text-muted-foreground">{formatTime(order.createdAt)}</p>
                  </div>
                  <div className="flex gap-2">
                    <Button 
                      variant="ghost" 
                      size="icon" 
                      className="h-8 w-8 text-destructive"
                      onClick={() => handleCancelOrder(order.orderId)}
                      disabled={cancellingOrderId === order.orderId}
                    >
                      {cancellingOrderId === order.orderId ? (
                        <Loader2 className="h-4 w-4 animate-spin" />
                      ) : (
                        <X className="h-4 w-4" />
                      )}
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  )
}
