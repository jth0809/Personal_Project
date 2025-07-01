import { Product } from "@/types";

export interface CartItem {
  id: string;
  productId: string;
  name: string;
  price: number;
  originalPrice: number;
  image: string;
  color: string;
  size: string;
  quantity: number;
  selected: boolean;
  inStock: boolean;
}

// 확장된 샘플 상품 데이터
export const sampleProducts: Product[] = [
  // 가방 카테고리
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
    id: "7",
    name: "레더 크로스백",
    price: 89000,
    originalPrice: 120000,
    image: "/next.svg",
    images: ["/next.svg", "/vercel.svg"],
    category: "가방",
    rating: 4.7,
    reviews: 89,
    tags: ["신상품", "프리미엄"],
    description: "고급 천연 가죽으로 제작된 세련된 크로스백입니다.",
    features: [
      "100% 천연 가죽",
      "조절 가능한 스트랩",
      "내부 지퍼 포켓",
      "25cm x 20cm x 8cm",
    ],
    inStock: true,
    stockCount: 15,
    colors: ["브라운", "블랙", "베이지"],
    sizes: ["원 사이즈"],
  },
  {
    id: "8",
    name: "캐주얼 백팩",
    price: 45000,
    originalPrice: 55000,
    image: "/next.svg",
    category: "가방",
    rating: 4.6,
    reviews: 234,
    tags: ["베스트셀러"],
    description: "일상 사용에 완벽한 심플한 디자인의 백팩입니다.",
    inStock: true,
    stockCount: 40,
    colors: ["블랙", "그레이", "네이비"],
    sizes: ["원 사이즈"],
  },

  // 생활용품 카테고리
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
    stockCount: 30,
    colors: ["화이트", "블랙", "핑크"],
    sizes: ["360ml"],
  },
  {
    id: "9",
    name: "스테인리스 텀블러",
    price: 32000,
    originalPrice: 38000,
    image: "/next.svg",
    category: "생활용품",
    rating: 4.9,
    reviews: 167,
    tags: ["베스트셀러", "친환경"],
    description: "보온보냉 기능이 뛰어난 스테인리스 텀블러입니다.",
    features: [
      "6시간 보온/보냉",
      "304 스테인리스 스틸",
      "누수 방지 뚜껑",
      "500ml 용량",
    ],
    inStock: true,
    stockCount: 50,
    colors: ["실버", "블랙", "화이트"],
    sizes: ["500ml"],
  },
  {
    id: "10",
    name: "아로마 디퓨저",
    price: 65000,
    originalPrice: 80000,
    image: "/next.svg",
    category: "생활용품",
    rating: 4.5,
    reviews: 123,
    tags: ["신상품"],
    description: "자연 목재로 제작된 프리미엄 아로마 디퓨저입니다.",
    inStock: true,
    stockCount: 20,
    colors: ["내추럴", "다크브라운"],
    sizes: ["원 사이즈"],
  },

  // 액세서리 카테고리
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
    stockCount: 100,
    colors: ["실버", "골드", "로즈골드"],
    sizes: ["원 사이즈"],
  },
  {
    id: "11",
    name: "미니멀 시계",
    price: 89000,
    originalPrice: 120000,
    image: "/next.svg",
    category: "액세서리",
    rating: 4.7,
    reviews: 156,
    tags: ["프리미엄"],
    description: "심플하고 세련된 디자인의 미니멀 시계입니다.",
    features: [
      "일본 무브먼트",
      "스테인리스 스틸 케이스",
      "가죽 스트랩",
      "생활방수",
    ],
    inStock: true,
    stockCount: 25,
    colors: ["블랙", "브라운", "화이트"],
    sizes: ["원 사이즈"],
  },
  {
    id: "12",
    name: "실버 목걸이",
    price: 45000,
    originalPrice: 60000,
    image: "/next.svg",
    category: "액세서리",
    rating: 4.8,
    reviews: 89,
    tags: ["인기상품"],
    description: "925 실버로 제작된 우아한 목걸이입니다.",
    inStock: true,
    stockCount: 30,
    colors: ["실버"],
    sizes: ["원 사이즈"],
  },

  // 인테리어 카테고리
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
    stockCount: 15,
    sizes: ["A3", "A2"],
  },
  {
    id: "13",
    name: "캔들 홀더",
    price: 28000,
    originalPrice: 35000,
    image: "/next.svg",
    category: "인테리어",
    rating: 4.6,
    reviews: 78,
    tags: ["신상품"],
    description: "따뜻한 분위기를 연출하는 유리 캔들 홀더입니다.",
    inStock: true,
    stockCount: 40,
    colors: ["클리어", "앰버", "스모키"],
    sizes: ["원 사이즈"],
  },
  {
    id: "14",
    name: "우드 액자",
    price: 22000,
    originalPrice: 28000,
    image: "/next.svg",
    category: "인테리어",
    rating: 4.5,
    reviews: 145,
    tags: ["베스트셀러"],
    description: "천연 목재로 제작된 심플한 디자인의 액자입니다.",
    inStock: true,
    stockCount: 60,
    colors: ["내추럴", "화이트", "블랙"],
    sizes: ["4x6", "5x7", "8x10"],
  },

  // 의류 카테고리
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
    stockCount: 80,
    colors: ["화이트", "블랙", "그레이", "네이비"],
    sizes: ["S", "M", "L", "XL"],
  },
  {
    id: "15",
    name: "후드 집업",
    price: 65000,
    originalPrice: 80000,
    image: "/next.svg",
    category: "의류",
    rating: 4.7,
    reviews: 234,
    tags: ["신상품"],
    description: "포근한 기모 안감의 프리미엄 후드 집업입니다.",
    features: ["기모 안감", "YKK 지퍼", "캥거루 포켓", "조절 가능한 후드"],
    inStock: true,
    stockCount: 45,
    colors: ["블랙", "그레이", "네이비", "베이지"],
    sizes: ["S", "M", "L", "XL"],
  },
  {
    id: "16",
    name: "데님 재킷",
    price: 89000,
    originalPrice: 110000,
    image: "/next.svg",
    category: "의류",
    rating: 4.6,
    reviews: 156,
    tags: ["클래식"],
    description: "빈티지 워싱 처리된 클래식 데님 재킷입니다.",
    inStock: true,
    stockCount: 35,
    colors: ["인디고", "라이트블루", "블랙"],
    sizes: ["S", "M", "L", "XL"],
  },

  // 문구 카테고리
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
    stockCount: 100,
    sizes: ["A5", "A4"],
  },
  {
    id: "17",
    name: "만년필 세트",
    price: 75000,
    originalPrice: 95000,
    image: "/next.svg",
    category: "문구",
    rating: 4.9,
    reviews: 89,
    tags: ["프리미엄", "선물용"],
    description: "고급 메탈 소재의 만년필과 잉크 세트입니다.",
    features: [
      "스테인리스 스틸 펜촉",
      "고급 선물 박스",
      "3가지 잉크 색상",
      "리필 카트리지 포함",
    ],
    inStock: true,
    stockCount: 20,
    colors: ["실버", "골드", "블랙"],
    sizes: ["원 사이즈"],
  },
  {
    id: "18",
    name: "컬러 마커 세트",
    price: 32000,
    originalPrice: 40000,
    image: "/next.svg",
    category: "문구",
    rating: 4.7,
    reviews: 234,
    tags: ["베스트셀러"],
    description: "선명한 발색의 프리미엄 컬러 마커 24색 세트입니다.",
    inStock: true,
    stockCount: 50,
    colors: ["멀티컬러"],
    sizes: ["24색 세트"],
  },
];

// 카테고리 목록
export const categories = [
  { id: "all", name: "전체", count: sampleProducts.length },
  {
    id: "가방",
    name: "가방",
    count: sampleProducts.filter((p) => p.category === "가방").length,
  },
  {
    id: "생활용품",
    name: "생활용품",
    count: sampleProducts.filter((p) => p.category === "생활용품").length,
  },
  {
    id: "액세서리",
    name: "액세서리",
    count: sampleProducts.filter((p) => p.category === "액세서리").length,
  },
  {
    id: "인테리어",
    name: "인테리어",
    count: sampleProducts.filter((p) => p.category === "인테리어").length,
  },
  {
    id: "의류",
    name: "의류",
    count: sampleProducts.filter((p) => p.category === "의류").length,
  },
  {
    id: "문구",
    name: "문구",
    count: sampleProducts.filter((p) => p.category === "문구").length,
  },
];

// 관련 상품 데이터
export const relatedProducts: Omit<Product, "features" | "colors" | "sizes">[] =
  [
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
    },
    {
      id: "3",
      name: "스타일 키링",
      price: 12000,
      originalPrice: 15000,
      image: "/vercel.svg",
      category: "액세서리",
      rating: 4.9,
      reviews: 203,
      tags: ["인기상품"],
      description: "고급 메탈 소재의 스타일리시한 키링",
    },
    {
      id: "4",
      name: "아트 포스터",
      price: 35000,
      originalPrice: 40000,
      image: "/globe.svg",
      category: "인테리어",
      rating: 4.7,
      reviews: 124,
      tags: ["한정판"],
      description: "독창적인 디자인의 아트 포스터",
    },
  ];

// 추천 상품 데이터
export const recommendedProducts: Omit<
  Product,
  "features" | "colors" | "sizes" | "tags" | "description"
>[] = [
  {
    id: "4",
    name: "아트 포스터",
    price: 35000,
    originalPrice: 40000,
    image: "/window.svg",
    category: "인테리어",
    rating: 4.7,
    reviews: 124,
  },
  {
    id: "5",
    name: "코튼 티셔츠",
    price: 28000,
    originalPrice: 35000,
    image: "/file.svg",
    category: "의류",
    rating: 4.5,
    reviews: 78,
  },
];

// 특별 상품 (Featured Products용)
export const featuredProducts: Product[] = [
  {
    ...sampleProducts[0], // 프리미엄 에코백
    tags: ["베스트셀러", "친환경", "특가"],
  },
  {
    ...sampleProducts[8], // 스테인리스 텀블러
    tags: ["베스트셀러", "친환경", "한정판"],
  },
  {
    ...sampleProducts[10], // 미니멀 시계
    tags: ["신상품", "프리미엄", "추천"],
  },
];

// 타입별 상품 개수
export const getProductCountByType = () => {
  let bestCount = 0;
  let newCount = 0;
  let saleCount = 0;

  sampleProducts.forEach((product) => {
    if (product.tags?.includes("베스트셀러")) bestCount++;
    if (product.tags?.includes("신상품")) newCount++;
    if (product.originalPrice && product.originalPrice > product.price)
      saleCount++;
  });

  return {
    all: sampleProducts.length,
    best: bestCount,
    new: newCount,
    sale: saleCount,
  };
};

// 상품 관련 유틸리티 함수들
export const getProductById = (id: string): Product | undefined => {
  return sampleProducts.find((product) => product.id === id);
};

export const getProductsByCategory = (category: string): Product[] => {
  if (category === "all") {
    return sampleProducts;
  }
  return sampleProducts.filter((product) => product.category === category);
};

export const calculateDiscount = (
  originalPrice: number,
  currentPrice: number
): number => {
  return Math.round(((originalPrice - currentPrice) / originalPrice) * 100);
};

export const searchProducts = (query: string): Product[] => {
  const lowercaseQuery = query.toLowerCase();
  return sampleProducts.filter(
    (product) =>
      product.name.toLowerCase().includes(lowercaseQuery) ||
      product.description.toLowerCase().includes(lowercaseQuery) ||
      product.category.toLowerCase().includes(lowercaseQuery) ||
      product.tags.some((tag) => tag.toLowerCase().includes(lowercaseQuery))
  );
};
