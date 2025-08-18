"use client";

import { ReactNode } from "react";
import Header from "./Header";
import Footer from "./Footer";
import { GlobalErrorBoundary } from "./GlobalErrorBoundary";
import SkipLink from "./SkipLink";

interface MainLayoutProps {
  children: ReactNode;
}

export default function MainLayout({ children }: MainLayoutProps) {
  return (
    <div>
      <GlobalErrorBoundary>
        <SkipLink />
        <div className="min-h-screen flex flex-col">
          <Header />
          <main
            id="main"
            className="container mx-auto w-full max-w-7xl flex-1 px-4 py-6"
          >
            {children}
          </main>
          <Footer />
        </div>
      </GlobalErrorBoundary>
    </div>
  );
}
