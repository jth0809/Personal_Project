export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="mx-auto max-w-md rounded-2xl border bg-white p-6 shadow-sm">
      {children}
    </div>
  );
}
