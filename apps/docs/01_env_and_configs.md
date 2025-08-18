# 01_env_and_configs.md

## 목적

환경변수와 빌드/실행 설정, 프록시(리라이트) 정책을 정의한다.

## 프론트엔드(.env)

- `NEXT_PUBLIC_API_BASE_URL` = `http://localhost:8080` (직접 호출 모드 시)
- `NEXT_PUBLIC_FRONTEND_ORIGIN` = `http://localhost:3000` (백엔드 CORS에 사용할 경우)

## 프론트엔드 next.config.ts (프록시 추천)

> `/api/*` → 백엔드로 전달. 프론트 코드에서는 `/api/...`만 호출하면 됨.

```ts
// apps/frontend/next.config.ts
import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: process.env.BACKEND_ORIGIN
          ? `${process.env.BACKEND_ORIGIN}/:path*`
          : "http://localhost:8080/:path*", // dev default
      },
    ];
  },
};

export default nextConfig;
```
