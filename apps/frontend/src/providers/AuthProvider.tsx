"use client";

import { createContext, useContext, useEffect } from "react";
import { useAuthStore } from "@/store/authStore";
import { AuthContextType, AuthProviderProps } from "@/types";

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: AuthProviderProps) {
  const {
    user,
    isAuthenticated,
    isLoading,
    token,
    login,
    logout,
    register,
    setLoading,
    clearAuth,
  } = useAuthStore();

  // 토큰 유효성 검사
  useEffect(() => {
    const validateToken = async () => {
      if (token && !isLoading) {
        try {
          setLoading(true);
          // 실제 환경에서는 토큰 검증 API 호출
          // const response = await fetch('/api/auth/validate', {
          //   headers: { Authorization: `Bearer ${token}` }
          // });
          // if (!response.ok) {
          //   clearAuth();
          // }

          // 모의 토큰 검증 (3초 후 완료)
          await new Promise((resolve) => setTimeout(resolve, 1000));
        } catch (error) {
          console.error("Token validation failed:", error);
          clearAuth();
        } finally {
          setLoading(false);
        }
      }
    };

    validateToken();
  }, [token, isLoading, setLoading, clearAuth]);

  const contextValue: AuthContextType = {
    user,
    isAuthenticated,
    isLoading,
    login,
    logout,
    register,
  };

  return (
    <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
