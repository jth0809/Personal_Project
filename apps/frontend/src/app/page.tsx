import HeroSection from "@/components/Main/HeroSection";
import ProductsSection from "@/components/Main/ProductsSection";
import CategoriesSection from "@/components/Main/CategoriesSection";
import FeaturedProducts from "@/components/Main/FeaturedProducts";
import BrandStorySection from "@/components/Main/BrandStorySection";
import TestimonialsSection from "@/components/Main/TestimonialsSection";
import NewsletterSection from "@/components/Main/NewsletterSection";

export default function Home() {
  return (
    <div>
      <HeroSection />
      <CategoriesSection />
      <FeaturedProducts />
      <ProductsSection />
      <BrandStorySection />
      <TestimonialsSection />
      <NewsletterSection />
    </div>
  );
}
