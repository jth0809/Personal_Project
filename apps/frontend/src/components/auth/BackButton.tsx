import Link from "next/link";
import { ArrowLeft } from "lucide-react";

export default function BackButton() {
  return (
    <div className="absolute top-6 left-6 z-50">
      <Link
        href="/"
        className="flex items-center space-x-2 text-white/80 hover:text-white transition-colors group"
      >
        <ArrowLeft className="h-5 w-5 group-hover:-translate-x-1 transition-transform" />
        <span className="hidden sm:block">홈으로 돌아가기</span>
      </Link>
    </div>
  );
}
