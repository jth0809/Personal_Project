"use client";

import { useState } from "react";
import AuthContainer from "@/components/auth/AuthContainer";
import BackButton from "@/components/auth/BackButton";
import AuthHeader from "@/components/auth/AuthHeader";
import LoginForm from "@/components/auth/LoginForm";
import RegisterForm from "@/components/auth/RegisterForm";
import SocialLogin from "@/components/auth/SocialLogin";
import type { LoginData, RegisterData } from "@/types/auth";

export default function AuthPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [isAnimating, setIsAnimating] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  // Login state
  const [loginData, setLoginData] = useState<LoginData>({
    email: "",
    password: "",
    remember: false,
  });

  // Register state
  const [registerData, setRegisterData] = useState<RegisterData>({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    agreements: {
      terms: false,
      privacy: false,
      marketing: false,
    },
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const validateLogin = () => {
    const newErrors: Record<string, string> = {};

    if (!loginData.email.trim()) {
      newErrors.email = "이메일을 입력해주세요";
    } else if (!/\S+@\S+\.\S+/.test(loginData.email)) {
      newErrors.email = "올바른 이메일 형식을 입력해주세요";
    }

    if (!loginData.password) {
      newErrors.password = "비밀번호를 입력해주세요";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validateRegister = () => {
    const newErrors: Record<string, string> = {};

    if (!registerData.name.trim()) {
      newErrors.name = "이름을 입력해주세요";
    }

    if (!registerData.email.trim()) {
      newErrors.email = "이메일을 입력해주세요";
    } else if (!/\S+@\S+\.\S+/.test(registerData.email)) {
      newErrors.email = "올바른 이메일 형식을 입력해주세요";
    }

    if (!registerData.password) {
      newErrors.password = "비밀번호를 입력해주세요";
    } else if (registerData.password.length < 8) {
      newErrors.password = "비밀번호는 최소 8자 이상이어야 합니다";
    }

    if (!registerData.confirmPassword) {
      newErrors.confirmPassword = "비밀번호 확인을 입력해주세요";
    } else if (registerData.password !== registerData.confirmPassword) {
      newErrors.confirmPassword = "비밀번호가 일치하지 않습니다";
    }

    if (!registerData.agreements.terms) {
      newErrors.terms = "이용약관에 동의해주세요";
    }

    if (!registerData.agreements.privacy) {
      newErrors.privacy = "개인정보처리방침에 동의해주세요";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateLogin()) return;

    setIsLoading(true);
    try {
      console.log("로그인 시도:", loginData);
      // API 호출 로직
    } catch (error) {
      console.error("로그인 오류:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegisterSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateRegister()) return;

    setIsLoading(true);
    try {
      console.log("회원가입 시도:", registerData);
      // API 호출 로직
    } catch (error) {
      console.error("회원가입 오류:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const clearError = (field: string) => {
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: "" }));
    }
  };

  const clearAllErrors = () => {
    setErrors({});
  };

  return (
    <AuthContainer>
      <BackButton />

      <AuthHeader
        isLogin={isLogin}
        setIsLogin={setIsLogin}
        isAnimating={isAnimating}
        setIsAnimating={setIsAnimating}
        clearAllErrors={clearAllErrors}
      />

      {/* Form Container */}
      <div
        className={`
          bg-slate-800/40 backdrop-blur-xl rounded-2xl border border-slate-700/50 shadow-2xl
          transition-all duration-300 transform
          ${isAnimating ? "scale-95 opacity-50" : "scale-100 opacity-100"}
        `}
      >
        <div className="p-8">
          {isLogin ? (
            <LoginForm
              loginData={loginData}
              setLoginData={setLoginData}
              errors={errors}
              clearError={clearError}
              onSubmit={handleLoginSubmit}
              isLoading={isLoading}
              showPassword={showPassword}
              setShowPassword={setShowPassword}
            />
          ) : (
            <RegisterForm
              registerData={registerData}
              setRegisterData={setRegisterData}
              errors={errors}
              clearError={clearError}
              onSubmit={handleRegisterSubmit}
              isLoading={isLoading}
              showPassword={showPassword}
              setShowPassword={setShowPassword}
              showConfirmPassword={showConfirmPassword}
              setShowConfirmPassword={setShowConfirmPassword}
            />
          )}

          <SocialLogin />
        </div>
      </div>
    </AuthContainer>
  );
}
