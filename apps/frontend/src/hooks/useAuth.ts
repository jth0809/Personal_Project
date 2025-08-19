import { useMutation } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import { TokenResponse } from "@/types/backend";
import { useAuthStore } from "@/store/authStore";
import { setAuthCookie } from "@/lib/authToken";

export function useLogin() {
  const setToken = useAuthStore((s) => s.setToken);
  return useMutation({
    mutationFn: (data: { email: string; password: string }) =>
      apiFetch<TokenResponse>(`/auth/login`, {
        method: "POST",
        body: JSON.stringify(data),
      }),
    onSuccess: (res) => {
      setToken(res.accessToken);
      setAuthCookie(res.accessToken);
    },
  });
}

export function useSignup() {
  return useMutation({
    mutationFn: (data: { email: string; password: string; name: string }) =>
      apiFetch<void>(`/auth/signup`, {
        method: "POST",
        body: JSON.stringify(data),
      }),
  });
}
