// lib/images.ts
import { apiFetch } from "@/lib/apiClient";

export type PresignedSingle = { uploadUrl: string; imageUrl: string };

// 단일 presigned 발급
export async function getUploadUrl(fileName: string): Promise<PresignedSingle> {
  return apiFetch<PresignedSingle>(`/images/generate-upload-url`, {
    method: "POST",
    body: JSON.stringify({ fileName }),
  });
}

// 파일 1개 즉시 업로드 → imageUrl 반환
export async function uploadOneImmediately(file: File): Promise<string> {
  const fileName = `${Date.now()}_${file.name}`;
  const { uploadUrl, imageUrl } = await getUploadUrl(fileName);
  await fetch(uploadUrl, {
    method: "PUT",
    headers: { "Content-Type": file.type },
    body: file,
  });
  return imageUrl;
}

// 파일 여러 개 즉시 업로드
export async function uploadManyImmediately(files: File[]): Promise<string[]> {
  if (!files.length) return [];
  return Promise.all(files.map(uploadOneImmediately));
}
