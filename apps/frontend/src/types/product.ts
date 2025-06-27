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
