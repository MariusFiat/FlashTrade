/**
 * Currency utilities for formatting and displaying monetary values
 */

export type CurrencyCode = 'Dollar' | 'Euro' | 'Pound' | 'Yen';

interface CurrencyConfig {
  symbol: string;
  code: string;
  locale: string;
}

const CURRENCY_MAP: Record<CurrencyCode, CurrencyConfig> = {
  Dollar: {
    symbol: '$',
    code: 'USD',
    locale: 'en-US',
  },
  Euro: {
    symbol: '€',
    code: 'EUR',
    locale: 'en-GB',
  },
  Pound: {
    symbol: '£',
    code: 'GBP',
    locale: 'en-GB',
  },
  Yen: {
    symbol: '¥',
    code: 'JPY',
    locale: 'ja-JP',
  },
};

const DEFAULT_CURRENCY: CurrencyCode = 'Dollar';

/**
 * Get currency configuration
 */
export function getCurrencyConfig(currency?: string): CurrencyConfig {
  if (!currency || !(currency in CURRENCY_MAP)) {
    return CURRENCY_MAP[DEFAULT_CURRENCY];
  }
  return CURRENCY_MAP[currency as CurrencyCode];
}

/**
 * Get currency symbol
 */
export function getCurrencySymbol(currency?: string): string {
  return getCurrencyConfig(currency).symbol;
}

/**
 * Format amount with currency symbol
 */
export function formatCurrency(
  amount: number,
  currency?: string,
  options?: {
    showSymbol?: boolean;
    minimumFractionDigits?: number;
    maximumFractionDigits?: number;
  }
): string {
  const {
    showSymbol = true,
    minimumFractionDigits = 2,
    maximumFractionDigits = 2,
  } = options || {};

  const config = getCurrencyConfig(currency);
  const formatted = amount.toLocaleString(config.locale, {
    minimumFractionDigits,
    maximumFractionDigits,
  });

  return showSymbol ? `${config.symbol}${formatted}` : formatted;
}

/**
 * Format amount with sign prefix (+ or -)
 */
export function formatCurrencyWithSign(
  amount: number,
  currency?: string,
  options?: {
    minimumFractionDigits?: number;
    maximumFractionDigits?: number;
  }
): string {
  const sign = amount >= 0 ? '+' : '';
  return `${sign}${formatCurrency(amount, currency, options)}`;
}
