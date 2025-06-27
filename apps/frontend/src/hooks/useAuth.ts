import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuthStore } from "@/store/authStore";
import { LoginData, RegisterData, LoginResponse } from "@/types";

// API 함수들
const loginApi = async (loginData: LoginData): Promise<LoginResponse> => {
  await new Promise((resolve) => setTimeout(resolve, 1000));

  // 실제 API 호출:
  // const response = await fetch('/api/auth/login', {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify(loginData)
  // });
  // if (!response.ok) throw new Error('Login failed');
  // return response.json();

  // 모의 로그인 응답
  if (
    loginData.email === "test@example.com" &&
    loginData.password === "password"
  ) {
    return {
      user: {
        id: "1",
        name: "홍길동",
        email: loginData.email,
        avatar: "/avatar.jpg",
        role: "user",
      },
      token: "mock-jwt-token-" + Date.now(),
    };
  }

  throw new Error("잘못된 이메일 또는 비밀번호입니다.");
};

const registerApi = async (
  registerData: RegisterData
): Promise<LoginResponse> => {
  await new Promise((resolve) => setTimeout(resolve, 1200));

  // 실제 API 호출:
  // const response = await fetch('/api/auth/register', {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify(registerData)
  // });
  // if (!response.ok) throw new Error('Registration failed');
  // return response.json();

  // 모의 회원가입 응답
  return {
    user: {
      id: "1",
      name: registerData.name,
      email: registerData.email,
      role: "user",
    },
    token: "mock-jwt-token-" + Date.now(),
  };
};

const logoutApi = async (): Promise<void> => {
  await new Promise((resolve) => setTimeout(resolve, 500));

  // 실제 API 호출:
  // const response = await fetch('/api/auth/logout', {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' }
  // });
  // if (!response.ok) throw new Error('Logout failed');
};

// React Query hooks
export const useLogin = () => {
  const { setUser, setToken } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: loginApi,
    onSuccess: (data) => {
      setUser(data.user);
      setToken(data.token);

      // 사용자 관련 쿼리들 무효화
      queryClient.invalidateQueries({ queryKey: ["user"] });
      queryClient.invalidateQueries({ queryKey: ["cart"] });
      queryClient.invalidateQueries({ queryKey: ["favorites"] });
    },
    onError: (error) => {
      console.error("Login failed:", error);
    },
  });
};

export const useRegister = () => {
  const { setUser, setToken } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: registerApi,
    onSuccess: (data) => {
      setUser(data.user);
      setToken(data.token);

      // 사용자 관련 쿼리들 무효화
      queryClient.invalidateQueries({ queryKey: ["user"] });
    },
    onError: (error) => {
      console.error("Registration failed:", error);
    },
  });
};

export const useLogout = () => {
  const { clearAuth } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: logoutApi,
    onSuccess: () => {
      clearAuth();

      // 모든 쿼리 캐시 클리어
      queryClient.clear();
    },
    onError: (error) => {
      console.error("Logout failed:", error);
      // 로그아웃 실패해도 로컬 상태는 클리어
      clearAuth();
      queryClient.clear();
    },
  });
};
