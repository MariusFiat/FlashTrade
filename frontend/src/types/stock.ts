export interface StockHistoryPoint {
  price: number;
  timestamp: string;
}

export interface StockPerformanceResponse {
  symbol: string;
  history: StockHistoryPoint[];
  currentPrice: number;
  correlationId: string;
}

export interface Stock {
  symbol: string;
  name: string;
  price: number;
  change: number;
}

export interface MarketDataResponse {
  stocks: Stock[];
}

export interface TradeRecord {
  tradeId: number;
  symbol: string;
  quantity: number;
  price: number;
  side: 'BUY' | 'SELL';
  timestamp: string;
}

export interface TradeHistoryResponse {
  userId: string;
  trades: TradeRecord[];
}
