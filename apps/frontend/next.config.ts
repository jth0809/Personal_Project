import type { NextConfig } from "next";

const securityHeaders = [
  { key: "X-Frame-Options", value: "SAMEORIGIN" },
  { key: "Referrer-Policy", value: "strict-origin-when-cross-origin" },
  { key: "Permissions-Policy", value: "geolocation=(self)" },
  {
    key: "Content-Security-Policy-Report-Only",
    value:
      "default-src 'self'; img-src 'self' data: https:; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline' https:; connect-src 'self' https:",
  },
];

const nextConfig: NextConfig = {
  output: "standalone",
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: process.env.BACKEND_ORIGIN
          ? `${process.env.BACKEND_ORIGIN}/:path*`
          : "http://localhost:8080/:path*",
      },
    ];
  },
  async headers() {
    return [{ source: "/(.*)", headers: securityHeaders }];
  },
  images: {
    remotePatterns: [
      // 예: Oracle OCI Object Storage
      { protocol: "https", hostname: "**.objectstorage.**.oraclecloud.com" },
      // 예: CloudFront/CDN
      { protocol: "https", hostname: "cdn.example.com" },
      // 필요 시 개별 호스트 추가
      {
        protocol: "https",
        hostname: "objectstorage.ap-chuncheon-1.oraclecloud.com",
      },
    ],
  },
};

export default nextConfig;
