export interface PlaceOrderRequest {
  userId: string;
  symbol: string;
  side: 'BUY' | 'SELL';
  quantity: number;
  price?: number;
}

export interface PlaceOrderResponse {
  message: string;
  correlationId: string;
}

export interface CancelOrderRequest {
  orderId: number;
  userId: string;
}

export interface CancelOrderResponse {
  message: string;
  correlationId: string;
}

export interface ActiveOrder {
  orderId: number;
  userId: string;
  symbol: string;
  side: 'BUY' | 'SELL';
  quantity: number;
  price: number;
  status: string;
  timestamp: string;
}
