import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '@/lib/authApi';
import { setCookie, getCookie, deleteCookie } from '@/lib/cookies';
import type { User, AuthContextType, LoginRequest, RegisterRequest } from '@/types/auth';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const TOKEN_COOKIE_NAME = 'access_token';

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Load token from cookie on mount
  useEffect(() => {
    const storedToken = getCookie(TOKEN_COOKIE_NAME);
    if (storedToken) {
      setToken(storedToken);
      // Decode JWT to get user info (basic decoding, no verification needed on client)
      try {
        const payload = JSON.parse(atob(storedToken.split('.')[1]));
        setUser({
          email: payload.sub,
          role: payload.role,
        });
      } catch (error) {
        // Invalid token, clear it
        deleteCookie(TOKEN_COOKIE_NAME);
      }
    }
    setIsLoading(false);
  }, []);

  const login = async (credentials: LoginRequest) => {
    const response = await authApi.login(credentials);
    const accessToken = response.access_token;
    
    // Store token in cookie (expires in 1 day - matching JWT expiration)
    setCookie(TOKEN_COOKIE_NAME, accessToken, { days: 1 });
    setToken(accessToken);

    // Decode and store user info
    const payload = JSON.parse(atob(accessToken.split('.')[1]));
    setUser({
      email: payload.sub,
      role: payload.role,
    });
  };

  const register = async (data: RegisterRequest) => {
    await authApi.register(data);
    // After registration, automatically log in
    await login({ email: data.email, password: data.password });
  };

  const logout = () => {
    deleteCookie(TOKEN_COOKIE_NAME);
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        login,
        register,
        logout,
        isAuthenticated: !!token,
        isLoading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
