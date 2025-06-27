import { create } from "zustand";
import { persist } from "zustand/middleware";
import { User, AuthStore } from "@/types";

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      // State
      user: null,
      isAuthenticated: false,
      isLoading: false,
      token: null,

      // Actions
      login: async (email: string) => {
        set({ isLoading: true });
        try {
          // 실제 API 호출 대신 모의 로그인
          await new Promise((resolve) => setTimeout(resolve, 1000));

          const mockUser: User = {
            id: "1",
            email,
            name: "홍길동",
            avatar: "/avatar.jpg",
            role: "user",
          };

          const mockToken = "mock-jwt-token";

          set({
            user: mockUser,
            token: mockToken,
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      register: async (email: string, password: string, name: string) => {
        set({ isLoading: true });
        try {
          // 실제 API 호출 대신 모의 회원가입
          await new Promise((resolve) => setTimeout(resolve, 1000));

          const mockUser: User = {
            id: "1",
            email,
            name,
            role: "user",
          };

          const mockToken = "mock-jwt-token";

          set({
            user: mockUser,
            token: mockToken,
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      logout: () => {
        set({
          user: null,
          token: null,
          isAuthenticated: false,
          isLoading: false,
        });
      },

      setUser: (user: User) => {
        set({ user, isAuthenticated: true });
      },

      setToken: (token: string) => {
        set({ token });
      },

      setLoading: (isLoading: boolean) => {
        set({ isLoading });
      },

      clearAuth: () => {
        set({
          user: null,
          token: null,
          isAuthenticated: false,
          isLoading: false,
        });
      },
    }),
    {
      name: "auth-storage",
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);
