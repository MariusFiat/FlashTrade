import { useEffect } from 'react';
import { wsService, TransactionUpdate } from '@/services/websocket';

export function useWebSocket(onTransactionUpdate: (data: TransactionUpdate) => void) {
  useEffect(() => {
    wsService.connect(onTransactionUpdate);

    return () => {
      wsService.disconnect();
    };
  }, [onTransactionUpdate]);

  return {
    isConnected: wsService.getConnectionStatus(),
  };
}
