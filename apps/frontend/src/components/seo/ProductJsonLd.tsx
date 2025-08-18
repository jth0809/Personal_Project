// src/components/seo/ProductJsonLd.tsx
export function ProductJsonLd({ product }: { product: any }) {
  const images = Array.isArray(product.imageUrl)
    ? product.imageUrl.filter((s: string) => typeof s === "string" && s.trim())
    : typeof product.imageUrl === "string" && product.imageUrl.trim()
      ? [product.imageUrl]
      : undefined;

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
