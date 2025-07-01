"use client";

import { useRouter } from "next/navigation";
import { Star, ShoppingCart, Heart, Eye, Zap, Crown, Gift } from "lucide-react";
import { motion } from "framer-motion";
import { Product } from "@/types";

// 배지 아이콘 매핑
const badgeIcons = {
  베스트셀러: Crown,
  신상품: Zap,
  한정판: Gift,
  특가: Star,
  프리미엄: Crown,
  추천: Star,
  친환경: Gift,
  할인상품: Star,
  인기상품: Crown,
  클래식: Crown,
  선물용: Gift,
};

// 배지 색상 매핑
const badgeColors = {
  베스트셀러: "from-yellow-400 to-orange-500",
  신상품: "from-green-400 to-emerald-500",
  한정판: "from-purple-400 to-indigo-500",
  특가: "from-red-400 to-pink-500",
  프리미엄: "from-blue-400 to-cyan-500",
  추천: "from-indigo-400 to-purple-500",
  친환경: "from-green-400 to-teal-500",
  할인상품: "from-red-400 to-pink-500",
  인기상품: "from-yellow-400 to-orange-500",
  클래식: "from-gray-400 to-slate-500",
  선물용: "from-purple-400 to-pink-500",
};

interface ProductCardProps {
  product: Product;
  onAddToCart?: (productId: string) => void;
  onAddToWishlist?: (productId: string) => void;
  variant?: "default" | "featured";
  className?: string;
}

export default function ProductCard({
  product,
  onAddToCart,
  onAddToWishlist,
  variant: _variant = "default",
  className = "",
}: ProductCardProps) {
  const router = useRouter();
  const primaryTag = product.tags?.[0] || "추천";
  const BadgeIcon = badgeIcons[primaryTag as keyof typeof badgeIcons] || Star;
  const badgeColor =
    badgeColors[primaryTag as keyof typeof badgeColors] ||
    "from-blue-400 to-cyan-500";

  const calculateDiscount = (originalPrice?: number, currentPrice?: number) => {
    if (!originalPrice || !currentPrice) return 0;
    return Math.round((1 - currentPrice / originalPrice) * 100);
  };

  const discount = calculateDiscount(product.originalPrice, product.price);

  const handleCardClick = () => {
    router.push(`/product/${product.id}`);
  };

  return (
    <motion.div
      className={`group relative bg-white rounded-2xl shadow-lg overflow-hidden border border-gray-100 cursor-pointer ${className}`}
      whileHover={{
        y: -4,
        boxShadow: "0 20px 40px -8px rgba(0, 0, 0, 0.15)",
      }}
      transition={{
        type: "spring",
        stiffness: 200,
        damping: 25,
        duration: 0.6,
      }}
      onClick={handleCardClick}
    >
      {/* Image Container */}
      <div className="relative aspect-square overflow-hidden">
        <motion.img
          src={product.image}
          alt={product.name}
          className="w-full h-full object-cover"
          whileHover={{ scale: 1.05 }}
          transition={{
            duration: 0.7,
            ease: [0.4, 0, 0.2, 1],
          }}
        />

        {/* Badge */}
        <motion.div
          className={`absolute top-3 left-3 bg-gradient-to-r ${badgeColor} text-white px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-1 backdrop-blur-sm bg-opacity-90`}
          initial={{ x: -30, opacity: 0 }}
          whileInView={{ x: 0, opacity: 1 }}
          viewport={{ once: true }}
          transition={{
            delay: 0.2,
            duration: 0.6,
            ease: [0.25, 0.46, 0.45, 0.94],
          }}
        >
          <BadgeIcon className="w-3 h-3" />
          {primaryTag}
        </motion.div>

        {/* Discount Badge */}
        {discount > 0 && (
          <motion.div
            className="absolute top-3 right-3 bg-red-500 text-white px-2 py-1 rounded-full text-xs font-bold backdrop-blur-sm bg-opacity-95"
            initial={{ x: 30, opacity: 0 }}
            whileInView={{ x: 0, opacity: 1 }}
            viewport={{ once: true }}
            transition={{
              delay: 0.3,
              duration: 0.6,
              ease: [0.25, 0.46, 0.45, 0.94],
            }}
          >
            -{discount}%
          </motion.div>
        )}

        {/* Overlay Actions */}
        <motion.div
          className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 flex items-center justify-center gap-4"
          transition={{
            duration: 0.4,
            ease: [0.4, 0, 0.2, 1],
          }}
        >
          <motion.button
            className="bg-white/95 hover:bg-white text-gray-900 p-3 rounded-full shadow-xl border border-white/20"
            initial={{ scale: 0, rotate: -180 }}
            whileInView={{ scale: 1, rotate: 0 }}
            whileHover={{
              scale: 1.05,
              boxShadow: "0 10px 25px -5px rgba(0, 0, 0, 0.3)",
            }}
            whileTap={{ scale: 0.98 }}
            transition={{
              type: "spring",
              stiffness: 300,
              damping: 20,
              duration: 0.4,
            }}
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              onAddToCart?.(product.id);
            }}
          >
            <ShoppingCart className="w-4 h-4" />
          </motion.button>
          <motion.button
            className="bg-white/95 hover:bg-white text-gray-900 p-3 rounded-full shadow-xl border border-white/20"
            initial={{ scale: 0, rotate: -180 }}
            whileInView={{ scale: 1, rotate: 0 }}
            whileHover={{
              scale: 1.05,
              boxShadow: "0 10px 25px -5px rgba(0, 0, 0, 0.3)",
            }}
            whileTap={{ scale: 0.98 }}
            transition={{
              type: "spring",
              stiffness: 300,
              damping: 20,
              duration: 0.4,
              delay: 0.05,
            }}
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              onAddToWishlist?.(product.id);
            }}
          >
            <Heart className="w-4 h-4" />
          </motion.button>
          <motion.button
            className="bg-white/95 hover:bg-white text-gray-900 p-3 rounded-full shadow-xl border border-white/20"
            initial={{ scale: 0, rotate: -180 }}
            whileInView={{ scale: 1, rotate: 0 }}
            whileHover={{
              scale: 1.05,
              boxShadow: "0 10px 25px -5px rgba(0, 0, 0, 0.3)",
            }}
            whileTap={{ scale: 0.98 }}
            transition={{
              type: "spring",
              stiffness: 300,
              damping: 20,
              duration: 0.4,
              delay: 0.1,
            }}
            onClick={(e) => {
              e.stopPropagation();
              router.push(`/product/${product.id}`);
            }}
          >
            <Eye className="w-4 h-4" />
          </motion.button>
        </motion.div>
      </div>

      {/* Content */}
      <motion.div
        className="p-4"
        initial={{ y: 10, opacity: 0.8 }}
        whileInView={{ y: 0, opacity: 1 }}
        viewport={{ once: true }}
        transition={{
          delay: 0.1,
          duration: 0.5,
          ease: [0.25, 0.46, 0.45, 0.94],
        }}
      >
        <div className="flex items-center gap-2 text-sm text-gray-500 mb-2">
          <span>{product.category}</span>
          <span>•</span>
          <div className="flex items-center gap-1">
            <Star className="w-3 h-3 fill-yellow-400 text-yellow-400" />
            <span className="font-medium">{product.rating}</span>
            <span className="text-gray-400">({product.reviews})</span>
          </div>
        </div>

        <motion.h3
          className="text-lg font-bold text-gray-900 mb-2 group-hover:text-blue-600 transition-colors"
          whileHover={{ x: 2 }}
          transition={{
            duration: 0.3,
            ease: [0.25, 0.46, 0.45, 0.94],
          }}
        >
          {product.name}
        </motion.h3>

        <p className="text-sm text-gray-600 mb-3 line-clamp-2">
          {product.description}
        </p>

        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <span className="text-xl font-bold text-gray-900">
              {product.price.toLocaleString()}원
            </span>
            {product.originalPrice && (
              <span className="text-sm text-gray-500 line-through">
                {product.originalPrice.toLocaleString()}원
              </span>
            )}
          </div>
        </div>

        <motion.button
          className="w-full bg-gradient-to-r from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700 text-white py-3 rounded-xl font-semibold shadow-lg transition-all duration-300"
          whileHover={{
            scale: 1.01,
            boxShadow: "0 10px 25px -5px rgba(59, 130, 246, 0.4)",
          }}
          whileTap={{ scale: 0.99 }}
          transition={{
            type: "spring",
            stiffness: 400,
            damping: 25,
          }}
          onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            onAddToCart?.(product.id);
          }}
        >
          장바구니 담기
        </motion.button>
      </motion.div>
    </motion.div>
  );
}
