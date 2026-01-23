import { useEffect } from 'react';
import { wsService, TransactionUpdate, OrderStatusUpdate } from '@/services/websocket';

export function useWebSocket(
  onTransactionUpdate: (data: TransactionUpdate) => void,
  userId?: string,
  onOrderStatusUpdate?: (data: OrderStatusUpdate) => void
) {
  useEffect(() => {
    wsService.connect(onTransactionUpdate, userId, onOrderStatusUpdate);

    return () => {
      // Don't disconnect on unmount as other components might be using it
    };
  }, [onTransactionUpdate, userId, onOrderStatusUpdate]);

  return {
    isConnected: wsService.getConnectionStatus(),
  };
}
