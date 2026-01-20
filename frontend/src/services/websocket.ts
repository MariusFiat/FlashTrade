import { Client } from '@stomp/stompjs';

export interface TransactionUpdate {
  orderId: string;
  userId: string;
  symbol: string;
  side: string;
  quantity: number;
  price: number;
  status: string;
  timestamp: string;
}

export interface DashboardUpdate {
  totalValue: number;
  totalGain: number;
  percentageGain: number;
  holdings: Record<string, number>;
}

class WebSocketService {
  private client: Client | null = null;
  private isConnected = false;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;

  connect(onTransactionUpdate: (data: TransactionUpdate) => void) {
    if (this.isConnected) return;

    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('WebSocket connected');
        this.isConnected = true;
        this.reconnectAttempts = 0;

        this.client?.subscribe('/topic/transactions', (message) => {
          const data = JSON.parse(message.body);
          onTransactionUpdate(data);
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
        this.isConnected = false;
        this.attemptReconnect(onTransactionUpdate);
      },
      onWebSocketClose: () => {
        console.log('WebSocket closed');
        this.isConnected = false;
        this.attemptReconnect(onTransactionUpdate);
      },
    });

    this.client.activate();
  }

  private attemptReconnect(onTransactionUpdate: (data: TransactionUpdate) => void) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      setTimeout(() => this.connect(onTransactionUpdate), 5000);
    }
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.isConnected = false;
      this.client = null;
    }
  }

  getConnectionStatus() {
    return this.isConnected;
  }
}

export const wsService = new WebSocketService();
