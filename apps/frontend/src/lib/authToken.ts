// 토큰 쿠키도 함께 관리(SSR 대비). 운영에선 HttpOnly 서버세션이 이상적이지만, 여기선 FE만으로 대체.
export function setAuthCookie(token: string | null) {
  if (typeof document === "undefined") return;
  if (!token) {
    document.cookie = "auth_token=; Max-Age=0; path=/";
    return;
  }
  // 7일
  document.cookie = `auth_token=${encodeURIComponent(token)}; Max-Age=${7 * 24 * 60 * 60}; path=/`;
}

export function getAuthCookie(): string | null {
  if (typeof document === "undefined") return null;
  const m = document.cookie.match(/(?:^|; )auth_token=([^;]+)/);
  return m ? decodeURIComponent(m[1]) : null;
}

// 아주 가벼운 JWT payload 디코더(선택)
export function decodeJwtPayload<T = any>(token?: string | null): T | null {
  if (!token) return null;
  try {
    const payload = token.split(".")[1];
    const json = atob(
      payload
        .replace(/-/g, "+")
        .replace(/_/g, "/")
        .padEnd(Math.ceil(payload.length / 4) * 4, "=")
    );
    return JSON.parse(json) as T;
  } catch {
    return null;
  }
}
