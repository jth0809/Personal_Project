export default function SkipLink() {
  return (
    <a
      href="#main"
      className="sr-only focus:not-sr-only fixed left-2 top-2 rounded bg-black px-3 py-2 text-sm text-white"
    >
      본문으로 건너뛰기
    </a>
  );
}
