"use client";

import { useEffect, useMemo, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { useProducts } from "@/hooks/useProducts";
import { useCategories } from "@/hooks/useCategories";
import ProductCard from "@/components/Product/ProductCard";
import Pagination from "@/components/Product/Pagination";

// shadcn/ui
import { Card, CardContent } from "@/components/ui/card";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";

type SortOrder = "ASC" | "DESC";

const SORT_FIELDS = [
  { value: "id", label: "최신순" },
  { value: "price", label: "가격" },
  { value: "name", label: "이름" },
] as const;

export default function HomePage() {
  const sp = useSearchParams();
  const router = useRouter();

  // ----- 초기값 해석 (타입 안전)
  const initKeyword = sp.get("keyword") ?? "";
  const initCategoryId = sp.get("categoryId");
  const initSortBy = sp.get("sortBy") ?? "id";
  const initSortOrder: SortOrder =
    sp.get("sortOrder") === "ASC" || sp.get("sortOrder") === "DESC"
      ? (sp.get("sortOrder") as SortOrder)
      : "DESC";
  const initPage = sp.get("page") ? Number(sp.get("page")) : 0;

  // ----- 상태
  const [keyword, setKeyword] = useState<string>(initKeyword);
  const [categoryId, setCategoryId] = useState<number | undefined>(
    initCategoryId ? Number(initCategoryId) : undefined
  );
  const [sortBy, setSortBy] = useState<string>(initSortBy);
  const [sortOrder, setSortOrder] = useState<SortOrder>(initSortOrder);
  const [page, setPage] = useState<number>(initPage);
  const size = 12;

  // ----- 데이터
  const { data: cats } = useCategories();
  const { data, isLoading, error } = useProducts({
    keyword: keyword || undefined,
    categoryId,
    page,
    size,
    sortBy,
    sortOrder,
  });

  // ----- URL 동기화
  useEffect(() => {
    const qs = new URLSearchParams();
    if (keyword) qs.set("keyword", keyword);
    if (categoryId !== undefined) qs.set("categoryId", String(categoryId));
    if (sortBy) qs.set("sortBy", sortBy);
    if (sortOrder) qs.set("sortOrder", sortOrder);
    if (page) qs.set("page", String(page));
    router.replace(`/?${qs.toString()}`);
  }, [keyword, categoryId, sortBy, sortOrder, page, router]);

  // ----- 표시용 요약
  const totalText = useMemo(() => {
    if (!data) return "";
    const total = (data as any).totalElements ?? undefined; // 훅 타입에 totalElements가 노출되면 any 제거 가능
    return typeof total === "number" ? `총 ${total.toLocaleString()}개` : "";
  }, [data]);

  const resetFilters = () => {
    setKeyword("");
    setCategoryId(undefined);
    setSortBy("id");
    setSortOrder("DESC");
    setPage(0);
  };

  return (
    <div className="space-y-6">
      {/* Hero + 필터 카드 */}
      <Card className="overflow-hidden border-none bg-gradient-to-br from-gray-900 to-black">
        <CardContent className="p-6 sm:p-10 text-white flex flex-col justify-between h-full gap-4">
          <h1 className="text-3xl font-extrabold">
            더 똑똑한 쇼핑, 지금 시작하세요
          </h1>

          <div className="flex justify-between items-center">
            {/* 카테고리 */}
            <div className="space-y-2">
              <Label className="text-white/80">카테고리</Label>
              <Select
                value={categoryId?.toString() ?? "all"}
                onValueChange={(v) => {
                  setPage(0);
                  setCategoryId(v === "all" ? undefined : Number(v));
                }}
              >
                <SelectTrigger className="bg-white/10 text-white ring-1 ring-white/15 placeholder:text-gray-300">
                  <SelectValue placeholder="전체 카테고리" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">전체 카테고리</SelectItem>
                  {cats?.map((c) => (
                    <SelectItem key={c.id} value={String(c.id)}>
                      {c.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* 검색어 */}
            <div className="space-y-2">
              <Label className="text-white/80">검색어</Label>
              <Input
                value={keyword}
                onChange={(e) => {
                  setPage(0);
                  setKeyword(e.target.value);
                }}
                placeholder="검색어를 입력하세요"
                className="bg-white/10 text-white placeholder:text-gray-300 ring-1 ring-white/15"
              />
            </div>

            {/* 정렬 필드 */}
            <div className="space-y-2">
              <Label className="text-white/80">정렬 기준</Label>
              <Select value={sortBy} onValueChange={(v) => setSortBy(v)}>
                <SelectTrigger className="bg-white/10 text-white ring-1 ring-white/15">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {SORT_FIELDS.map((f) => (
                    <SelectItem key={f.value} value={f.value}>
                      {f.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* 정렬 방향 */}
            <div className="space-y-2">
              <Label className="text-white/80">정렬 방향</Label>
              <Select
                value={sortOrder}
                onValueChange={(v) =>
                  setSortOrder(v === "ASC" ? "ASC" : "DESC")
                }
              >
                <SelectTrigger className="bg-white/10 text-white ring-1 ring-white/15">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="DESC">내림차순</SelectItem>
                  <SelectItem value="ASC">오름차순</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 상태 */}
      {isLoading && (
        <div className="py-20 text-center text-gray-500">불러오는 중…</div>
      )}
      {error && (
        <div className="py-20 text-center text-red-600">
          오류가 발생했습니다:{" "}
          {error instanceof Error ? error.message : "알 수 없는 오류"}
        </div>
      )}

      {/* 리스트 */}
      {data && (
        <section className="space-y-6">
          <div className="grid gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
            {data.content.map((p) => (
              <ProductCard key={p.id} p={p} />
            ))}
          </div>
          <Pagination page={data.number} totalPages={data.totalPages} />
        </section>
      )}
    </div>
  );
}
