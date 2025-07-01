import { Sparkles } from "lucide-react";
import { AuthProps } from "@/types/auth";

interface AuthHeaderProps extends AuthProps {
  clearAllErrors?: () => void;
}

export default function AuthHeader({
  isLogin,
  setIsLogin,
  isAnimating,
  setIsAnimating,
  clearAllErrors,
}: AuthHeaderProps) {
  const toggleMode = () => {
    setIsAnimating(true);
    setTimeout(() => {
      setIsLogin(!isLogin);
      clearAllErrors?.();
      setTimeout(() => setIsAnimating(false), 100);
    }, 200);
  };

  return (
    <div className="text-center mb-8">
      <div className="flex items-center justify-center space-x-2 mb-6">
        <Sparkles className="h-8 w-8 text-blue-400" />
        <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-400 to-cyan-400 bg-clip-text text-transparent">
          GOODSSHOP
        </h1>
      </div>

      <div className="flex items-center justify-center bg-slate-800/50 backdrop-blur-sm rounded-full p-1 mb-6 border border-slate-700">
        <button
          onClick={() => (!isAnimating && isLogin ? null : toggleMode())}
          className={`px-6 py-2 rounded-full text-sm font-medium transition-all duration-300 ${
            isLogin
              ? "bg-gradient-to-r from-blue-500 to-cyan-500 text-white shadow-lg shadow-blue-500/25"
              : "text-slate-400 hover:text-white"
          }`}
        >
          로그인
        </button>
        <button
          onClick={() => (!isAnimating && !isLogin ? null : toggleMode())}
          className={`px-6 py-2 rounded-full text-sm font-medium transition-all duration-300 ${
            !isLogin
              ? "bg-gradient-to-r from-blue-500 to-cyan-500 text-white shadow-lg shadow-blue-500/25"
              : "text-slate-400 hover:text-white"
          }`}
        >
          회원가입
        </button>
      </div>
    </div>
  );
}
