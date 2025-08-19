"use client";
import { useState } from "react";
import { useSignup } from "@/hooks/useAuth";
import { useRouter } from "next/navigation";

export default function SignupPage() {
  const router = useRouter();
  const signup = useSignup();
  const [email, setEmail] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    signup.mutate(
      { email, password, name },
      { onSuccess: () => router.push("/login") }
    );
  };

  return (
    <div className="mx-auto max-w-md rounded-2xl border bg-white p-6 shadow-sm">
      <h1 className="mb-4 text-xl font-extrabold">회원가입</h1>
      <form onSubmit={submit} className="space-y-3">
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="이름"
          className="w-full rounded-xl border px-4 py-2 text-sm"
        />
        <input
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="이메일"
          className="w-full rounded-xl border px-4 py-2 text-sm"
        />
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="비밀번호"
          className="w-full rounded-xl border px-4 py-2 text-sm"
        />
        <button
          disabled={signup.isPending}
          className="w-full rounded-xl bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-black disabled:opacity-50"
        >
          {signup.isPending ? "가입 중…" : "가입하기"}
        </button>
      </form>
      {signup.error && (
        <p className="mt-3 text-sm text-red-600">{signup.error.message}</p>
      )}
    </div>
  );
}
