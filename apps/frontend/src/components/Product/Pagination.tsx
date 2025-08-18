"use client";
import { useRouter, useSearchParams } from "next/navigation";

type Props = { page: number; totalPages: number };
export default function Pagination({ page, totalPages }: Props) {
  const router = useRouter();
  const sp = useSearchParams();

  const setPage = (p: number) => {
    const qs = new URLSearchParams(sp.toString());
    qs.set("page", String(p));
    router.push(`/?${qs.toString()}`);
  };

  return (
    <div className="flex items-center justify-center gap-2">
      <button
        onClick={() => setPage(Math.max(0, page - 1))}
        disabled={page <= 0}
        className="rounded-lg border px-3 py-1 text-sm disabled:opacity-50"
      >
        이전
      </button>
      <span className="text-sm text-gray-600">
        {page + 1} / {Math.max(1, totalPages)}
      </span>
      <button
        onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
        disabled={page >= totalPages - 1}
        className="rounded-lg border px-3 py-1 text-sm disabled:opacity-50"
      >
        다음
      </button>
    </div>
  );
}
