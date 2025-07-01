"use client";

import Link from "next/link";
import { Briefcase, Home, Watch, Shirt, PenTool, Sofa } from "lucide-react";
import { motion } from "framer-motion";

const categories = [
  {
    id: "bags",
    name: "가방",
    icon: Briefcase,
    color: "from-blue-500 to-cyan-500",
    description: "스타일리시한 가방",
  },
  {
    id: "living",
    name: "생활용품",
    icon: Home,
    color: "from-emerald-500 to-teal-500",
    description: "실용적인 생활용품",
  },
  {
    id: "accessories",
    name: "액세서리",
    icon: Watch,
    color: "from-purple-500 to-indigo-500",
    description: "세련된 액세서리",
  },
  {
    id: "clothing",
    name: "의류",
    icon: Shirt,
    color: "from-pink-500 to-rose-500",
    description: "트렌디한 의류",
  },
  {
    id: "stationery",
    name: "문구",
    icon: PenTool,
    color: "from-amber-500 to-orange-500",
    description: "감각적인 문구",
  },
  {
    id: "interior",
    name: "인테리어",
    icon: Sofa,
    color: "from-slate-500 to-gray-500",
    description: "모던한 인테리어",
  },
];

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      delayChildren: 0.2,
      staggerChildren: 0.1,
    },
  },
};

const itemVariants = {
  hidden: { y: 50, opacity: 0 },
  visible: {
    y: 0,
    opacity: 1,
    transition: {
      type: "spring" as const,
      stiffness: 100,
      damping: 12,
    },
  },
};

export default function CategoriesSection() {
  return (
    <section className="py-24 bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <motion.div
          className="text-center mb-16"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6 }}
        >
          <motion.div
            className="inline-flex items-center gap-2 bg-blue-100 text-blue-700 px-4 py-2 rounded-full mb-4"
            initial={{ scale: 0 }}
            whileInView={{ scale: 1 }}
            viewport={{ once: true }}
            transition={{ delay: 0.2, type: "spring", stiffness: 300 }}
          >
            <span className="text-sm font-medium">카테고리</span>
          </motion.div>
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
            다양한 카테고리
          </h2>
          <p className="text-xl text-gray-600 max-w-2xl mx-auto">
            원하는 상품을 빠르게 찾아보세요
          </p>
        </motion.div>

        <motion.div
          className="grid grid-cols-2 md:grid-cols-3 gap-6 lg:gap-8"
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true }}
        >
          {categories.map((category) => {
            const IconComponent = category.icon;
            return (
              <motion.div key={category.id} variants={itemVariants}>
                <Link href={`/products/${category.id}`} className="block group">
                  <motion.div
                    className="relative bg-white rounded-2xl p-8 shadow-lg border border-gray-100 overflow-hidden"
                    whileHover={{
                      scale: 1.05,
                      boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.25)",
                    }}
                    whileTap={{ scale: 0.98 }}
                    transition={{ type: "spring", stiffness: 300, damping: 20 }}
                  >
                    {/* 배경 그라디언트 */}
                    <motion.div
                      className={`absolute inset-0 bg-gradient-to-br ${category.color} opacity-0 group-hover:opacity-10 transition-opacity duration-500`}
                      initial={{ scale: 0, opacity: 0 }}
                      whileHover={{ scale: 1, opacity: 0.1 }}
                      transition={{ duration: 0.3 }}
                    />

                    {/* 아이콘 */}
                    <motion.div
                      className={`w-16 h-16 rounded-xl bg-gradient-to-br ${category.color} flex items-center justify-center mb-6 mx-auto`}
                      whileHover={{ rotate: [0, -10, 10, 0] }}
                      transition={{ duration: 0.5 }}
                    >
                      <IconComponent
                        className="w-8 h-8 text-white"
                        strokeWidth={1.5}
                      />
                    </motion.div>

                    {/* 텍스트 */}
                    <div className="text-center">
                      <motion.h3
                        className="text-xl font-bold text-gray-900 mb-2"
                        initial={{ opacity: 0.8 }}
                        whileHover={{ opacity: 1 }}
                      >
                        {category.name}
                      </motion.h3>
                      <motion.p
                        className="text-gray-600 text-sm"
                        initial={{ opacity: 0.6 }}
                        whileHover={{ opacity: 1 }}
                      >
                        {category.description}
                      </motion.p>
                    </div>

                    {/* 호버 효과 화살표 */}
                    <motion.div
                      className="absolute bottom-4 right-4 opacity-0 group-hover:opacity-100"
                      initial={{ x: -10, opacity: 0 }}
                      whileHover={{ x: 0, opacity: 1 }}
                      transition={{ duration: 0.2 }}
                    >
                      <div className="w-6 h-6 rounded-full bg-gray-900 flex items-center justify-center">
                        <svg
                          className="w-3 h-3 text-white"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M9 5l7 7-7 7"
                          />
                        </svg>
                      </div>
                    </motion.div>
                  </motion.div>
                </Link>
              </motion.div>
            );
          })}
        </motion.div>

        {/* 더 보기 버튼 */}
        <motion.div
          className="text-center mt-12"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ delay: 0.5, duration: 0.6 }}
        >
          <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
            <Link
              href="/products"
              className="inline-flex items-center gap-2 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white px-8 py-4 rounded-full font-semibold text-lg shadow-lg hover:shadow-xl transition-all duration-300"
            >
              모든 상품 보기
              <motion.svg
                className="w-5 h-5"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                whileHover={{ x: 3 }}
                transition={{ type: "spring", stiffness: 400 }}
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M13 7l5 5m0 0l-5 5m5-5H6"
                />
              </motion.svg>
            </Link>
          </motion.div>
        </motion.div>
      </div>
    </section>
  );
}
