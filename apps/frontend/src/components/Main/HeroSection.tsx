import { Button } from "@/components/ui/button";

export default function HeroSection() {
  return (
    <section className="bg-gradient-to-r from-blue-600 to-purple-600 text-white py-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 className="text-4xl md:text-6xl font-bold mb-4">
          특별한 굿즈를 만나보세요
        </h2>
        <p className="text-xl md:text-2xl mb-8 opacity-90">
          일상을 더 특별하게 만들어줄 다양한 굿즈들
        </p>
        <Button size="lg" className="bg-white text-blue-600 hover:bg-gray-100">
          쇼핑 시작하기
        </Button>
      </div>
    </section>
  );
}
