export interface ProductResponse {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  imageUrl: string | string[] | null;
  categoryName: string | null;
  detailContent: string | null;
}

export interface ShippingResponse {
  shippingMethod: string | null;
  shippingFee: number | null;
  freeShippingThreshold: number | null;
  estimatedDeliveryDays: string | null;
  shippingProvider: string | null;
}

export interface CategoryResponse {
  id: number;
  name: string;
}

export interface TokenResponse {
  accessToken: string;
}

export interface OrderItemResponse {
  id: number;
  productId: number;
  name: string;
  price: number;
  quantity: number;
}
export interface OrderHistoryResponse {
  id: number;
  status: string;
  createdAt: string;
  totalPrice: number;
}
export interface OrderDetailResponse extends OrderHistoryResponse {
  items: OrderItemResponse[];
}

export interface CartItemResponse {
  id: number;
  productId: number;
  name: string;
  price: number;
  quantity: number;
  /** 백엔드가 배열/문자열/널을 모두 줄 수 있음 */
  imageUrl?: string | string[] | null;
}

export interface CartResponse {
  items: CartItemResponse[];
  totalPrice: number;
}
