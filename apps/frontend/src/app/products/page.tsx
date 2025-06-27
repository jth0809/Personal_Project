"use client";

import { useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Search, Filter, Grid, List } from "lucide-react";
import ProductCard from "@/components/Main/ProductCard";
import {
  categories,
  searchProducts,
  getProductsByCategory,
} from "@/contents/products";
import { Product } from "@/types";

type SortOption = "latest" | "price-low" | "price-high" | "rating" | "reviews";
type ViewMode = "grid" | "list";

export default function ProductsPage() {
  const [selectedCategory, setSelectedCategory] = useState("all");
  const [searchQuery, setSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState<SortOption>("latest");
  const [viewMode, setViewMode] = useState<ViewMode>("grid");

  // 상품 필터링 및 정렬
  const getFilteredAndSortedProducts = (): Product[] => {
    let products = searchQuery
      ? searchProducts(searchQuery)
      : getProductsByCategory(selectedCategory);

    // 정렬
    switch (sortBy) {
      case "price-low":
        products = products.sort((a, b) => a.price - b.price);
        break;
      case "price-high":
        products = products.sort((a, b) => b.price - a.price);
        break;
      case "rating":
        products = products.sort((a, b) => b.rating - a.rating);
        break;
      case "reviews":
        products = products.sort((a, b) => b.reviews - a.reviews);
        break;
      default:
        // latest - 기본 순서 유지
        break;
    }

    return products;
  };

  const filteredProducts = getFilteredAndSortedProducts();

  const handleAddToCart = (productId: string) => {
    console.log("장바구니에 추가:", productId);
    // 장바구니 추가 로직 구현
  };

  const handleAddToWishlist = (productId: string) => {
    console.log("찜 목록에 추가:", productId);
    // 찜 목록 추가 로직 구현
  };

  return (
    <div className="bg-gray-50 min-h-screen">
      {/* Breadcrumb */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <nav className="text-sm text-gray-600">
          <Link href="/" className="hover:text-gray-900">
            홈
          </Link>
          <span className="mx-2">/</span>
          <span className="text-gray-900">전체 상품</span>
        </nav>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-16">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">전체 상품</h1>
          <p className="text-gray-600">
            다양한 카테고리의 특별한 굿즈들을 만나보세요
          </p>
        </div>

        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar - Categories */}
          <div className="lg:w-64 space-y-6">
            {/* Search */}
            <div className="bg-white p-4 rounded-lg shadow-sm">
              <h3 className="font-semibold text-gray-900 mb-3">상품 검색</h3>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                <Input
                  placeholder="상품명, 카테고리 검색..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            {/* Categories */}
            <div className="bg-white p-4 rounded-lg shadow-sm">
              <h3 className="font-semibold text-gray-900 mb-3">카테고리</h3>
              <div className="space-y-2">
                {categories.map((category) => (
                  <button
                    key={category.id}
                    onClick={() => {
                      setSelectedCategory(category.id);
                      setSearchQuery("");
                    }}
                    className={`w-full flex items-center justify-between p-2 rounded-lg text-left transition-colors ${
                      selectedCategory === category.id
                        ? "bg-blue-50 text-blue-600 border border-blue-200"
                        : "hover:bg-gray-50 text-gray-700"
                    }`}
                  >
                    <span>{category.name}</span>
                    <Badge variant="secondary" className="text-xs">
                      {category.count}
                    </Badge>
                  </button>
                ))}
              </div>
            </div>

            {/* Filters */}
            <div className="bg-white p-4 rounded-lg shadow-sm">
              <h3 className="font-semibold text-gray-900 mb-3">
                <Filter className="inline h-4 w-4 mr-2" />
                필터
              </h3>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    정렬 기준
                  </label>
                  <Select
                    value={sortBy}
                    onValueChange={(value: SortOption) => setSortBy(value)}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="latest">최신순</SelectItem>
                      <SelectItem value="price-low">낮은 가격순</SelectItem>
                      <SelectItem value="price-high">높은 가격순</SelectItem>
                      <SelectItem value="rating">평점순</SelectItem>
                      <SelectItem value="reviews">리뷰 많은 순</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </div>
          </div>

          {/* Main Content */}
          <div className="flex-1">
            {/* Toolbar */}
            <div className="bg-white p-4 rounded-lg shadow-sm mb-6">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-4">
                  <span className="text-sm text-gray-600">
                    총{" "}
                    <span className="font-semibold text-gray-900">
                      {filteredProducts.length}
                    </span>
                    개 상품
                  </span>
                  {searchQuery && (
                    <Badge variant="outline">
                      &apos;{searchQuery}&apos; 검색 결과
                    </Badge>
                  )}
                  {selectedCategory !== "all" && (
                    <Badge variant="outline">
                      {categories.find((c) => c.id === selectedCategory)?.name}
                    </Badge>
                  )}
                </div>
                <div className="flex items-center space-x-2">
                  <Button
                    variant={viewMode === "grid" ? "default" : "outline"}
                    size="sm"
                    onClick={() => setViewMode("grid")}
                  >
                    <Grid className="h-4 w-4" />
                  </Button>
                  <Button
                    variant={viewMode === "list" ? "default" : "outline"}
                    size="sm"
                    onClick={() => setViewMode("list")}
                  >
                    <List className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </div>

            {/* Products Grid */}
            {filteredProducts.length > 0 ? (
              <div
                className={`grid gap-6 ${
                  viewMode === "grid"
                    ? "grid-cols-1 sm:grid-cols-2 lg:grid-cols-3"
                    : "grid-cols-1"
                }`}
              >
                {filteredProducts.map((product) => (
                  <Link key={product.id} href={`/product/${product.id}`}>
                    <ProductCard
                      product={product}
                      onAddToCart={handleAddToCart}
                      onAddToWishlist={handleAddToWishlist}
                    />
                  </Link>
                ))}
              </div>
            ) : (
              <div className="bg-white rounded-lg shadow-sm p-12 text-center">
                <div className="text-gray-400 mb-4">
                  <Search className="h-16 w-16 mx-auto" />
                </div>
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  검색 결과가 없습니다
                </h3>
                <p className="text-gray-600 mb-6">
                  다른 키워드로 검색하거나 카테고리를 변경해보세요
                </p>
                <Button
                  onClick={() => {
                    setSearchQuery("");
                    setSelectedCategory("all");
                  }}
                >
                  전체 상품 보기
                </Button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
