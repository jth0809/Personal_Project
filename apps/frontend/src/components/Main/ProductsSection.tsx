"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { sampleProducts, getProductCountByType } from "@/contents/products";
import ProductCard from "./ProductCard";
import { Sparkles, Filter, ArrowRight } from "lucide-react";
import { useState, useMemo } from "react";
import { Product } from "@/types";
import { motion, AnimatePresence } from "framer-motion";

// 상품 타입 정의
type ProductType = "all" | "best" | "new" | "sale";

// 상품에 타입 추가 (tags 기반으로 매핑)
const getProductType = (product: Product): ProductType => {
  if (product.tags?.includes("베스트셀러")) return "best";
  if (product.tags?.includes("신상품")) return "new";
  if (product.originalPrice && product.originalPrice > product.price)
    return "sale";
  return "all";
};

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      delayChildren: 0.4,
      staggerChildren: 0.15,
      duration: 0.8,
      ease: [0.25, 0.46, 0.45, 0.94] as const,
    },
  },
};

const cardVariants = {
  hidden: { y: 60, opacity: 0, scale: 0.96 },
  visible: {
    y: 0,
    opacity: 1,
    scale: 1,
    transition: {
      type: "spring" as const,
      stiffness: 80,
      damping: 20,
      duration: 0.7,
    },
  },
  exit: {
    y: -30,
    opacity: 0,
    scale: 0.96,
    transition: {
      duration: 0.3,
      ease: [0.4, 0, 0.2, 1] as const,
    },
  },
};

export default function ProductsSection() {
  const [selectedType, setSelectedType] = useState<ProductType>("all");

  const handleAddToCart = (productId: string) => {
    console.log("장바구니에 추가:", productId);
    // 장바구니 추가 로직 구현
  };

  const handleAddToWishlist = (productId: string) => {
    console.log("찜 목록에 추가:", productId);
    // 찜 목록 추가 로직 구현
  };

  // 상품 필터링 (6개로 제한)
  const filteredProducts = useMemo(() => {
    let filtered = sampleProducts;

    if (selectedType !== "all") {
      filtered = sampleProducts.filter((product) => {
        const productType = getProductType(product);
        return productType === selectedType;
      });
    }

    // 6개로 제한
    return filtered.slice(0, 6);
  }, [selectedType]);

  // 필터 버튼 데이터 (헬퍼 함수 사용)
  const productCounts = getProductCountByType();
  const filterButtons = [
    {
      type: "all" as ProductType,
      label: "전체",
      count: Math.min(productCounts.all, 6),
    },
    {
      type: "best" as ProductType,
      label: "베스트셀러",
      count: Math.min(productCounts.best, 6),
    },
    {
      type: "new" as ProductType,
      label: "신상품",
      count: Math.min(productCounts.new, 6),
    },
    {
      type: "sale" as ProductType,
      label: "할인상품",
      count: Math.min(productCounts.sale, 6),
    },
  ];

  return (
    <section className="py-20 bg-gradient-to-br from-slate-50 to-blue-50 relative overflow-hidden">
      {/* Background Decoration */}
      <div className="absolute inset-0">
        <div className="absolute top-20 right-20 w-64 h-64 bg-blue-200/20 rounded-full blur-3xl"></div>
        <div className="absolute bottom-20 left-20 w-48 h-48 bg-cyan-200/20 rounded-full blur-3xl"></div>
      </div>

      <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Header */}
        <motion.div
          className="text-center mb-12"
          initial={{ opacity: 0, y: 40 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{
            duration: 0.8,
            ease: [0.25, 0.46, 0.45, 0.94] as const,
          }}
        >
          <motion.div
            className="inline-flex items-center px-4 py-2 rounded-full bg-blue-100 border border-blue-200 mb-6"
            initial={{ scale: 0, rotate: -5 }}
            whileInView={{ scale: 1, rotate: 0 }}
            viewport={{ once: true }}
            transition={{
              delay: 0.3,
              type: "spring",
              stiffness: 200,
              damping: 20,
              duration: 0.6,
            }}
          >
            <Sparkles className="h-4 w-4 text-blue-600 mr-2" />
            <span className="text-blue-800 text-sm font-medium">
              Popular Items
            </span>
          </motion.div>

          <motion.h2
            className="text-4xl md:text-5xl font-bold mb-4"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{
              delay: 0.4,
              duration: 0.7,
              ease: [0.25, 0.46, 0.45, 0.94] as const,
            }}
          >
            <span className="text-slate-900">지금 </span>
            <span className="bg-gradient-to-r from-blue-600 to-cyan-600 bg-clip-text text-transparent">
              인기있는
            </span>
            <span className="text-slate-900"> 상품들</span>
          </motion.h2>

          <motion.p
            className="text-xl text-slate-600 max-w-2xl mx-auto"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{
              delay: 0.5,
              duration: 0.7,
              ease: [0.25, 0.46, 0.45, 0.94] as const,
            }}
          >
            고객들이 선택한 인기 상품들을 만나보세요
          </motion.p>
        </motion.div>

        {/* Filter Buttons */}
        <motion.div
          className="flex flex-wrap justify-center gap-3 mb-12"
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{
            delay: 0.6,
            duration: 0.8,
            ease: [0.25, 0.46, 0.45, 0.94] as const,
          }}
        >
          {filterButtons.map((button, index) => (
            <motion.button
              key={button.type}
              onClick={() => setSelectedType(button.type)}
              className={`relative inline-flex items-center gap-2 px-6 py-3 rounded-full font-medium transition-all duration-300 ${
                selectedType === button.type
                  ? "text-white shadow-lg"
                  : "text-gray-600 bg-white border border-gray-200 hover:border-gray-300"
              }`}
              initial={{ opacity: 0, y: 20, scale: 0.9 }}
              whileInView={{ opacity: 1, y: 0, scale: 1 }}
              viewport={{ once: true }}
              transition={{
                delay: 0.6 + index * 0.1,
                duration: 0.5,
                ease: [0.25, 0.46, 0.45, 0.94] as const,
              }}
              whileHover={{
                scale: 1.03,
                boxShadow: "0 8px 25px -8px rgba(0, 0, 0, 0.15)",
              }}
              whileTap={{ scale: 0.98 }}
            >
              {selectedType === button.type && (
                <motion.div
                  className={`absolute inset-0 rounded-full ${
                    button.type === "best"
                      ? "bg-gradient-to-r from-yellow-400 to-orange-400"
                      : button.type === "new"
                        ? "bg-gradient-to-r from-green-400 to-emerald-400"
                        : button.type === "sale"
                          ? "bg-gradient-to-r from-red-400 to-pink-400"
                          : "bg-gradient-to-r from-blue-500 to-cyan-500"
                  }`}
                  layoutId="activeFilter"
                  transition={{
                    type: "spring",
                    stiffness: 200,
                    damping: 25,
                    duration: 0.6,
                  }}
                />
              )}
              <span className="relative z-10 flex items-center gap-2">
                {button.type === "all" && <Filter className="h-4 w-4" />}
                {button.label}
                <span className="ml-2 text-xs bg-white/20 px-2 py-0.5 rounded-full">
                  {button.count}
                </span>
              </span>
            </motion.button>
          ))}
        </motion.div>

        {/* Products Grid */}
        <motion.div
          className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mb-12 min-h-[400px]"
          variants={containerVariants}
          initial="hidden"
          animate="visible"
        >
          <AnimatePresence mode="wait">
            {filteredProducts.length > 0 ? (
              filteredProducts.map((product, _index) => (
                <motion.div
                  key={`${selectedType}-${product.id}`}
                  variants={cardVariants}
                  layout
                >
                  <ProductCard
                    product={product}
                    onAddToCart={handleAddToCart}
                    onAddToWishlist={handleAddToWishlist}
                  />
                </motion.div>
              ))
            ) : (
              <motion.div
                className="col-span-full text-center py-12"
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -30 }}
                transition={{
                  duration: 0.5,
                  ease: [0.25, 0.46, 0.45, 0.94] as const,
                }}
              >
                <div className="text-gray-500 text-lg">
                  해당 카테고리에 상품이 없습니다.
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </motion.div>

        {/* View All Button */}
        <motion.div
          className="text-center"
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{
            delay: 0.8,
            duration: 0.6,
            ease: [0.25, 0.46, 0.45, 0.94] as const,
          }}
        >
          <Link href="/products">
            <motion.div
              whileHover={{
                scale: 1.03,
                boxShadow: "0 15px 35px -5px rgba(59, 130, 246, 0.4)",
              }}
              whileTap={{ scale: 0.98 }}
              transition={{
                type: "spring",
                stiffness: 300,
                damping: 20,
              }}
            >
              <Button
                size="lg"
                className="bg-gradient-to-r from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700 text-white px-8 py-4 rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 group"
              >
                모든 상품 보기
                <motion.div
                  className="ml-2"
                  animate={{ x: [0, 4, 0] }}
                  transition={{
                    repeat: Infinity,
                    duration: 2,
                    ease: "easeInOut",
                  }}
                >
                  <ArrowRight className="h-5 w-5" />
                </motion.div>
              </Button>
            </motion.div>
          </Link>
        </motion.div>
      </div>
    </section>
  );
}
