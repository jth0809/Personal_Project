export default function ProductCardSkeleton() {
  return (
    <div className="animate-pulse rounded-2xl border bg-white p-4">
      <div className="mb-3 aspect-[4/3] w-full rounded-xl bg-gray-200" />
      <div className="h-4 w-2/3 rounded bg-gray-200" />
      <div className="mt-2 h-4 w-1/3 rounded bg-gray-200" />
    </div>
  );
}
