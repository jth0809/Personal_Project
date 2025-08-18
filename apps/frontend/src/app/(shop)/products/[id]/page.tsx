"use client";
import { useParams } from "next/navigation";
import Image from "next/image";
import { useEffect, useMemo, useState } from "react";
import { useProduct } from "@/hooks/useProduct";
import { useShipping } from "@/hooks/useShipping";
import { useCart } from "@/hooks/useCart";
import { ProductJsonLd } from "@/components/seo/ProductJsonLd";

// shadcn/ui
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { AspectRatio } from "@/components/ui/aspect-ratio";

type SortableImage = string;

function normalizeImages(raw?: string | string[] | null): string[] {
  if (!raw) return [];
  if (Array.isArray(raw))
    return raw.filter((s) => typeof s === "string" && s.trim().length > 0);
  return raw.trim().length > 0 ? [raw] : [];
}

export default function ProductDetailPage() {
  const params = useParams();
  const parsedId = Number(params?.id);
  const id = Number.isFinite(parsedId) ? parsedId : -1;

  const { data: p, isLoading, error } = useProduct(id);
  const { data: shipping } = useShipping(id);
  const { add } = useCart();

  // 이미지 정규화 + 갤러리
  const images = useMemo<string[]>(
    () => normalizeImages(p?.imageUrl),
    [p?.imageUrl]
  );
  const [selected, setSelected] = useState<SortableImage | null>(null);
  const [imgErr, setImgErr] = useState(false);
  const primary = selected ?? images[0] ?? "/placeholder.png";

  // 상품 바뀔 때 썸네일 초기화
  useEffect(() => {
    setSelected(null);
    setImgErr(false);
  }, [id]);

  // 수량
  const [qty, setQty] = useState(1);
  useEffect(() => setQty(1), [id]); // 상품 전환 시 초기화
  const maxQty = Math.max(1, p?.stockQuantity ?? 99);
  const inc = () => setQty((q) => Math.min(maxQty, q + 1));
  const dec = () => setQty((q) => Math.max(1, q - 1));

  if (!Number.isFinite(id)) {
    return (
      <div className="py-20 text-center text-red-600">
        잘못된 상품 경로입니다.
      </div>
    );
  }

  if (isLoading) {
    return <div className="py-20 text-center text-gray-500">불러오는 중…</div>;
  }

  if (error || !p) {
    return (
      <div className="py-20 text-center text-red-600">
        상품을 불러오지 못했습니다.
      </div>
    );
  }

  const inStock = (p.stockQuantity ?? 0) > 0;

  return (
    <>
      <ProductJsonLd product={p} />
      <div className="grid gap-8 md:grid-cols-2">
        {/* 좌측: 이미지 갤러리 */}
        <Card className="overflow-hidden">
          <CardContent className="p-4">
            <div className="relative overflow-hidden rounded-xl bg-white">
              <AspectRatio ratio={1}>
                <Image
                  src={imgErr ? "/placeholder.png" : primary}
                  alt={(p.name || "상품 이미지") as string}
                  fill
                  priority
                  sizes="(min-width: 1024px) 50vw, 100vw"
                  className="object-cover"
                  onError={() => setImgErr(true)}
                />
              </AspectRatio>
            </div>

            {images.length > 1 && (
              <div className="mt-3 grid grid-cols-5 gap-2">
                {images.slice(0, 5).map((src, idx) => (
                  <button
                    key={`${src}-${idx}`}
                    type="button"
                    onClick={() => {
                      setSelected(src);
                      setImgErr(false);
                    }}
                    className={`relative h-16 overflow-hidden rounded-lg ring-1 ring-gray-200 ${
                      (selected ?? images[0]) === src
                        ? "outline outline-2 outline-black"
                        : ""
                    }`}
                    aria-label={`상품 썸네일 ${idx + 1}`}
                  >
                    <Image
                      src={src}
                      alt={`${p.name} 썸네일 ${idx + 1}`}
                      fill
                      className="object-cover"
                    />
                  </button>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* 우측: 정보/액션 */}
        <div className="space-y-6">
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-2xl font-extrabold">{p.name}</h1>
              <Badge variant={inStock ? "default" : "secondary"}>
                {inStock ? "재고 있음" : "일시 품절"}
              </Badge>
            </div>
            <p className="mt-1 text-sm text-gray-500">
              {p.categoryName || "기타"}
            </p>
          </div>

          <div className="text-3xl font-extrabold">
            {p.price.toLocaleString()}원
          </div>

          <p className="whitespace-pre-wrap text-sm text-gray-700">
            {p.detailContent || p.description || ""}
          </p>

          <Separator />

          {/* 배송 정보 */}
          <Card>
            <CardContent className="p-4 text-sm">
              <h2 className="mb-2 font-semibold">배송 정보</h2>
              {shipping ? (
                <ul className="grid gap-1 text-gray-600">
                  <li>방법: {shipping.shippingMethod || "기본"}</li>
                  <li>
                    배송비:{" "}
                    {shipping.shippingFee != null
                      ? `${shipping.shippingFee.toLocaleString()}원`
                      : "무료/조건부"}
                  </li>
                  <li>
                    무료 조건:{" "}
                    {shipping.freeShippingThreshold != null
                      ? `${shipping.freeShippingThreshold.toLocaleString()}원 이상`
                      : "-"}
                  </li>
                  <li>예상: {shipping.estimatedDeliveryDays || "1-3일"}</li>
                </ul>
              ) : (
                <p className="text-gray-500">배송 정보 없음</p>
              )}
            </CardContent>
          </Card>

          {/* 수량 + 액션 */}
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
            <div className="inline-flex items-center rounded-xl border bg-white">
              <Button
                type="button"
                variant="ghost"
                className="h-10 w-10"
                onClick={dec}
                disabled={!inStock}
              >
                −
              </Button>
              <span className="w-10 text-center text-sm">{qty}</span>
              <Button
                type="button"
                variant="ghost"
                className="h-10 w-10"
                onClick={inc}
                disabled={!inStock}
              >
                ＋
              </Button>
            </div>

            <div className="flex w-full gap-3">
              <Button
                onClick={() => add.mutate({ productId: id, quantity: qty })}
                className="w-full"
                disabled={!inStock || add.isPending}
              >
                {add.isPending ? "담는 중…" : "장바구니"}
              </Button>
              <Button className="w-full" variant="outline" disabled={!inStock}>
                바로구매
              </Button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
