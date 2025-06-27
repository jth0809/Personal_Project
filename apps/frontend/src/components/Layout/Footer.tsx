import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import {
  Facebook,
  Instagram,
  Youtube,
  Mail,
  Phone,
  MapPin,
} from "lucide-react";

export default function Footer() {
  const customerService = [
    { name: "자주 묻는 질문", href: "/faq" },
    { name: "배송 정보", href: "/shipping" },
    { name: "교환/환불", href: "/returns" },
    { name: "고객 센터", href: "/support" },
    { name: "1:1 문의", href: "/contact" },
  ];

  const companyInfo = [
    { name: "회사 소개", href: "/about" },
    { name: "채용 정보", href: "/careers" },
    { name: "제휴 문의", href: "/partnership" },
    { name: "공지사항", href: "/notices" },
    { name: "이벤트", href: "/events" },
  ];

  const policies = [
    { name: "이용약관", href: "/terms" },
    { name: "개인정보처리방침", href: "/privacy" },
    { name: "쿠키 정책", href: "/cookies" },
    { name: "운영정책", href: "/policy" },
  ];

  const categories = [
    { name: "가방", href: "/products?category=bags" },
    { name: "생활용품", href: "/products?category=lifestyle" },
    { name: "액세서리", href: "/products?category=accessories" },
    { name: "의류", href: "/products?category=clothing" },
    { name: "문구", href: "/products?category=stationery" },
    { name: "인테리어", href: "/products?category=interior" },
  ];

  return (
    <footer className="bg-gray-900 text-white">
      {/* Newsletter Section */}
      <div className="border-b border-gray-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center">
            <h3 className="text-xl font-bold mb-2">뉴스레터 구독</h3>
            <p className="text-gray-400 mb-6">
              신상품 소식과 특별 할인 혜택을 가장 먼저 받아보세요
            </p>
            <div className="flex max-w-md mx-auto">
              <Input
                type="email"
                placeholder="이메일 주소를 입력하세요"
                className="rounded-r-none bg-gray-800 border-gray-700 text-white placeholder-gray-400"
              />
              <Button className="rounded-l-none bg-blue-600 hover:bg-blue-700">
                구독하기
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Main Footer Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-8">
          {/* Company Info */}
          <div className="lg:col-span-2">
            <Link
              href="/"
              className="text-2xl font-bold text-white hover:text-blue-400 transition-colors"
            >
              GOODSSHOP
            </Link>
            <p className="text-gray-400 mt-4 mb-6">
              특별한 굿즈로 여러분의 일상을 더욱 특별하게 만들어드립니다.
              고품질의 상품과 최고의 서비스를 제공하겠습니다.
            </p>

            <div className="space-y-2">
              <div className="flex items-center space-x-2 text-sm text-gray-400">
                <MapPin className="h-4 w-4" />
                <span>서울시 강남구 테헤란로 123, 굿즈샵 빌딩</span>
              </div>
              <div className="flex items-center space-x-2 text-sm text-gray-400">
                <Phone className="h-4 w-4" />
                <span>1588-1234</span>
              </div>
              <div className="flex items-center space-x-2 text-sm text-gray-400">
                <Mail className="h-4 w-4" />
                <span>support@goodsshop.com</span>
              </div>
            </div>

            <div className="flex space-x-4 mt-6">
              <Button
                variant="ghost"
                size="icon"
                className="text-gray-400 hover:text-white"
              >
                <Instagram className="h-5 w-5" />
              </Button>
              <Button
                variant="ghost"
                size="icon"
                className="text-gray-400 hover:text-white"
              >
                <Facebook className="h-5 w-5" />
              </Button>
              <Button
                variant="ghost"
                size="icon"
                className="text-gray-400 hover:text-white"
              >
                <Youtube className="h-5 w-5" />
              </Button>
            </div>
          </div>

          {/* Customer Service */}
          <div>
            <h4 className="text-lg font-semibold mb-4">고객 서비스</h4>
            <ul className="space-y-2">
              {customerService.map((item) => (
                <li key={item.name}>
                  <Link
                    href={item.href}
                    className="text-gray-400 hover:text-white transition-colors text-sm"
                  >
                    {item.name}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Company */}
          <div>
            <h4 className="text-lg font-semibold mb-4">회사 정보</h4>
            <ul className="space-y-2">
              {companyInfo.map((item) => (
                <li key={item.name}>
                  <Link
                    href={item.href}
                    className="text-gray-400 hover:text-white transition-colors text-sm"
                  >
                    {item.name}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Categories */}
          <div>
            <h4 className="text-lg font-semibold mb-4">카테고리</h4>
            <ul className="space-y-2">
              {categories.map((item) => (
                <li key={item.name}>
                  <Link
                    href={item.href}
                    className="text-gray-400 hover:text-white transition-colors text-sm"
                  >
                    {item.name}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        </div>

        <Separator className="my-8 bg-gray-800" />

        {/* Bottom Section */}
        <div className="flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0">
          <div className="flex flex-wrap gap-4 text-sm text-gray-400">
            {policies.map((policy, index) => (
              <div key={policy.name} className="flex items-center">
                <Link
                  href={policy.href}
                  className="hover:text-white transition-colors"
                >
                  {policy.name}
                </Link>
                {index < policies.length - 1 && (
                  <span className="mx-2 text-gray-600">|</span>
                )}
              </div>
            ))}
          </div>
          <p className="text-sm text-gray-400">
            &copy; 2024 GOODSSHOP. All rights reserved.
          </p>
        </div>

        {/* Business Info */}
        <div className="mt-6 pt-6 border-t border-gray-800">
          <div className="text-xs text-gray-500 space-y-1">
            <p>
              <strong>굿즈샵 주식회사</strong> | 대표: 홍길동 | 사업자등록번호:
              123-45-67890
            </p>
            <p>
              통신판매업신고: 제2024-서울강남-1234호 | 개인정보보호책임자:
              김철수
            </p>
            <p>
              호스팅서비스 제공: 굿즈샵 클라우드 | 이메일:
              business@goodsshop.com
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
}
