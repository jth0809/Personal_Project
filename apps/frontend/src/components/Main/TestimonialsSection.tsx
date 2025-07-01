"use client";

import { Button } from "@/components/ui/button";
import { Star, Quote, Users, ArrowLeft, ArrowRight } from "lucide-react";
import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";

interface Testimonial {
  id: string;
  name: string;
  role: string;
  avatar: string;
  rating: number;
  content: string;
  product: string;
}

const testimonials: Testimonial[] = [
  {
    id: "1",
    name: "김민지",
    role: "디자이너",
    avatar: "/logo.png",
    rating: 5,
    content:
      "정말 만족스러운 구매였어요! 품질도 좋고 디자인도 예쁘고, 특히 포장이 너무 정성스럽게 되어 있어서 감동받았습니다. 다음에도 꼭 재구매할게요!",
    product: "프리미엄 에코백",
  },
  {
    id: "2",
    name: "박준호",
    role: "개발자",
    avatar: "/logo.png",
    rating: 5,
    content:
      "업무용으로 구매했는데 정말 유용해요. 충전도 빠르고 디자인도 깔끔하고 무선이라 선 정리에 신경 쓸 일이 없어서 좋습니다. 강력 추천!",
    product: "스마트 무선 충전기",
  },
  {
    id: "3",
    name: "이수진",
    role: "마케터",
    avatar: "/logo.png",
    rating: 5,
    content:
      "홈오피스 셋업을 위해 구매했는데 각도 조절도 자유롭고 안정성도 좋아서 만족합니다. 가격 대비 퀄리티가 정말 좋아요!",
    product: "미니멀 노트북 스탠드",
  },
  {
    id: "4",
    name: "최영수",
    role: "학생",
    avatar: "/logo.png",
    rating: 4,
    content:
      "배송도 빠르고 상품 상태도 완벽했어요. 고객 서비스도 친절하고 빠른 응답으로 문제를 해결해 주셔서 감사했습니다.",
    product: "캐주얼 백팩",
  },
];

const slideVariants = {
  enter: (direction: number) => ({
    x: direction > 0 ? 300 : -300,
    opacity: 0,
    scale: 0.95,
  }),
  center: {
    zIndex: 1,
    x: 0,
    opacity: 1,
    scale: 1,
  },
  exit: (direction: number) => ({
    zIndex: 0,
    x: direction < 0 ? 300 : -300,
    opacity: 0,
    scale: 0.95,
  }),
};

export default function TestimonialsSection() {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [direction, setDirection] = useState(0);

  const nextTestimonial = () => {
    setDirection(1);
    setCurrentIndex((prev) => (prev + 1) % testimonials.length);
  };

  const prevTestimonial = () => {
    setDirection(-1);
    setCurrentIndex(
      (prev) => (prev - 1 + testimonials.length) % testimonials.length
    );
  };

  const goToSlide = (index: number) => {
    setDirection(index > currentIndex ? 1 : -1);
    setCurrentIndex(index);
  };

  return (
    <section className="py-20 bg-gradient-to-br from-slate-900 via-blue-900 to-indigo-900 relative overflow-hidden">
      {/* Background Elements - 정적 */}
      <div className="absolute inset-0">
        <div className="absolute top-20 left-20 w-64 h-64 bg-blue-500/10 rounded-full blur-3xl" />
        <div className="absolute bottom-20 right-20 w-48 h-48 bg-cyan-500/10 rounded-full blur-3xl" />
      </div>

      <div className="relative z-10 max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Header */}
        <motion.div
          className="text-center mb-16"
          initial={{ opacity: 0, y: 40 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{
            duration: 0.8,
            ease: [0.25, 0.46, 0.45, 0.94] as const,
          }}
        >
          <motion.div
            className="inline-flex items-center px-4 py-2 rounded-full bg-blue-500/10 backdrop-blur-sm border border-blue-500/20 mb-6"
            initial={{ scale: 0 }}
            whileInView={{ scale: 1 }}
            viewport={{ once: true }}
            transition={{
              delay: 0.2,
              type: "spring",
              stiffness: 200,
              damping: 20,
            }}
          >
            <Users className="h-4 w-4 text-blue-400 mr-2" />
            <span className="text-blue-300 text-sm font-medium">
              Customer Reviews
            </span>
          </motion.div>

          <motion.h2
            className="text-4xl md:text-5xl font-bold mb-4"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{
              delay: 0.3,
              duration: 0.7,
              ease: [0.25, 0.46, 0.45, 0.94] as const,
            }}
          >
            <span className="text-white">고객들의 </span>
            <span className="bg-gradient-to-r from-blue-400 to-cyan-400 bg-clip-text text-transparent">
              생생한 후기
            </span>
          </motion.h2>

          <motion.p
            className="text-xl text-slate-300 max-w-2xl mx-auto"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{
              delay: 0.4,
              duration: 0.7,
              ease: [0.25, 0.46, 0.45, 0.94] as const,
            }}
          >
            실제 고객들이 남긴 솔직한 후기를 확인해보세요
          </motion.p>
        </motion.div>

        {/* Testimonials Carousel */}
        <motion.div
          className="relative"
          initial={{ opacity: 0, scale: 0.95 }}
          whileInView={{ opacity: 1, scale: 1 }}
          viewport={{ once: true }}
          transition={{
            delay: 0.5,
            duration: 0.8,
            ease: [0.25, 0.46, 0.45, 0.94] as const,
          }}
        >
          <motion.div
            className="bg-slate-800/40 backdrop-blur-xl rounded-3xl border border-slate-700/50 shadow-2xl p-8 md:p-12"
            whileHover={{
              boxShadow: "0 20px 40px -8px rgba(0, 0, 0, 0.4)",
              borderColor: "rgba(59, 130, 246, 0.3)",
            }}
            transition={{ duration: 0.4 }}
          >
            <div className="flex flex-col md:flex-row items-center gap-8">
              {/* Quote Icon */}
              <motion.div
                className="flex-shrink-0"
                whileHover={{
                  rotate: [0, -5, 5, 0],
                  scale: 1.05,
                }}
                transition={{ duration: 0.6 }}
              >
                <div className="w-16 h-16 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-2xl flex items-center justify-center shadow-lg">
                  <Quote className="h-8 w-8 text-white" />
                </div>
              </motion.div>

              {/* Content */}
              <div className="flex-1 text-center md:text-left overflow-hidden">
                <AnimatePresence mode="wait" custom={direction}>
                  <motion.div
                    key={currentIndex}
                    custom={direction}
                    variants={slideVariants}
                    initial="enter"
                    animate="center"
                    exit="exit"
                    transition={{
                      x: { type: "spring", stiffness: 250, damping: 25 },
                      opacity: { duration: 0.3 },
                      scale: { duration: 0.3 },
                    }}
                  >
                    {/* Stars */}
                    <div className="flex items-center justify-center md:justify-start space-x-1 mb-4">
                      {[...Array(5)].map((_, i) => (
                        <motion.div
                          key={i}
                          initial={{ scale: 0 }}
                          animate={{ scale: 1 }}
                          transition={{
                            delay: i * 0.1,
                            type: "spring",
                            stiffness: 200,
                            duration: 0.5,
                          }}
                        >
                          <Star
                            className={`h-5 w-5 ${
                              i < testimonials[currentIndex].rating
                                ? "text-yellow-400 fill-current"
                                : "text-slate-600"
                            }`}
                          />
                        </motion.div>
                      ))}
                    </div>

                    {/* Review Content */}
                    <motion.blockquote
                      className="text-xl md:text-2xl text-white mb-6 leading-relaxed font-medium"
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ delay: 0.3, duration: 0.6 }}
                    >
                      &ldquo;{testimonials[currentIndex].content}&rdquo;
                    </motion.blockquote>

                    {/* Product */}
                    <motion.div
                      className="inline-flex items-center px-3 py-1 rounded-full bg-blue-500/20 border border-blue-500/30 mb-6"
                      initial={{ scale: 0 }}
                      animate={{ scale: 1 }}
                      transition={{
                        delay: 0.4,
                        type: "spring",
                        stiffness: 200,
                        duration: 0.6,
                      }}
                    >
                      <span className="text-blue-300 text-sm">
                        구매 상품: {testimonials[currentIndex].product}
                      </span>
                    </motion.div>

                    {/* Author */}
                    <motion.div
                      className="flex items-center justify-center md:justify-start space-x-4"
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: 0.5, duration: 0.6 }}
                    >
                      <motion.img
                        src={testimonials[currentIndex].avatar}
                        alt={testimonials[currentIndex].name}
                        className="w-12 h-12 rounded-full border-2 border-slate-600"
                        whileHover={{ scale: 1.05 }}
                        transition={{
                          type: "spring",
                          stiffness: 300,
                          duration: 0.3,
                        }}
                      />
                      <div>
                        <div className="font-semibold text-white">
                          {testimonials[currentIndex].name}
                        </div>
                        <div className="text-slate-400 text-sm">
                          {testimonials[currentIndex].role}
                        </div>
                      </div>
                    </motion.div>
                  </motion.div>
                </AnimatePresence>
              </div>
            </div>
          </motion.div>

          {/* Navigation Buttons */}
          <motion.div
            className="flex items-center justify-center space-x-4 mt-8"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ delay: 0.7, duration: 0.6 }}
          >
            <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
              <Button
                onClick={prevTestimonial}
                variant="outline"
                size="icon"
                className="border-slate-600 bg-slate-800/40 backdrop-blur-sm text-slate-300 hover:bg-slate-700/50 hover:text-white rounded-full"
              >
                <ArrowLeft className="h-4 w-4" />
              </Button>
            </motion.div>

            {/* Dots Indicator */}
            <div className="flex space-x-2">
              {testimonials.map((_, index) => (
                <motion.button
                  key={index}
                  onClick={() => goToSlide(index)}
                  className={`w-2 h-2 rounded-full transition-colors ${
                    index === currentIndex ? "bg-blue-400" : "bg-slate-600"
                  }`}
                  whileHover={{ scale: 1.3 }}
                  whileTap={{ scale: 0.9 }}
                  transition={{ duration: 0.2 }}
                />
              ))}
            </div>

            <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
              <Button
                onClick={nextTestimonial}
                variant="outline"
                size="icon"
                className="border-slate-600 bg-slate-800/40 backdrop-blur-sm text-slate-300 hover:bg-slate-700/50 hover:text-white rounded-full"
              >
                <ArrowRight className="h-4 w-4" />
              </Button>
            </motion.div>
          </motion.div>
        </motion.div>

        {/* Stats */}
        <motion.div
          className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-16"
          initial={{ opacity: 0, y: 40 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{
            delay: 0.8,
            duration: 0.8,
            ease: [0.25, 0.46, 0.45, 0.94] as const,
          }}
        >
          {[
            { number: "4.9/5", label: "평균 만족도" },
            { number: "1,200+", label: "고객 리뷰" },
            { number: "98%", label: "재구매율" },
          ].map((stat, index) => (
            <motion.div
              key={stat.label}
              className="text-center"
              initial={{ opacity: 0, scale: 0 }}
              whileInView={{ opacity: 1, scale: 1 }}
              viewport={{ once: true }}
              transition={{
                delay: 0.9 + index * 0.1,
                type: "spring",
                stiffness: 200,
                duration: 0.6,
              }}
              whileHover={{ y: -5 }}
            >
              <div className="text-4xl font-bold text-white mb-2">
                {stat.number}
              </div>
              <div className="text-slate-400">{stat.label}</div>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
}
