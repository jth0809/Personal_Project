import RequireAuth from "@/components/Account/RequireAuth";
export default function AccountLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <RequireAuth>{children}</RequireAuth>;
}
