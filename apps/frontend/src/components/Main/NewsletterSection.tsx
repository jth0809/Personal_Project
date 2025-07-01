"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Mail, Check, Sparkles, Gift, Zap } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

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

const successVariants = {
  initial: { scale: 0 },
  animate: {
    scale: 1,
    transition: {
      type: "spring" as const,
      stiffness: 150,
      damping: 15,
      duration: 0.8,
    },
  },
};

export default function NewsletterSection() {
  const [email, setEmail] = useState("");
  const [isSubscribed, setIsSubscribed] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim()) return;

    setIsLoading(true);

    // 실제 구독 로직
    setTimeout(() => {
      setIsSubscribed(true);
      setIsLoading(false);
      setEmail("");
    }, 1500);
  };

  return (
    <AnimatePresence mode="wait">
      {isSubscribed ? (
        <motion.section
          key="success"
          className="py-20 bg-gradient-to-r from-green-600 to-emerald-600 relative overflow-hidden"
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          exit={{ opacity: 0, scale: 0.95 }}
          transition={{
            duration: 0.8,
            type: "spring",
            stiffness: 100,
            damping: 20,
          }}
        >
          {/* Background Elements - 정적 */}
          <div className="absolute inset-0">
            <div className="absolute top-10 right-10 w-32 h-32 bg-white/10 rounded-full blur-2xl" />
            <div className="absolute bottom-10 left-10 w-40 h-40 bg-white/10 rounded-full blur-2xl" />
          </div>

          <motion.div
            className="relative z-10 max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center"
            variants={containerVariants}
            initial="hidden"
            animate="visible"
          >
            <motion.div
              className="bg-white/10 backdrop-blur-sm rounded-full w-24 h-24 flex items-center justify-center mx-auto mb-6"
              variants={successVariants}
              initial="initial"
              animate="animate"
            >
              <Check className="h-12 w-12 text-white" />
            </motion.div>

            <motion.h2
              className="text-4xl md:text-5xl font-bold text-white mb-4"
              variants={itemVariants}
            >
              구독 완료! 🎉
            </motion.h2>

            <motion.p
              className="text-xl text-green-100 mb-8"
              variants={itemVariants}
            >
              특별한 혜택과 새로운 소식을 가장 먼저 받아보세요
            </motion.p>

            <motion.div
              className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-2xl mx-auto"
              variants={containerVariants}
            >
              {[
                { icon: Gift, text: "웰컴 쿠폰" },
                { icon: Zap, text: "독점 할인" },
                { icon: Sparkles, text: "신상품 알림" },
              ].map((benefit, _index) => (
                <motion.div
                  key={benefit.text}
                  className="bg-white/10 backdrop-blur-sm rounded-xl p-4"
                  variants={itemVariants}
                  whileHover={{
                    scale: 1.02,
                    backgroundColor: "rgba(255, 255, 255, 0.15)",
                  }}
                  transition={{ type: "spring", stiffness: 400, damping: 25 }}
                >
                  <div className="h-8 w-8 text-white mx-auto mb-2">
                    <benefit.icon className="h-8 w-8" />
                  </div>
                  <p className="text-white font-medium">{benefit.text}</p>
                </motion.div>
              ))}
            </motion.div>
          </motion.div>
        </motion.section>
      ) : (
        <motion.section
          key="newsletter"
          className="py-20 bg-gradient-to-br from-slate-900 via-blue-900 to-indigo-900 relative overflow-hidden"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.6 }}
        >
          {/* Background Elements - 정적 */}
          <div className="absolute inset-0">
            <div className="absolute top-20 right-20 w-64 h-64 bg-blue-500/20 rounded-full blur-3xl" />
            <div className="absolute bottom-20 left-20 w-48 h-48 bg-cyan-500/20 rounded-full blur-3xl" />
          </div>

          <motion.div
            className="relative z-10 max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center"
            variants={containerVariants}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
          >
            {/* Header */}
            <motion.div className="mb-12" variants={itemVariants}>
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
                <Mail className="h-4 w-4 text-blue-400 mr-2" />
                <span className="text-blue-300 text-sm font-medium">
                  Newsletter
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
                <span className="text-white">특별한 혜택을 </span>
                <span className="bg-gradient-to-r from-blue-400 to-cyan-400 bg-clip-text text-transparent">
                  놓치지 마세요
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
                새로운 상품 출시 소식과 독점 할인 혜택을
                <br className="hidden md:block" />
                가장 먼저 받아보세요
              </motion.p>
            </motion.div>

            {/* Benefits */}
            <motion.div
              className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12"
              variants={containerVariants}
            >
              {[
                {
                  icon: Gift,
                  title: "웰컴 쿠폰",
                  desc: "구독 즉시 15% 할인 쿠폰",
                  color: "blue",
                },
                {
                  icon: Zap,
                  title: "독점 세일",
                  desc: "구독자만의 특별 할인",
                  color: "cyan",
                },
                {
                  icon: Sparkles,
                  title: "신상품 알림",
                  desc: "새로운 상품을 가장 먼저",
                  color: "indigo",
                },
              ].map((benefit, _index) => (
                <motion.div
                  key={benefit.title}
                  className="bg-slate-800/40 backdrop-blur-xl rounded-xl p-6 border border-slate-700/50"
                  variants={itemVariants}
                  whileHover={{
                    scale: 1.02,
                    y: -3,
                    borderColor: `rgba(59, 130, 246, 0.5)`,
                    boxShadow: "0 10px 25px -5px rgba(59, 130, 246, 0.15)",
                  }}
                  transition={{ type: "spring", stiffness: 400, damping: 25 }}
                >
                  <div className="h-10 w-10 text-blue-400 mx-auto mb-4">
                    <benefit.icon className="h-10 w-10" />
                  </div>
                  <h3 className="text-lg font-semibold text-white mb-2">
                    {benefit.title}
                  </h3>
                  <p className="text-slate-400 text-sm">{benefit.desc}</p>
                </motion.div>
              ))}
            </motion.div>

            {/* Newsletter Form */}
            <motion.div className="max-w-md mx-auto" variants={itemVariants}>
              <motion.form
                onSubmit={handleSubmit}
                className="flex flex-col sm:flex-row gap-4"
                initial={{ opacity: 0, scale: 0.95 }}
                whileInView={{ opacity: 1, scale: 1 }}
                viewport={{ once: true }}
                transition={{
                  delay: 0.8,
                  duration: 0.7,
                  ease: [0.25, 0.46, 0.45, 0.94] as const,
                }}
              >
                <motion.div
                  className="flex-1 relative"
                  whileFocus={{ scale: 1.01 }}
                  transition={{ type: "spring", stiffness: 400, damping: 25 }}
                >
                  <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-5 w-5" />
                  <Input
                    type="email"
                    placeholder="이메일 주소를 입력하세요"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="pl-10 bg-slate-800/50 border-slate-600 text-white placeholder-slate-400 focus:border-blue-500 focus:ring-blue-500/20 h-12"
                    required
                  />
                </motion.div>

                <motion.div
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  transition={{ type: "spring", stiffness: 400, damping: 25 }}
                >
                  <Button
                    type="submit"
                    disabled={isLoading}
                    className="bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 text-white font-medium px-6 h-12 rounded-lg shadow-lg shadow-blue-500/25 transition-all duration-200 hover:shadow-blue-500/40"
                  >
                    {isLoading ? (
                      <motion.div
                        className="flex items-center"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                      >
                        <motion.div
                          className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full mr-2"
                          animate={{ rotate: 360 }}
                          transition={{
                            duration: 1,
                            repeat: Infinity,
                            ease: "linear",
                          }}
                        />
                        구독 중...
                      </motion.div>
                    ) : (
                      <span>구독하기</span>
                    )}
                  </Button>
                </motion.div>
              </motion.form>

              <motion.p
                className="text-sm text-slate-500 mt-4"
                initial={{ opacity: 0 }}
                whileInView={{ opacity: 1 }}
                viewport={{ once: true }}
                transition={{
                  delay: 1,
                  duration: 0.6,
                  ease: [0.25, 0.46, 0.45, 0.94] as const,
                }}
              >
                언제든지 구독을 취소할 수 있습니다. 개인정보는 안전하게
                보호됩니다.
              </motion.p>
            </motion.div>
          </motion.div>
        </motion.section>
      )}
    </AnimatePresence>
  );
}
