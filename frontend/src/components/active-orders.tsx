
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { X, Loader2 } from "lucide-react"
import { useState, useEffect, useCallback } from "react"
import { orderApi } from "@/services/orderApi"
import { useAuth } from "@/hooks/useAuth"
import { useToast } from "@/hooks/use-toast"
import { useWebSocket } from "@/hooks/useWebSocket"
import { TransactionUpdate, OrderStatusUpdate } from "@/services/websocket"
import type { ActiveOrder } from "@/types/order"

export function ActiveOrders() {
  const [orders, setOrders] = useState<ActiveOrder[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [cancellingOrderId, setCancellingOrderId] = useState<number | null>(null)
  const { user } = useAuth()
  const { toast } = useToast()

  const handleTransactionUpdate = useCallback((update: TransactionUpdate) => {
    console.log('Active orders received transaction update:', update);
    // Only refresh if the update is for the current user
    if (user && update.userId === user.email) {
      console.log('Refreshing active orders for user:', user.email);
      fetchOrders();
    }
  }, [user]);

  const handleOrderStatusUpdate = useCallback((update: OrderStatusUpdate) => {
    console.log('Order status update received:', update);
    
    const isCancelled = update.status === 'CANCELED' || update.status === 'CANCELLED';
    const isFailed = update.status === 'FAILED';
    
    // Show toast notification for status changes
    if (update.status === 'SUCCESS' || update.status === 'OPEN') {
      toast({
        title: "Order Active",
        description: `${update.orderType} order for ${update.quantity} ${update.symbol} is now ${update.status.toLowerCase()}`,
      });
    } else if (isCancelled) {
      toast({
        title: "Order Cancelled",
        description: `Order #${update.orderId} has been cancelled`,
        variant: "destructive",
      });
    } else if (isFailed) {
      toast({
        title: "Order Failed",
        description: update.errorMessage || `Order #${update.orderId} failed`,
        variant: "destructive",
      });
    }
    
    // Update the order in the list immediately
    setOrders(prevOrders => {
      // If cancelled or failed, remove from list
      if (isCancelled || isFailed) {
        console.log(`Removing order ${update.orderId} (status: ${update.status})`);
        return prevOrders.filter(o => o.orderId !== update.orderId);
      }
      
      const orderIndex = prevOrders.findIndex(o => o.orderId === update.orderId);
      
      if (orderIndex !== -1) {
        // Update existing order
        const updatedOrders = [...prevOrders];
        updatedOrders[orderIndex] = {
          ...updatedOrders[orderIndex],
          status: update.status,
        };
        console.log(`Updated order ${update.orderId} status to ${update.status}`);
        return updatedOrders;
      } else {
        // Add new order if it's not already in the list
        console.log(`Adding new order ${update.orderId}`);
        const newOrder: ActiveOrder = {
          orderId: update.orderId,
          symbol: update.symbol,
          side: (update.orderType === 'BUY' || update.orderType === 'SELL') ? update.orderType : 'BUY',
          originalQty: update.quantity,
          price: update.price,
          status: update.status,
          createdAt: update.createdAt,
        };
        return [newOrder, ...prevOrders];
      }
    });
  }, [toast]);

  useWebSocket(handleTransactionUpdate, user?.email, handleOrderStatusUpdate);

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
