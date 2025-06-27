import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { QueryProvider } from "@/providers/QueryProvider";
import { AuthProvider } from "@/providers/AuthProvider";
import MainLayout from "@/components/Layout/MainLayout";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "GOODSSHOP - 특별한 굿즈를 만나보세요",
  description:
    "일상을 더 특별하게 만들어줄 다양한 굿즈들을 만나보세요. 가방, 생활용품, 액세서리, 의류 등 고품질 상품을 합리적인 가격에 제공합니다.",
  keywords: "굿즈, 쇼핑몰, 가방, 생활용품, 액세서리, 의류, 인테리어, 문구",
  authors: [{ name: "GOODSSHOP" }],
  creator: "GOODSSHOP",
  publisher: "GOODSSHOP",
  openGraph: {
    title: "GOODSSHOP - 특별한 굿즈를 만나보세요",
    description: "일상을 더 특별하게 만들어줄 다양한 굿즈들을 만나보세요.",
    type: "website",
    locale: "ko_KR",
  },
  twitter: {
    card: "summary_large_image",
    title: "GOODSSHOP - 특별한 굿즈를 만나보세요",
    description: "일상을 더 특별하게 만들어줄 다양한 굿즈들을 만나보세요.",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased bg-gray-50`}
      >
        <QueryProvider>
          <AuthProvider>
            <MainLayout>{children}</MainLayout>
          </AuthProvider>
        </QueryProvider>
      </body>
    </html>
  );
}
