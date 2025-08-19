import { ProductResponse } from "@/types/backend";

// src/components/seo/ProductJsonLd.tsx
export function ProductJsonLd({ product }: { product: ProductResponse }) {
  const images = product.imageUrl?.filter(
    (s: string) => typeof s === "string" && s.trim()
  );

  const jsonLd = {
    "@context": "https://schema.org",
    "@type": "Product",
    name: product.name,
    description: product.detailContent || product.description || "",
    image: images, // 배열 또는 undefined
    offers: {
      "@type": "Offer",
      priceCurrency: "KRW",
      price: product.price,
      availability:
        (product.stockQuantity ?? 0) > 0
          ? "https://schema.org/InStock"
          : "https://schema.org/OutOfStock",
    },
  } as const;

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(jsonLd) }}
    />
  );
}
