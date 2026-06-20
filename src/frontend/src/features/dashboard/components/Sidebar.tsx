import { Link, useLocation } from "react-router-dom";
import {
  LayoutDashboard,
  GitBranch,
  BookOpen,
  Code,
  Trophy,
  Map,
  ChevronRight,
} from "lucide-react";
import { Logo } from "../../../components/Logo/Logo";
import { useAuth } from "../../auth";
import { NavItem } from "./NavItem";

const navItems = [
  { path: "/dashboard", icon: <LayoutDashboard size={18} />, label: "Dashboard" },
  { path: "/dashboard/skill-tree", icon: <GitBranch size={18} />, label: "Skill Tree" },
  { path: "/dashboard/learning-space", icon: <BookOpen size={18} />, label: "Learning Space" },
  { path: "/dashboard/code-review", icon: <Code size={18} />, label: "Code Review" },
  { path: "/dashboard/leaderboard", icon: <Trophy size={18} />, label: "Leaderboard" },
  { path: "/dashboard/roadmaps", icon: <Map size={18} />, label: "Roadmap" },
];

export function Sidebar() {
  const { user } = useAuth();
  const location = useLocation();
  const profileActive = location.pathname === "/dashboard/profile";

  const activeIndex = navItems.findIndex((item) =>
    item.path === "/dashboard"
      ? location.pathname === "/dashboard"
      : location.pathname.startsWith(item.path)
  );

  return (
    <aside className="w-[260px] flex flex-col bg-[#0d0e1a]">
      <div className="p-6 pb-2">
        <Logo />
      </div>

      <nav className="flex-1 px-4 mt-2 relative">
        {/* Sliding Indicator */}
        {activeIndex !== -1 && (
          <div
            className="absolute left-4 right-4 h-12 bg-indigo-500/10 rounded-[14px] transition-transform duration-300 ease-out"
            style={{ transform: `translateY(${activeIndex * 54}px)` }}
          />
        )}

        <div className="flex flex-col gap-1.5 relative z-10">
          {navItems.map((item) => (
            <NavItem
              key={item.path}
              to={item.path}
              icon={item.icon}
              label={item.label}
            />
          ))}
        </div>
      </nav>

      <div className="p-6 pb-8">
        {user ? (
          <Link
            to="/dashboard/profile"
            aria-label={`Open profile for ${user.username}`}
            className={`group flex items-center gap-3 rounded-xl border px-3 py-2.5 transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-400/50 ${
              profileActive
                ? "border-indigo-400/20 bg-indigo-400/[0.09]"
                : "border-transparent hover:border-white/[0.06] hover:bg-white/[0.035]"
            }`}
          >
            <img
              src={`https://i.pravatar.cc/150?u=${user.username}`}
              alt={`${user.username}'s avatar`}
              className="h-9 w-9 rounded-xl border border-white/10 object-cover"
            />
            <div className="min-w-0 flex-1">
              <div className="truncate text-sm font-medium text-gray-200">{user.username}</div>
              <div className="truncate text-xs text-gray-500">@{user.username}</div>
            </div>
            <ChevronRight
              aria-hidden="true"
              size={16}
              strokeWidth={1.8}
              className={`transition-colors ${profileActive ? "text-indigo-300" : "text-gray-600 group-hover:text-gray-400"}`}
            />
          </Link>
        ) : (
          <div className="flex items-center gap-3 px-2 py-2.5">
            <img
              src="https://i.pravatar.cc/150?u=guest"
              alt="Guest avatar"
              className="h-9 w-9 rounded-xl border border-white/10 object-cover"
            />
            <div>
              <div className="text-sm font-medium text-gray-200">Guest</div>
              <div className="text-xs text-gray-500">Not logged in</div>
            </div>
          </div>
        )}
      </div>
    </aside>
  );
}
