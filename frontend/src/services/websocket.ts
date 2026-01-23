import { Client } from '@stomp/stompjs';

const WS_URL = import.meta.env.VITE_GATEWAY_WS_URL || 'ws://localhost:8080/ws';

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
  
export interface OrderStatusUpdate {
  orderId: number;
  symbol: string;
  quantity: number;
  price: number;
  orderType: string;
  userId: string;
  createdAt: string;
  correlationId: string;
  status: string;
  errorMessage?: string;
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
  private orderStatusCallback: ((data: OrderStatusUpdate) => void) | null = null;
  private transactionCallback: ((data: TransactionUpdate) => void) | null = null;
  private currentUserId: string | null = null;

  connect(onTransactionUpdate: (data: TransactionUpdate) => void, userId?: string, onOrderStatusUpdate?: (data: OrderStatusUpdate) => void) {
    if (this.isConnected && this.currentUserId === userId) return;

    this.transactionCallback = onTransactionUpdate;
    this.orderStatusCallback = onOrderStatusUpdate || null;
    this.currentUserId = userId || null;

    this.client = new Client({
      brokerURL: WS_URL,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('WebSocket connected');
        this.isConnected = true;
        this.reconnectAttempts = 0;

        // Subscribe to general transaction updates
        this.client?.subscribe('/topic/transactions', (message) => {
          console.log('Received transaction update:', message.body);
          const data = JSON.parse(message.body);
          if (this.transactionCallback) {
            this.transactionCallback(data);
          }
        });

        // Subscribe to user-specific order status updates
        if (this.currentUserId && this.orderStatusCallback) {
          this.client?.subscribe(`/topic/orders/${this.currentUserId}`, (message) => {
            console.log('Received order status update:', message.body);
            const data = JSON.parse(message.body);
            if (this.orderStatusCallback) {
              this.orderStatusCallback(data);
            }
          });
          console.log(`Subscribed to /topic/orders/${this.currentUserId}`);
        }
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
        this.isConnected = false;
        this.attemptReconnect();
      },
      onWebSocketClose: () => {
        console.log('WebSocket closed');
        this.isConnected = false;
        this.attemptReconnect();
      },
      onWebSocketError: (error) => {
        console.error('WebSocket error:', error);
      },
    });

    this.client.activate();
  }

  private attemptReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      setTimeout(() => {
        if (this.transactionCallback) {
          this.connect(this.transactionCallback, this.currentUserId || undefined, this.orderStatusCallback || undefined);
        }
      }, 5000);
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
