import { type ReactNode } from "react";
import { Link, useLocation } from "react-router-dom";

interface NavItemProps {
  icon: ReactNode;
  label: string;
  to: string;
  onNavigate?: () => void;
}

export function NavItem({ icon, label, to, onNavigate }: NavItemProps) {
  const location = useLocation();
  const active = to === "/dashboard"
    ? location.pathname === "/dashboard"
    : location.pathname.startsWith(to);

  return (
    <Link
      to={to}
      onClick={onNavigate}
      aria-current={active ? "page" : undefined}
      className={`flex h-11 items-center gap-3 rounded-xl border px-3.5 text-sm font-medium transition-colors ${
        active
          ? "border-line-strong bg-surface-elevated text-ink"
          : "border-transparent text-muted hover:border-line hover:bg-surface hover:text-ink"
      }`}
    >
      <span className={active ? "text-gold" : "text-subtle"}>{icon}</span>
      {label}
    </Link>
  );
}
