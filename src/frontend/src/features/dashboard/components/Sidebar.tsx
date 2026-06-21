import { ChevronRight, LayoutDashboard, Map, X } from "lucide-react";
import { Link, useLocation } from "react-router-dom";

import { Logo } from "../../../components/Logo/Logo";
import { UserAvatar } from "../../../components/UserAvatar/UserAvatar";
import { useAuth } from "../../auth";
import { NavItem } from "./NavItem";

const navItems = [
  { path: "/dashboard", icon: <LayoutDashboard size={18} />, label: "Overview" },
  { path: "/dashboard/roadmaps", icon: <Map size={18} />, label: "Roadmaps" },
];

interface SidebarProps {
  mobile?: boolean;
  onClose?: () => void;
}

export function Sidebar({ mobile = false, onClose }: SidebarProps) {
  const { user, isLoading } = useAuth();
  const location = useLocation();
  const profileActive = location.pathname === "/dashboard/profile";

  return (
    <aside
      aria-label="Dashboard navigation"
      className={`${mobile ? "flex" : "hidden lg:flex"} h-full w-[264px] shrink-0 flex-col border-r border-line bg-shell`}
    >
      <div className="flex h-20 items-center justify-between px-5">
        <Logo />
        {mobile ? (
          <button type="button" onClick={onClose} className="icon-button" aria-label="Close navigation">
            <X aria-hidden="true" size={19} />
          </button>
        ) : null}
      </div>

      <nav className="flex-1 px-3" aria-label="Primary">
        <p className="mb-2 px-3 text-[11px] font-semibold uppercase tracking-[0.14em] text-subtle">
          Learning workspace
        </p>
        <div className="space-y-1.5">
          {navItems.map((item) => (
            <NavItem
              key={item.path}
              to={item.path}
              icon={item.icon}
              label={item.label}
              onNavigate={onClose}
            />
          ))}
        </div>
      </nav>

      <div className="border-t border-line p-4">
        {isLoading ? (
          <div className="rounded-xl border border-line bg-surface px-3 py-3 text-sm text-muted">
            Restoring session…
          </div>
        ) : user ? (
          <Link
            to="/dashboard/profile"
            onClick={onClose}
            aria-label={`Open profile for ${user.username}`}
            aria-current={profileActive ? "page" : undefined}
            className={`group flex items-center gap-3 rounded-xl border px-3 py-2.5 transition-colors ${
              profileActive
                ? "border-line-strong bg-surface-elevated"
                : "border-transparent hover:border-line hover:bg-surface"
            }`}
          >
            <UserAvatar username={user.username} className="h-10 w-10 text-sm" />
            <div className="min-w-0 flex-1">
              <div className="truncate text-sm font-medium text-ink">{user.username}</div>
              <div className="truncate text-xs text-subtle">Account settings</div>
            </div>
            <ChevronRight
              aria-hidden="true"
              size={16}
              className={profileActive ? "text-gold" : "text-subtle group-hover:text-muted"}
            />
          </Link>
        ) : (
          <div className="rounded-xl border border-line bg-surface px-3 py-3 text-sm text-muted">
            Sign in to access your learning workspace.
          </div>
        )}
      </div>
    </aside>
  );
}
