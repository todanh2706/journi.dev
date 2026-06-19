import { useLocation } from "react-router-dom";
import {
  LayoutDashboard,
  GitBranch,
  BookOpen,
  Code,
  Trophy,
  Map,
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
        <div className="flex items-center gap-3 px-2">
          <img
            src={user ? `https://i.pravatar.cc/150?u=${user.username}` : "https://i.pravatar.cc/150?u=guest"}
            alt={user ? user.username : "Guest"}
            className="w-9 h-9 rounded-full border border-white/10"
          />
          <div>
            <div className="text-sm font-medium text-gray-200">
              {user ? user.username : "Guest"}
            </div>
            <div className="text-xs text-gray-500">
              {user ? `@${user.username}` : "Not logged in"}
            </div>
          </div>
        </div>
      </div>
    </aside>
  );
}
