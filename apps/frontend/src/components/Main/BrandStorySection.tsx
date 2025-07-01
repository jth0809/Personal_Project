"use client";

import { Button } from "@/components/ui/button";
import { Heart, Users, Award, Sparkles, ArrowRight } from "lucide-react";
import Link from "next/link";
import { motion } from "framer-motion";

const stats = [
  {
    number: "2019",
    label: "설립년도",
    icon: <Sparkles className="h-6 w-6" />,
  },
  {
    number: "10K+",
    label: "만족한 고객",
    icon: <Users className="h-6 w-6" />,
  },
  {
    number: "500+",
    label: "파트너 브랜드",
    icon: <Heart className="h-6 w-6" />,
  },
  {
    number: "50+",
    label: "수상 경력",
    icon: <Award className="h-6 w-6" />,
  },
];

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      delayChildren: 0.3,
      staggerChildren: 0.15,
      duration: 0.8,
      ease: [0.25, 0.46, 0.45, 0.94] as const,
    },
  },
};

const itemVariants = {
  hidden: { y: 40, opacity: 0 },
  visible: {
    y: 0,
    opacity: 1,
    transition: {
      type: "spring" as const,
      stiffness: 80,
      damping: 20,
      duration: 0.7,
    },
  },
};

export default function BrandStorySection() {
  return (
    <section className="py-20 bg-gradient-to-br from-slate-50 to-blue-50 relative overflow-hidden">
      {/* Background Decoration - 정적 */}
      <div className="absolute inset-0">
        <div className="absolute top-20 left-20 w-64 h-64 bg-blue-200/20 rounded-full blur-3xl" />
        <div className="absolute bottom-20 right-20 w-48 h-48 bg-cyan-200/20 rounded-full blur-3xl" />
      </div>

      <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <motion.div
          className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center"
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true }}
        >
          {/* Left Content */}
          <motion.div variants={itemVariants}>
            <motion.div
              className="inline-flex items-center px-4 py-2 rounded-full bg-blue-100 border border-blue-200 mb-6"
              initial={{ scale: 0 }}
              whileInView={{ scale: 1 }}
              viewport={{ once: true }}
              transition={{
                delay: 0.2,
                type: "spring",
                stiffness: 200,
                damping: 20,
                duration: 0.6,
              }}
            >
              <Heart className="h-4 w-4 text-blue-600 mr-2" />
              <span className="text-blue-800 text-sm font-medium">
                Our Story
              </span>
            </motion.div>

            <motion.h2
              className="text-4xl md:text-5xl font-bold mb-6"
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{
                delay: 0.3,
                duration: 0.7,
                ease: [0.25, 0.46, 0.45, 0.94] as const,
              }}
            >
              <span className="text-slate-900">특별함을 </span>
              <span className="bg-gradient-to-r from-blue-600 to-cyan-600 bg-clip-text text-transparent">
                일상
              </span>
              <span className="text-slate-900">으로</span>
            </motion.h2>

            <motion.p
              className="text-xl text-slate-600 mb-8 leading-relaxed"
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{
                delay: 0.4,
                duration: 0.7,
                ease: [0.25, 0.46, 0.45, 0.94] as const,
              }}
            >
              GOODSSHOP은 단순한 쇼핑몰이 아닙니다. 우리는 사람들의 일상을 더
              특별하고 의미있게 만들어주는
              <span className="font-semibold text-blue-600">
                라이프스타일 큐레이터
              </span>
              입니다.
            </motion.p>

            <motion.div
              className="space-y-4 mb-8"
              variants={containerVariants}
              initial="hidden"
              whileInView="visible"
              viewport={{ once: true }}
            >
              {[
                {
                  color: "bg-blue-500",
                  title: "엄선된 상품:",
                  desc: "품질과 디자인을 모두 갖춘 제품들만 선별",
                },
                {
                  color: "bg-cyan-500",
                  title: "지속가능성:",
                  desc: "환경을 생각하는 브랜드와의 협업 우선",
                },
                {
                  color: "bg-indigo-500",
                  title: "고객 중심:",
                  desc: "모든 결정의 중심에는 고객 만족이 있습니다",
                },
              ].map((item, index) => (
                <motion.div
                  key={index}
                  className="flex items-start space-x-3"
                  variants={itemVariants}
                  whileHover={{ x: 3 }}
                  transition={{
                    type: "spring",
                    stiffness: 400,
                    damping: 25,
                    duration: 0.3,
                  }}
                >
                  <motion.div
                    className={`w-2 h-2 ${item.color} rounded-full mt-2`}
                    initial={{ scale: 0 }}
                    whileInView={{ scale: 1 }}
                    viewport={{ once: true }}
                    transition={{ delay: 0.5 + index * 0.1, duration: 0.4 }}
                  />
                  <p className="text-slate-700">
                    <span className="font-semibold">{item.title}</span>{" "}
                    {item.desc}
                  </p>
                </motion.div>
              ))}
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{
                delay: 0.8,
                duration: 0.6,
                ease: [0.25, 0.46, 0.45, 0.94] as const,
              }}
            >
              <Link href="/about">
                <motion.div
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  transition={{
                    type: "spring",
                    stiffness: 400,
                    damping: 25,
                  }}
                >
                  <Button
                    size="lg"
                    className="bg-gradient-to-r from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700 text-white px-8 py-4 rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 group"
                  >
                    더 자세히 알아보기
                    <ArrowRight className="h-5 w-5 ml-2" />
                  </Button>
                </motion.div>
              </Link>
            </motion.div>
          </motion.div>

          {/* Right Content - Image & Stats */}
          <motion.div className="relative" variants={itemVariants}>
            {/* Main Image */}
            <motion.div
              className="relative bg-gradient-to-br from-blue-100 to-cyan-100 rounded-3xl p-8 shadow-2xl"
              whileHover={{
                scale: 1.01,
                boxShadow: "0 20px 40px -8px rgba(0, 0, 0, 0.15)",
              }}
              transition={{
                type: "spring",
                stiffness: 300,
                damping: 25,
                duration: 0.4,
              }}
            >
              <motion.img
                src="/logo.png"
                alt="GOODSSHOP Team"
                className="w-full h-80 object-contain rounded-2xl"
                initial={{ opacity: 0, scale: 0.95 }}
                whileInView={{ opacity: 1, scale: 1 }}
                viewport={{ once: true }}
                transition={{
                  delay: 0.5,
                  duration: 0.8,
                  ease: [0.25, 0.46, 0.45, 0.94] as const,
                }}
              />

              {/* Floating Stats Cards */}
              <motion.div
                className="absolute -top-4 -left-4 bg-white rounded-2xl p-4 shadow-xl border border-slate-200"
                initial={{ x: -30, y: -30, opacity: 0 }}
                whileInView={{ x: 0, y: 0, opacity: 1 }}
                viewport={{ once: true }}
                transition={{
                  delay: 0.7,
                  type: "spring",
                  stiffness: 120,
                  damping: 20,
                  duration: 0.8,
                }}
                whileHover={{
                  scale: 1.05,
                  boxShadow: "0 10px 25px -5px rgba(0, 0, 0, 0.15)",
                }}
              >
                <div className="flex items-center space-x-3">
                  <div className="w-12 h-12 bg-gradient-to-r from-green-400 to-emerald-400 rounded-xl flex items-center justify-center">
                    <Users className="h-6 w-6 text-white" />
                  </div>
                  <div>
                    <motion.div
                      className="text-2xl font-bold text-slate-900"
                      initial={{ scale: 0 }}
                      whileInView={{ scale: 1 }}
                      viewport={{ once: true }}
                      transition={{
                        delay: 0.8,
                        type: "spring",
                        stiffness: 200,
                        duration: 0.6,
                      }}
                    >
                      10K+
                    </motion.div>
                    <div className="text-sm text-slate-600">
                      Happy Customers
                    </div>
                  </div>
                </div>
              </motion.div>

              <motion.div
                className="absolute -bottom-4 -right-4 bg-white rounded-2xl p-4 shadow-xl border border-slate-200"
                initial={{ x: 30, y: 30, opacity: 0 }}
                whileInView={{ x: 0, y: 0, opacity: 1 }}
                viewport={{ once: true }}
                transition={{
                  delay: 0.9,
                  type: "spring",
                  stiffness: 120,
                  damping: 20,
                  duration: 0.8,
                }}
                whileHover={{
                  scale: 1.05,
                  boxShadow: "0 10px 25px -5px rgba(0, 0, 0, 0.15)",
                }}
              >
                <div className="flex items-center space-x-3">
                  <div className="w-12 h-12 bg-gradient-to-r from-purple-400 to-pink-400 rounded-xl flex items-center justify-center">
                    <Award className="h-6 w-6 text-white" />
                  </div>
                  <div>
                    <motion.div
                      className="text-2xl font-bold text-slate-900"
                      initial={{ scale: 0 }}
                      whileInView={{ scale: 1 }}
                      viewport={{ once: true }}
                      transition={{
                        delay: 1.0,
                        type: "spring",
                        stiffness: 200,
                        duration: 0.6,
                      }}
                    >
                      4.9★
                    </motion.div>
                    <div className="text-sm text-slate-600">Rating</div>
                  </div>
                </div>
              </motion.div>
            </motion.div>
          </motion.div>
        </motion.div>

        {/* Stats Section */}
        <motion.div
          className="mt-20 grid grid-cols-2 md:grid-cols-4 gap-8"
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true }}
        >
          {stats.map((stat, index) => (
            <motion.div
              key={stat.label}
              className="text-center group"
              variants={itemVariants}
              whileHover={{ y: -5 }}
              transition={{
                type: "spring",
                stiffness: 400,
                damping: 25,
                duration: 0.3,
              }}
            >
              <motion.div
                className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-2xl text-white mb-4"
                whileHover={{
                  scale: 1.1,
                  boxShadow: "0 10px 25px -5px rgba(59, 130, 246, 0.4)",
                }}
                transition={{
                  type: "spring",
                  stiffness: 300,
                  damping: 20,
                  duration: 0.3,
                }}
              >
                {stat.icon}
              </motion.div>
              <motion.div
                className="text-3xl font-bold text-slate-900 mb-2"
                initial={{ scale: 0 }}
                whileInView={{ scale: 1 }}
                viewport={{ once: true }}
                transition={{
                  delay: 0.5 + index * 0.1,
                  type: "spring",
                  stiffness: 200,
                  duration: 0.6,
                }}
              >
                {stat.number}
              </motion.div>
              <div className="text-slate-600">{stat.label}</div>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
}
