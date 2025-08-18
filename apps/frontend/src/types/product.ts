export interface Product {
  id: string;
  name: string;
  price: number;
  originalPrice: number;
  image: string;
  images?: string[];
  category: string;
  rating: number;
  reviews: number;
  tags: string[];
  description: string;
  features?: string[];
  inStock?: boolean;
  stockCount?: number;
  colors?: string[];
  sizes?: string[];
}

export interface CreateProductPayload {
  name: string;
  description?: string;
  price: number;
  imageUrl: string[]; // presigned 업로드 완료 후의 공개 URL들
  categoryId: number;
  stockQuantity: number;
}

export interface UpdateProductPayload extends CreateProductPayload {}
