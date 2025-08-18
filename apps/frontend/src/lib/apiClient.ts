// src/lib/apiClient.ts
import { getAuthCookie } from "./authToken";

async function readBodyAsText(res: Response): Promise<string> {
  try {
    const t = await res.text();
    return t ?? "";
  } catch {
    return "";
  }
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const token = getAuthCookie();

  const headers: HeadersInit = {
    // FormData가 아닌 경우에만 JSON 헤더
    ...(options.body instanceof FormData
      ? {}
      : { "Content-Type": "application/json" }),
    ...(options.headers || {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };

  const url = path.startsWith("/api") ? path : `/api${path}`;
  const res = await fetch(url, { ...options, headers });

  if (!res.ok) {
    const preview = await readBodyAsText(res);
    throw new Error(preview || `HTTP ${res.status}`);
  }

  // 204 No Content 등: 본문이 없는 성공 응답
  if (res.status === 204) {
    return undefined as T;
  }

  // 본문은 한 번만 읽는다
  const raw = await readBodyAsText(res);
  if (!raw) {
    // 200/201 이지만 빈 바디인 경우
    return undefined as T;
  }

  const ct = res.headers.get("content-type") || "";
  if (ct.includes("application/json")) {
    try {
      return JSON.parse(raw) as T;
    } catch (e) {
      console.error("[apiFetch] JSON parse failed. Raw response:", raw);
      throw e;
    }
  }

  // JSON이 아니면 text 그대로 반환 (필요 시 호출부 제네릭을 string으로)
  return raw as unknown as T;
}
