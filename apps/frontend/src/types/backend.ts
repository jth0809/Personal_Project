// 공통 페이지네이션
export interface SortObject {
  unsorted: boolean;
  sorted: boolean;
  empty: boolean;
}
export interface PageableObject {
  offset: number;
  unpaged: boolean;
  sort: SortObject;
  paged: boolean;
  pageNumber: number;
  pageSize: number;
}
export interface Page<T> {
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  sort: SortObject;
  pageable: PageableObject;
  size: number;
  content: T[];
  empty: boolean;
}

/** ===== 리뷰 ===== */
export interface ReviewCreateRequest {
  productId: number; // int64
  rating: number; // 1~5
  comment: string;
}
export interface ReviewUpdateRequest {
  rating: number; // 1~5
  comment: string;
}
export interface ReviewResponse {
  id: number; // int64
  rating: number;
  comment: string;
  authorName: string;
}
export type PageReviewResponse = Page<ReviewResponse>;

/** ===== QnA ===== */
export interface QnaCreateRequest {
  productId: number; // int64
  question: string;
}
export interface QnaAnswerRequest {
  answer: string;
}
export interface QnaResponse {
  id: number; // int64
  question: string;
  answer: string;
  authorName: string;
  createdAt: string; // date-time
  answeredAt: string; // date-time
}
export type PageQnaResponse = Page<QnaResponse>;

/** ===== 상품 ===== */
export interface ProductCreateRequest {
  name: string;
  description?: string;
  price: number; // >= 1
  imageUrl: string[];
  categoryId: number; // int64
  stockQuantity: number; // >= 0
}
export interface ProductUpdateRequest {
  name?: string;
  description?: string;
  price?: number;
  imageUrl?: string[];
  categoryId?: number; // int64
  stockQuantity?: number; // >= 0
}
export interface ProductResponse {
  id: number; // int64
  name: string;
  description: string | null;
  price: number;
  stockQuantity: number;
  imageUrl: string[]; // 백엔드가 배열로 내려준다 가정
  categoryName: string | null;
  detailContent: string | null;
}
export type PageProductResponse = Page<ProductResponse>;

/** ===== 카테고리 ===== */
export interface CategoryResponse {
  id: number; // int64
  name: string;
}

/** ===== 이미지 업로드 ===== */
export interface GenerateUploadUrlsRequest {
  fileNames: string[];
}
export interface UploadInfoResponse {
  fileName: string;
  uploadUrl: string;
  imageUrl: string;
}
export interface GenerateUploadUrlRequest {
  fileName: string;
}
export interface GenerateUploadUrlResponse {
  uploadUrl: string;
  imageUrl: string;
}

/** ===== 장바구니 ===== */
export interface CartAddItemRequest {
  productId: number; // int64
  quantity: number; // >=1
}
export interface CartItemResponse {
  cartItemId: number; // int64
  productId: number; // int64
  productName: string;
  price: number;
  imageUrl?: string | null;
  quantity: number;
}
export interface CartResponse {
  items: CartItemResponse[];
  totalPrice: number;
}

/** ===== 주문/결제 ===== */
export interface OrderItemRequest {
  productId: number; // int64
  count: number; // >=1
}
export interface OrderCreateRequest {
  orderItems: OrderItemRequest[]; // length >=1
}
export interface OrderItemResponse {
  productName: string;
  count: number;
  orderPrice: number;
}
export interface OrderHistoryResponse {
  orderId: number; // int64
  orderDate: string; // date-time
  orderStatus: string;
  orderItems: OrderItemResponse[];
}
export type PageOrderHistoryResponse = Page<OrderHistoryResponse>;

export interface VerificationRequest {
  provider: string;
  paymentKey: string;
  orderId: string;
  amount: number;
}
export interface ConfirmationResponse {
  status: string;
  orderId: string;
  totalAmount: number;
}

/** ===== 인증 ===== */
export interface SignupRequest {
  email: string;
  password: string; // >= 12
  username: string;
}
export interface LoginRequest {
  email: string;
  password: string; // >= 12
}
export interface TokenResponse {
  accessToken: string;
}

/** ===== 배송 ===== */
export interface ShippingResponse {
  shippingMethod: string | null;
  shippingFee: number | null;
  freeShippingThreshold: number | null;
  estimatedDeliveryDays: string | null;
  shippingProvider: string | null;
}

/** ===== 주문 취소 ===== */
export interface OrderCancelRequest {
  reason: string;
}

export interface QueryProviderProps {
  children: React.ReactNode;
}
