"use client";
import React from "react";

export class GlobalErrorBoundary extends React.Component<
  { children: React.ReactNode },
  { hasError: boolean }
> {
  state = { hasError: false } as { hasError: boolean };
  static getDerivedStateFromError() {
    return { hasError: true };
  }
  componentDidCatch(err: unknown) {
    // TODO: Sentry.captureException(err)
    console.error("Global error", err);
  }
  render() {
    if (this.state.hasError)
      return <div className="p-6">예기치 못한 오류가 발생했습니다.</div>;
    return this.props.children as React.ReactElement;
  }
}
