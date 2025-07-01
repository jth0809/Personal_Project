import { Button } from "@/components/ui/button";
import { FaGoogle, FaFacebook } from "react-icons/fa";

export default function SocialLogin() {
  const handleGoogleLogin = () => {
    console.log("Google 로그인");
    // Google OAuth 로직
  };

  const handleFacebookLogin = () => {
    console.log("Facebook 로그인");
    // Facebook OAuth 로직
  };

  return (
    <div className="mt-8">
      <div className="relative">
        <div className="absolute inset-0 flex items-center">
          <div className="w-full border-t border-slate-600"></div>
        </div>
        <div className="relative flex justify-center text-sm">
          <span className="px-2 bg-slate-800/40 text-slate-400">또는</span>
        </div>
      </div>

      <div className="mt-6 grid grid-cols-2 gap-3">
        <Button
          variant="outline"
          onClick={handleGoogleLogin}
          className="border-slate-600 bg-slate-700/30 text-slate-300 hover:bg-slate-700/50 hover:text-white"
        >
          <FaGoogle className="w-4 h-4 mr-2" />
          Google
        </Button>

        <Button
          variant="outline"
          onClick={handleFacebookLogin}
          className="border-slate-600 bg-slate-700/30 text-slate-300 hover:bg-slate-700/50 hover:text-white"
        >
          <FaFacebook className="w-4 h-4 mr-2" />
          Facebook
        </Button>
      </div>
    </div>
  );
}
