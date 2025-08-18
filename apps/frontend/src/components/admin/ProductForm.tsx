// components/admin/ProductForm.tsx
"use client";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { uploadManyImmediately } from "@/lib/images";
import type { ProductResponse } from "@/types/apiModels";
import type {
  CreateProductPayload,
  UpdateProductPayload,
} from "@/types/product";

const Schema = z.object({
  name: z.string().min(2, "2자 이상"),
  description: z.string().optional(),
  price: z.preprocess((v) => Number(v), z.number().nonnegative()),
  categoryId: z.preprocess((v) => Number(v), z.number().int().positive()),
  stockQuantity: z.preprocess((v) => Number(v), z.number().int().nonnegative()),
});

type ProductFormValues = z.infer<typeof Schema>;

export default function ProductForm({
  mode,
  initial,
  onSubmit,
}: {
  mode: "create" | "edit";
  initial?: ProductResponse;
  onSubmit: (
    payload: CreateProductPayload | UpdateProductPayload,
    ctx: { deletedImages: string[] }
  ) => Promise<void> | void;
}) {
  // 기존(서버에 이미 등록된) 이미지
  const [existingImages, setExistingImages] = useState<string[]>(() =>
    Array.isArray(initial?.imageUrl)
      ? (initial?.imageUrl as any)
      : initial?.imageUrl
        ? [String(initial.imageUrl)]
        : []
  );
  // 이번 폼에서 새로 업로드된 이미지
  const [uploadedImages, setUploadedImages] = useState<string[]>([]);
  const [deletedImages, setDeletedImages] = useState<string[]>([]);
  const [isUploading, setIsUploading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<ProductFormValues>({
    resolver: zodResolver(Schema) as any,
    defaultValues: initial
      ? {
          name: initial.name,
          description: initial.description || initial.detailContent || "",
          price: initial.price,
          categoryId: (initial as any).categoryId ?? 0,
          stockQuantity: initial.stockQuantity ?? 0,
        }
      : {
          name: "",
          description: "",
          price: 0,
          categoryId: 0,
          stockQuantity: 0,
        },
  });

  useEffect(() => {
    if (initial) {
      reset({
        name: initial.name,
        description: initial.description || initial.detailContent || "",
        price: initial.price,
        categoryId: (initial as any).categoryId ?? 0,
        stockQuantity: initial.stockQuantity ?? 0,
      });
      const imgs = Array.isArray(initial.imageUrl)
        ? (initial.imageUrl as any)
        : initial.imageUrl
          ? [String(initial.imageUrl)]
          : [];
      setExistingImages(imgs);
      setUploadedImages([]); // 수정화면에서 새로 들어온 업로드는 리셋
      setDeletedImages([]);
    }
  }, [initial, reset]);

  const onRemoveExisting = async (img: string) => {
    if (!initial) {
      setExistingImages((arr) => arr.filter((u) => u !== img));
      return;
    }
    // 서버에 연결된 기존 이미지 즉시 삭제
    // await deleteProductImage(initial.id, img);
    setExistingImages((arr) => arr.filter((u) => u !== img));
    setDeletedImages((arr) => (arr.includes(img) ? arr : [...arr, img]));
  };

  const onRemoveUploaded = (img: string) => {
    // 새 업로드건은 서버에 아직 연결 전이므로 클라이언트 배열에서만 제거
    setUploadedImages((arr) => arr.filter((u) => u !== img));
  };

  // 파일 선택 → 즉시 업로드
  const onFilesSelected: React.ChangeEventHandler<HTMLInputElement> = async (
    e
  ) => {
    const files = e.target.files ? Array.from(e.target.files) : [];
    if (!files.length) return;
    setIsUploading(true);
    try {
      const urls = await uploadManyImmediately(files);
      setUploadedImages((prev) => [...prev, ...urls]);
    } catch (err) {
      console.error("[image-upload] 실패", err);
      alert("이미지 업로드에 실패했습니다.");
    } finally {
      setIsUploading(false);
      // 같은 파일 다시 선택 가능하도록 초기화
      e.target.value = "";
    }
  };

  const submit = handleSubmit(async (values) => {
    // 업로드 중이면 제출 방지
    if (isUploading) return;

    const payload: CreateProductPayload = {
      name: values.name,
      description: values.description || "",
      price: values.price,
      imageUrl: [...existingImages, ...uploadedImages], // 즉시 업로드한 URL 그대로 사용
      categoryId: values.categoryId,
      stockQuantity: values.stockQuantity,
    };
    await onSubmit(payload, { deletedImages });
  });

  return (
    <form
      onSubmit={submit}
      className="space-y-5"
      aria-busy={isUploading || isSubmitting}
    >
      <div className="grid gap-4 sm:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="name">상품명</Label>
          <Input id="name" {...register("name")} />
          {errors.name && (
            <p className="text-xs text-red-600">{errors.name.message}</p>
          )}
        </div>
        <div className="space-y-2">
          <Label htmlFor="price">가격(원)</Label>
          <Input id="price" type="number" min={0} {...register("price")} />
          {errors.price && (
            <p className="text-xs text-red-600">{errors.price.message}</p>
          )}
        </div>
        <div className="space-y-2">
          <Label htmlFor="categoryId">카테고리</Label>
          <Input
            id="categoryId"
            type="number"
            min={1}
            {...register("categoryId")}
          />
          {errors.categoryId && (
            <p className="text-xs text-red-600">{errors.categoryId.message}</p>
          )}
        </div>
        <div className="space-y-2">
          <Label htmlFor="stockQuantity">재고</Label>
          <Input
            id="stockQuantity"
            type="number"
            min={0}
            {...register("stockQuantity")}
          />
          {errors.stockQuantity && (
            <p className="text-xs text-red-600">
              {errors.stockQuantity.message}
            </p>
          )}
        </div>

        <div className="col-span-full space-y-2">
          <Label htmlFor="description">설명</Label>
          <Textarea id="description" rows={5} {...register("description")} />
        </div>

        <div className="col-span-full space-y-2">
          <Label htmlFor="files">이미지 업로드 (선택 즉시 업로드)</Label>
          <Input
            id="files"
            type="file"
            multiple
            accept="image/*"
            onChange={onFilesSelected}
          />
          <p className="text-xs text-gray-500">
            {isUploading ? "이미지 업로드 중…" : "여러 장 선택 가능"}
          </p>

          {/* 기존 이미지 */}
          {existingImages.length > 0 && (
            <>
              <p className="mt-2 text-xs text-gray-500">기존 이미지</p>
              <ul className="mt-1 grid grid-cols-2 gap-3 sm:grid-cols-4">
                {existingImages.map((url) => (
                  <li key={url} className="group relative">
                    {/* eslint-disable-next-line @next/next/no-img-element */}
                    <img
                      src={url}
                      alt="기존 이미지"
                      className="h-28 w-full rounded-lg object-cover"
                    />
                    <button
                      type="button"
                      onClick={() => onRemoveExisting(url)}
                      className="absolute right-2 top-2 rounded bg-black/60 px-2 py-1 text-xs text-white opacity-0 transition group-hover:opacity-100"
                    >
                      삭제
                    </button>
                  </li>
                ))}
              </ul>
            </>
          )}

          {/* 이번에 업로드된 이미지 */}
          {uploadedImages.length > 0 && (
            <>
              <p className="mt-4 text-xs text-gray-500">
                이번에 업로드된 이미지
              </p>
              <ul className="mt-1 grid grid-cols-2 gap-3 sm:grid-cols-4">
                {uploadedImages.map((url) => (
                  <li key={url} className="group relative">
                    {/* eslint-disable-next-line @next/next/no-img-element */}
                    <img
                      src={url}
                      alt="새로 업로드된 이미지"
                      className="h-28 w-full rounded-lg object-cover"
                    />
                    <button
                      type="button"
                      onClick={() => onRemoveUploaded(url)}
                      className="absolute right-2 top-2 rounded bg-black/60 px-2 py-1 text-xs text-white opacity-0 transition group-hover:opacity-100"
                    >
                      제거
                    </button>
                  </li>
                ))}
              </ul>
            </>
          )}
        </div>
      </div>

      <div className="flex justify-end gap-3">
        <Button type="submit" disabled={isSubmitting || isUploading}>
          {isUploading
            ? "업로드 중…"
            : mode === "create"
              ? "상품 생성"
              : "상품 수정"}
        </Button>
      </div>
    </form>
  );
}
