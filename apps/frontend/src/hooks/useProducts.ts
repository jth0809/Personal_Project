import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Product } from "@/types";

// API 함수들
const fetchProducts = async (): Promise<Product[]> => {
  // 실제 API 호출 대신 모의 데이터 사용
  await new Promise((resolve) => setTimeout(resolve, 1000));

  // 실제 구현에서는 다음과 같이 사용:
  // const response = await fetch('/api/products');
  // if (!response.ok) throw new Error('Failed to fetch products');
  // return response.json();

  return [
    {
      id: "1",
      name: "프리미엄 에코백",
      price: 25000,
      originalPrice: 30000,
      image: "/next.svg",
      images: ["/next.svg", "/vercel.svg", "/globe.svg"],
      category: "가방",
      rating: 4.8,
      reviews: 156,
      tags: ["베스트셀러", "친환경"],
      description:
        "고급 캔버스 소재로 제작된 친환경 에코백입니다. 내구성이 뛰어나며 일상 사용에 완벽한 크기입니다.",
      features: [
        "100% 유기농 캔버스 소재",
        "내부 포켓 2개",
        "40cm x 35cm x 10cm",
        "최대 5kg 수용 가능",
      ],
      inStock: true,
      stockCount: 25,
      colors: ["네이비", "베이지", "블랙"],
      sizes: ["원 사이즈"],
    },
    {
      id: "2",
      name: "유니크 머그컵",
      price: 18000,
      originalPrice: 22000,
      image: "/next.svg",
      category: "생활용품",
      rating: 4.6,
      reviews: 89,
      tags: ["신상품"],
      description: "감성적인 디자인의 세라믹 머그컵",
      inStock: true,
      stockCount: 15,
      colors: ["화이트", "블랙", "핑크"],
      sizes: ["360ml"],
    },
    {
      id: "3",
      name: "스타일 키링",
      price: 12000,
      originalPrice: 15000,
      image: "/next.svg",
      category: "액세서리",
      rating: 4.9,
      reviews: 203,
      tags: ["인기상품"],
      description: "고급 메탈 소재의 스타일리시한 키링",
      inStock: true,
      stockCount: 50,
      colors: ["실버", "골드", "로즈골드"],
      sizes: ["원 사이즈"],
    },
    {
      id: "4",
      name: "아트 포스터",
      price: 35000,
      originalPrice: 40000,
      image: "/next.svg",
      category: "인테리어",
      rating: 4.7,
      reviews: 124,
      tags: ["한정판"],
      description: "독창적인 디자인의 아트 포스터",
      inStock: true,
      stockCount: 8,
      sizes: ["A3", "A2"],
    },
    {
      id: "5",
      name: "코튼 티셔츠",
      price: 28000,
      originalPrice: 35000,
      image: "/next.svg",
      category: "의류",
      rating: 4.5,
      reviews: 78,
      tags: ["베스트셀러"],
      description: "100% 유기농 코튼으로 제작된 편안한 티셔츠",
      inStock: true,
      stockCount: 20,
      colors: ["화이트", "블랙", "그레이", "네이비"],
      sizes: ["S", "M", "L", "XL"],
    },
    {
      id: "6",
      name: "우드 노트북",
      price: 15000,
      originalPrice: 18000,
      image: "/next.svg",
      category: "문구",
      rating: 4.8,
      reviews: 167,
      tags: ["친환경", "신상품"],
      description: "재활용 종이로 만든 친환경 노트북",
      inStock: true,
      stockCount: 30,
      sizes: ["A5", "A4"],
    },
  ];
};

const fetchProductById = async (id: string): Promise<Product> => {
  await new Promise((resolve) => setTimeout(resolve, 800));

  const products = await fetchProducts();
  const product = products.find((p) => p.id === id);

  if (!product) {
    throw new Error("Product not found");
  }

  return product;
};

const fetchProductsByCategory = async (
  category: string
): Promise<Product[]> => {
  await new Promise((resolve) => setTimeout(resolve, 1000));

  const products = await fetchProducts();
  return products.filter((p) => p.category === category);
};

const searchProducts = async (query: string): Promise<Product[]> => {
  await new Promise((resolve) => setTimeout(resolve, 800));

  const products = await fetchProducts();
  return products.filter(
    (p) =>
      p.name.toLowerCase().includes(query.toLowerCase()) ||
      p.description.toLowerCase().includes(query.toLowerCase()) ||
      p.tags.some((tag) => tag.toLowerCase().includes(query.toLowerCase()))
  );
};

// React Query hooks
export const useProducts = () => {
  return useQuery({
    queryKey: ["products"],
    queryFn: fetchProducts,
    staleTime: 5 * 60 * 1000, // 5분
  });
};

export const useProduct = (id: string) => {
  return useQuery({
    queryKey: ["product", id],
    queryFn: () => fetchProductById(id),
    enabled: !!id,
    staleTime: 10 * 60 * 1000, // 10분
  });
};

export const useProductsByCategory = (category: string) => {
  return useQuery({
    queryKey: ["products", "category", category],
    queryFn: () => fetchProductsByCategory(category),
    enabled: !!category,
    staleTime: 5 * 60 * 1000, // 5분
  });
};

export const useSearchProducts = (query: string) => {
  return useQuery({
    queryKey: ["products", "search", query],
    queryFn: () => searchProducts(query),
    enabled: !!query && query.length > 0,
    staleTime: 2 * 60 * 1000, // 2분
  });
};

// 상품 즐겨찾기 관련
export const useToggleFavorite = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      productId,
      isFavorite,
    }: {
      productId: string;
      isFavorite: boolean;
    }) => {
      await new Promise((resolve) => setTimeout(resolve, 500));

      // 실제 API 호출:
      // const response = await fetch(`/api/products/${productId}/favorite`, {
      //   method: isFavorite ? 'DELETE' : 'POST',
      //   headers: { 'Content-Type': 'application/json' }
      // });
      // if (!response.ok) throw new Error('Failed to toggle favorite');

      return { productId, isFavorite: !isFavorite };
    },
    onSuccess: () => {
      // 관련 쿼리들 무효화
      queryClient.invalidateQueries({ queryKey: ["products"] });
      queryClient.invalidateQueries({ queryKey: ["favorites"] });
    },
  });
};
