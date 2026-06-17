import { type ReactNode } from "react";
import { Link, useLocation } from "react-router-dom";

export function NavItem({ icon, label, to }: { icon: ReactNode; label: string; to: string }) {
  const location = useLocation();
  // Exact match for dashboard root, otherwise check prefix
  const active = to === "/dashboard" 
    ? location.pathname === "/dashboard" 
    : location.pathname.startsWith(to);

  return (
    <Link
      to={to}
      className={`flex items-center gap-3.5 px-4 py-3 rounded-[14px] text-[15px] font-medium transition-colors ${
        active 
          ? "bg-indigo-500/10 text-indigo-300" 
          : "text-gray-400 hover:text-gray-200 hover:bg-white/[0.03]"
      }`}
    >
      <span className={active ? "text-indigo-400" : "text-gray-500"}>{icon}</span>
      {label}
    </Link>
  );
}