import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Eye, EyeOff, Mail, Lock, AlertCircle } from "lucide-react";
import { LoginFormProps } from "@/types/auth";

export default function LoginForm({
  loginData,
  setLoginData,
  errors,
  clearError,
  onSubmit,
  isLoading,
  showPassword,
  setShowPassword,
}: LoginFormProps) {
  return (
    <form onSubmit={onSubmit} className="space-y-6">
      <div className="text-center mb-6">
        <h2 className="text-2xl font-bold text-white mb-2">환영합니다</h2>
        <p className="text-slate-400">계정에 로그인하여 쇼핑을 계속하세요</p>
      </div>

      {/* Email */}
      <div className="space-y-2">
        <Label htmlFor="login-email" className="text-slate-300">
          이메일
        </Label>
        <div className="relative">
          <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
          <Input
            id="login-email"
            type="email"
            placeholder="example@email.com"
            value={loginData.email}
            onChange={(e) => {
              setLoginData((prev) => ({
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
        <Label htmlFor="login-password" className="text-slate-300">
          비밀번호
        </Label>
        <div className="relative">
          <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
          <Input
            id="login-password"
            type={showPassword ? "text" : "password"}
            placeholder="비밀번호를 입력하세요"
            value={loginData.password}
            onChange={(e) => {
              setLoginData((prev) => ({
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

      {/* Remember & Forgot */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <Checkbox
            id="remember"
            checked={loginData.remember}
            onCheckedChange={(checked) =>
              setLoginData((prev) => ({
                ...prev,
                remember: checked as boolean,
              }))
            }
            className="border-slate-600 data-[state=checked]:bg-blue-500 data-[state=checked]:border-blue-500"
          />
          <Label htmlFor="remember" className="text-sm text-slate-300">
            로그인 상태 유지
          </Label>
        </div>
        <Link
          href="/forgot-password"
          className="text-sm text-blue-400 hover:text-blue-300"
        >
          비밀번호 찾기
        </Link>
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
            <span>로그인 중...</span>
          </div>
        ) : (
          "로그인"
        )}
      </Button>
    </form>
  );
}
