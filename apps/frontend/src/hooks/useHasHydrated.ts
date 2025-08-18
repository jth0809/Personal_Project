"use client";
import { useEffect, useState } from "react";
export function useHasHydrated() {
  const [m, setM] = useState(false);
  useEffect(() => setM(true), []);
  return m;
}
