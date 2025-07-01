import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Eye, EyeOff, Mail, Lock, User, AlertCircle } from "lucide-react";
import { RegisterFormProps } from "@/types/auth";

export default function RegisterForm({
  registerData,
  setRegisterData,
  errors,
  clearError,
  onSubmit,
  isLoading,
  showPassword,
  setShowPassword,
  showConfirmPassword,
  setShowConfirmPassword,
}: RegisterFormProps) {
  return (
    <form onSubmit={onSubmit} className="space-y-6">
      <div className="text-center mb-6">
        <h2 className="text-2xl font-bold text-white mb-2">계정 생성</h2>
        <p className="text-slate-400">새로운 계정을 만들어 쇼핑을 시작하세요</p>
      </div>

      {/* Name */}
      <div className="space-y-2">
        <Label htmlFor="register-name" className="text-slate-300">
          이름 *
        </Label>
        <div className="relative">
          <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
          <Input
            id="register-name"
            type="text"
            placeholder="홍길동"
            value={registerData.name}
            onChange={(e) => {
              setRegisterData((prev) => ({
                ...prev,
                name: e.target.value,
              }));
              clearError("name");
            }}
            className={`pl-10 bg-slate-700/50 border-slate-600 text-white placeholder-slate-400 focus:border-blue-500 focus:ring-blue-500/20 ${
              errors.name ? "border-red-500" : ""
            }`}
          />
        </div>
        {errors.name && (
          <p className="text-sm text-red-400 flex items-center">
            <AlertCircle className="h-3 w-3 mr-1" />
            {errors.name}
          </p>
        )}
      </div>

      {/* Email */}
      <div className="space-y-2">
        <Label htmlFor="register-email" className="text-slate-300">
          이메일 *
        </Label>
        <div className="relative">
          <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
          <Input
            id="register-email"
            type="email"
            placeholder="example@email.com"
            value={registerData.email}
            onChange={(e) => {
              setRegisterData((prev) => ({
                ...prev,
                email: e.target.value,
              }));
              clearError("email");
            }}
            className={`pl-10 bg-slate-700/50 border-slate-600 text-white placeholder-slate-400 focus:border-blue-500 focus:ring-blue-500/20 ${
              errors.email ? "border-red-500" : ""
            }`}
          />
        </div>
        {errors.email && (
          <p className="text-sm text-red-400 flex items-center">
            <AlertCircle className="h-3 w-3 mr-1" />
            {errors.email}
          </p>
        )}
      </div>

      {/* Password */}
      <div className="space-y-2">
        <Label htmlFor="register-password" className="text-slate-300">
          비밀번호 *
        </Label>
        <div className="relative">
          <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
          <Input
            id="register-password"
            type={showPassword ? "text" : "password"}
            placeholder="최소 8자 이상"
            value={registerData.password}
            onChange={(e) => {
              setRegisterData((prev) => ({
                ...prev,
                password: e.target.value,
              }));
              clearError("password");
            }}
            className={`pl-10 pr-10 bg-slate-700/50 border-slate-600 text-white placeholder-slate-400 focus:border-blue-500 focus:ring-blue-500/20 ${
              errors.password ? "border-red-500" : ""
            }`}
          />
          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-400 hover:text-slate-300"
          >
            {showPassword ? (
              <EyeOff className="h-4 w-4" />
            ) : (
              <Eye className="h-4 w-4" />
            )}
          </button>
        </div>
        {errors.password && (
          <p className="text-sm text-red-400 flex items-center">
            <AlertCircle className="h-3 w-3 mr-1" />
            {errors.password}
          </p>
        )}
      </div>

      {/* Confirm Password */}
      <div className="space-y-2">
        <Label htmlFor="register-confirm-password" className="text-slate-300">
          비밀번호 확인 *
        </Label>
        <div className="relative">
          <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
          <Input
            id="register-confirm-password"
            type={showConfirmPassword ? "text" : "password"}
            placeholder="비밀번호를 다시 입력하세요"
            value={registerData.confirmPassword}
            onChange={(e) => {
              setRegisterData((prev) => ({
                ...prev,
                confirmPassword: e.target.value,
              }));
              clearError("confirmPassword");
            }}
            className={`pl-10 pr-10 bg-slate-700/50 border-slate-600 text-white placeholder-slate-400 focus:border-blue-500 focus:ring-blue-500/20 ${
              errors.confirmPassword ? "border-red-500" : ""
            }`}
          />
          <button
            type="button"
            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-400 hover:text-slate-300"
          >
            {showConfirmPassword ? (
              <EyeOff className="h-4 w-4" />
            ) : (
              <Eye className="h-4 w-4" />
            )}
          </button>
        </div>
        {errors.confirmPassword && (
          <p className="text-sm text-red-400 flex items-center">
            <AlertCircle className="h-3 w-3 mr-1" />
            {errors.confirmPassword}
          </p>
        )}
      </div>

      {/* Agreements */}
      <div className="space-y-3">
        <div className="flex items-center space-x-2">
          <Checkbox
            id="terms"
            checked={registerData.agreements.terms}
            onCheckedChange={(checked) => {
              setRegisterData((prev) => ({
                ...prev,
                agreements: {
                  ...prev.agreements,
                  terms: checked as boolean,
                },
              }));
              clearError("terms");
            }}
            className="border-slate-600 data-[state=checked]:bg-blue-500 data-[state=checked]:border-blue-500"
          />
          <Label htmlFor="terms" className="text-sm text-slate-300">
            <span className="text-red-400">*</span> 이용약관에 동의합니다{" "}
            <Link href="/terms" className="text-blue-400 hover:underline">
              (보기)
            </Link>
          </Label>
        </div>
        {errors.terms && (
          <p className="text-sm text-red-400 flex items-center ml-6">
            <AlertCircle className="h-3 w-3 mr-1" />
            {errors.terms}
          </p>
        )}

        <div className="flex items-center space-x-2">
          <Checkbox
            id="privacy"
            checked={registerData.agreements.privacy}
            onCheckedChange={(checked) => {
              setRegisterData((prev) => ({
                ...prev,
                agreements: {
                  ...prev.agreements,
                  privacy: checked as boolean,
                },
              }));
              clearError("privacy");
            }}
            className="border-slate-600 data-[state=checked]:bg-blue-500 data-[state=checked]:border-blue-500"
          />
          <Label htmlFor="privacy" className="text-sm text-slate-300">
            <span className="text-red-400">*</span> 개인정보처리방침에
            동의합니다{" "}
            <Link href="/privacy" className="text-blue-400 hover:underline">
              (보기)
            </Link>
          </Label>
        </div>
        {errors.privacy && (
          <p className="text-sm text-red-400 flex items-center ml-6">
            <AlertCircle className="h-3 w-3 mr-1" />
            {errors.privacy}
          </p>
        )}

        <div className="flex items-center space-x-2">
          <Checkbox
            id="marketing"
            checked={registerData.agreements.marketing}
            onCheckedChange={(checked) =>
              setRegisterData((prev) => ({
                ...prev,
                agreements: {
                  ...prev.agreements,
                  marketing: checked as boolean,
                },
              }))
            }
            className="border-slate-600 data-[state=checked]:bg-blue-500 data-[state=checked]:border-blue-500"
          />
          <Label htmlFor="marketing" className="text-sm text-slate-300">
            마케팅 정보 수신에 동의합니다 (선택)
          </Label>
        </div>
      </div>

      {/* Submit Button */}
      <Button
        type="submit"
        disabled={isLoading}
        className="w-full bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 text-white font-medium py-3 rounded-xl shadow-lg shadow-blue-500/25 transition-all duration-200 hover:shadow-blue-500/40 hover:scale-[1.02]"
      >
        {isLoading ? (
          <div className="flex items-center justify-center space-x-2">
            <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
            <span>계정 생성 중...</span>
          </div>
        ) : (
          "계정 생성"
        )}
      </Button>
    </form>
  );
}
