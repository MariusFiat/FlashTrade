/**
 * Cookie utilities for managing authentication tokens
 */

interface CookieOptions {
  days?: number;
  path?: string;
  secure?: boolean;
  sameSite?: 'Strict' | 'Lax' | 'None';
}

/**
 * Set a cookie
 */
export function setCookie(name: string, value: string, options: CookieOptions = {}): void {
  const {
    days = 7,
    path = '/',
    secure = window.location.protocol === 'https:',
    sameSite = 'Lax'
  } = options;

  let cookieString = `${encodeURIComponent(name)}=${encodeURIComponent(value)}`;
  
  if (days) {
    const date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    cookieString += `; expires=${date.toUTCString()}`;
  }
  
  cookieString += `; path=${path}`;
  
  if (secure) {
    cookieString += '; secure';
  }
  
  cookieString += `; SameSite=${sameSite}`;
  
  document.cookie = cookieString;
}

/**
 * Get a cookie value by name
 */
export function getCookie(name: string): string | null {
  const nameEQ = encodeURIComponent(name) + '=';
  const cookies = document.cookie.split(';');
  
  for (let i = 0; i < cookies.length; i++) {
    let cookie = cookies[i];
    while (cookie.charAt(0) === ' ') {
      cookie = cookie.substring(1);
    }
    if (cookie.indexOf(nameEQ) === 0) {
      return decodeURIComponent(cookie.substring(nameEQ.length));
    }
  }
  
  return null;
}

/**
 * Delete a cookie
 */
export function deleteCookie(name: string, path: string = '/'): void {
  setCookie(name, '', { days: -1, path });
}

/**
 * Check if a cookie exists
 */
export function hasCookie(name: string): boolean {
  return getCookie(name) !== null;
}
