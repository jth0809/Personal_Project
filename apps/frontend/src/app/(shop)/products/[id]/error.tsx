"use client";
export default function Error({
  error,
  reset,
}: {
  error: Error;
  reset: () => void;
}) {
  console.error(error);
  return (
    <div className="p-10 text-center">
      <p className="text-gray-600">문제가 발생했습니다.</p>
      <button
        onClick={reset}
        className="mt-3 rounded-lg border px-4 py-2 text-sm"
      >
        다시 시도
      </button>
    </div>
  );
}
