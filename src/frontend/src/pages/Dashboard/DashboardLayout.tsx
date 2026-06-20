import { useEffect, useState } from "react";
import { Menu } from "lucide-react";
import { Outlet, useLocation } from "react-router-dom";

import { Logo } from "../../components/Logo/Logo";
import { Sidebar } from "../../features/dashboard";
import { SlideUpTransition } from "../../utils/transitions/SlideUpTransition";

export default function DashboardLayout() {
  const location = useLocation();
  const [navigationOpen, setNavigationOpen] = useState(false);

  useEffect(() => {
    if (!navigationOpen) return;

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") setNavigationOpen(false);
    };

    document.addEventListener("keydown", handleEscape);
    return () => document.removeEventListener("keydown", handleEscape);
  }, [navigationOpen]);

  return (
    <div className="flex min-h-screen bg-shell text-ink">
      <Sidebar />

      {navigationOpen ? (
        <div className="fixed inset-0 z-50 lg:hidden">
          <button
            type="button"
            aria-label="Close navigation"
            className="absolute inset-0 bg-black/70"
            onClick={() => setNavigationOpen(false)}
          />
          <div className="relative h-full w-[min(264px,86vw)] shadow-2xl shadow-black/50">
            <Sidebar mobile onClose={() => setNavigationOpen(false)} />
          </div>
        </div>
      ) : null}

      <div className="min-w-0 flex-1 bg-canvas">
        <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-line bg-canvas/95 px-4 backdrop-blur-md sm:px-6 lg:hidden">
          <Logo />
          <button
            type="button"
            className="icon-button"
            aria-label="Open navigation"
            aria-expanded={navigationOpen}
            onClick={() => setNavigationOpen(true)}
          >
            <Menu aria-hidden="true" size={20} />
          </button>
        </header>

        <main className="min-h-[calc(100vh-4rem)] min-w-0 overflow-x-hidden lg:min-h-screen">
          <SlideUpTransition key={location.pathname} bgClass="before:bg-canvas">
            <Outlet />
          </SlideUpTransition>
        </main>
      </div>
    </div>
  );
}
