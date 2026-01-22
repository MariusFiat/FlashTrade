import type { ActiveOrder, PlaceOrderRequest, CancelOrderRequest, PlaceOrderResponse, CancelOrderResponse } from '@/types/order';

const API_BASE_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8080';

class OrderApiService {
  private baseURL: string;

  constructor() {
    this.baseURL = API_BASE_URL;
  }

  async placeOrder(orderData: PlaceOrderRequest): Promise<PlaceOrderResponse> {
    const response = await fetch(`${this.baseURL}/api/orders`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(orderData),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Failed to place order');
    }

    return response.json();
  }

  async cancelOrder(orderId: number, userId: string): Promise<CancelOrderResponse> {
    const response = await fetch(`${this.baseURL}/api/orders/${orderId}?userId=${userId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Failed to cancel order');
    }

    return response.json();
  }

  async getActiveOrders(userId: string): Promise<ActiveOrder[]> {
    const response = await fetch(`${this.baseURL}/api/orders/active/${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Failed to fetch active orders');
    }

    const data = await response.json();
    return data.orders || [];
  }
}

export const orderApi = new OrderApiService();
