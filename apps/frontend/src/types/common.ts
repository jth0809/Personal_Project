import { ReactNode } from "react";

export interface LayoutProps {
  children: ReactNode;
}

export interface QueryProviderProps {
  children: ReactNode;
}

export interface AuthProviderProps {
  children: ReactNode;
}

export interface ApiResponse<T> {
  data: T;
  message: string;
  success: boolean;
}

export interface PaginationParams {
  page: number;
  limit: number;
}

export interface SearchParams {
  query: string;
  category?: string;
  sortBy?: "price" | "rating" | "reviews" | "name";
  sortOrder?: "asc" | "desc";
}
