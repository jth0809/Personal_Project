"use client";

import Link from "next/link";
import { ArrowRight, Sparkles, ShoppingBag, Heart, Star } from "lucide-react";
import { motion } from "framer-motion";

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      delayChildren: 0.3,
      staggerChildren: 0.2,
    },
  },
};

const itemVariants = {
  hidden: { y: 20, opacity: 0 },
  visible: {
    y: 0,
    opacity: 1,
    transition: {
      type: "spring" as const,
      stiffness: 100,
    },
  },
};

export default function HeroSection() {
  return (
    <section className="relative h-screen flex items-center justify-center overflow-hidden bg-gradient-to-br from-slate-900 via-blue-900 to-indigo-900">
      {/* 배경 그라디언트 오버레이 */}
      <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent" />

      {/* 플로팅 아이콘들 */}
      <div className="absolute inset-0 overflow-hidden">
        <motion.div
          className="absolute top-20 left-20 text-blue-300/30"
          animate={{
            y: [-10, 10, -10],
            rotate: [0, 5, -5, 0],
          }}
          transition={{
            duration: 4,
            repeat: Infinity,
            ease: "easeInOut",
          }}
        >
          <Sparkles size={32} />
        </motion.div>
        <motion.div
          className="absolute top-32 right-32 text-indigo-300/30"
          animate={{
            y: [-10, 10, -10],
            rotate: [0, 5, -5, 0],
          }}
          transition={{
            duration: 4,
            repeat: Infinity,
            ease: "easeInOut",
            delay: 0.5,
          }}
        >
          <ShoppingBag size={28} />
        </motion.div>
        <motion.div
          className="absolute bottom-32 left-32 text-purple-300/30"
          animate={{
            y: [-10, 10, -10],
            rotate: [0, 5, -5, 0],
          }}
          transition={{
            duration: 4,
            repeat: Infinity,
            ease: "easeInOut",
            delay: 1,
          }}
        >
          <Heart size={24} />
        </motion.div>
        <motion.div
          className="absolute bottom-20 right-20 text-blue-400/30"
          animate={{
            y: [-10, 10, -10],
            rotate: [0, 5, -5, 0],
          }}
          transition={{
            duration: 4,
            repeat: Infinity,
            ease: "easeInOut",
            delay: 1.5,
          }}
        >
          <Star size={30} />
        </motion.div>
      </div>

      {/* 메인 콘텐츠 */}
      <motion.div
        className="text-center z-10 px-4 max-w-4xl mx-auto"
        variants={containerVariants}
        initial="hidden"
        animate="visible"
      >
        <motion.div
          className="inline-flex items-center gap-2 bg-blue-500/20 backdrop-blur-sm border border-blue-300/30 rounded-full px-6 py-2 mb-8"
          variants={itemVariants}
        >
          <Sparkles className="w-4 h-4 text-blue-300" />
          <span className="text-blue-200 text-sm font-medium">
            새로운 컬렉션 출시
          </span>
        </motion.div>

        <motion.h1
          className="text-5xl md:text-7xl font-bold text-white mb-6 leading-tight"
          variants={itemVariants}
        >
          당신만의 특별한
          <br />
          <span className="bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
            라이프스타일
          </span>
        </motion.h1>

        <motion.p
          className="text-xl md:text-2xl text-gray-300 mb-12 leading-relaxed max-w-2xl mx-auto"
          variants={itemVariants}
        >
          세상에 하나뿐인 나만의 아이템으로
          <br className="hidden md:block" />
          특별한 순간을 만들어보세요
        </motion.p>

        <motion.div
          className="flex flex-col sm:flex-row gap-4 justify-center items-center"
          variants={itemVariants}
        >
          <motion.div
            animate={{
              scale: [1, 1.05, 1],
            }}
            transition={{
              duration: 2,
              repeat: Infinity,
              ease: "easeInOut",
            }}
          >
            <Link
              href="/products"
              className="group inline-flex items-center gap-3 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white px-8 py-4 rounded-full font-semibold text-lg transition-all duration-300 shadow-lg hover:shadow-2xl hover:shadow-blue-500/25"
            >
              지금 쇼핑하기
              <motion.div
                whileHover={{ x: 5 }}
                transition={{ type: "spring", stiffness: 400 }}
              >
                <ArrowRight className="w-5 h-5" />
              </motion.div>
            </Link>
          </motion.div>

          <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
            <Link
              href="/about"
              className="inline-flex items-center gap-2 text-white border-2 border-white/30 hover:border-blue-400 px-8 py-4 rounded-full font-semibold text-lg transition-all duration-300 backdrop-blur-sm hover:bg-white/10"
            >
              브랜드 스토리
            </Link>
          </motion.div>
        </motion.div>

        {/* 통계 섹션 */}
        <motion.div
          className="flex justify-center gap-8 md:gap-16 mt-16 pt-8 border-t border-white/10"
          variants={itemVariants}
        >
          {[
            { number: "10K+", label: "만족한 고객" },
            { number: "500+", label: "프리미엄 상품" },
            { number: "99%", label: "고객 만족도" },
          ].map((stat, index) => (
            <motion.div
              key={index}
              className="text-center"
              whileHover={{ scale: 1.1 }}
              transition={{ type: "spring", stiffness: 300 }}
            >
              <div className="text-2xl md:text-3xl font-bold text-white mb-1">
                {stat.number}
              </div>
              <div className="text-gray-400 text-sm md:text-base">
                {stat.label}
              </div>
            </motion.div>
          ))}
        </motion.div>
      </motion.div>

      {/* 스크롤 인디케이터 */}
      <motion.div
        className="absolute bottom-8 left-1/2 transform -translate-x-1/2"
        animate={{
          y: [0, 10, 0],
        }}
        transition={{
          duration: 2,
          repeat: Infinity,
          ease: "easeInOut",
        }}
      >
        <div className="w-6 h-10 border-2 border-white/30 rounded-full flex justify-center">
          <motion.div
            className="w-1 h-3 bg-white/60 rounded-full mt-2"
            animate={{
              opacity: [0.3, 1, 0.3],
              scaleY: [0.5, 1, 0.5],
            }}
            transition={{
              duration: 2,
              repeat: Infinity,
              ease: "easeInOut",
            }}
          />
        </div>
      </motion.div>
    </section>
  );
}
