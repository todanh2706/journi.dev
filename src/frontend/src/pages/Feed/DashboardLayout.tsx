import { Link, Outlet } from "react-router-dom";
import {
  LayoutDashboard,
  GitBranch,
  BookOpen,
  Code,
  Trophy,
  Map,
} from "lucide-react";

import { NavItem } from "./components/NavItem";
import { useAuth } from "../../hooks/useAuth";

export default function DashboardLayout() {
  const { user } = useAuth();
  
  return (
    <div className="flex min-h-screen bg-[#0d0e1a] text-white font-sans selection:bg-indigo-500/30">
      {/* Sidebar */}
      <aside className="w-[260px] flex flex-col bg-[#0d0e1a]">
        <div className="p-6 pb-2">
          <Link to="/" className="flex items-center gap-2.5 no-underline">
            <div className="w-9 h-9 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-lg flex items-center justify-center text-white font-bold text-sm select-none shadow-lg shadow-purple-500/20">
              {">_"}
            </div>
            <span className="text-lg tracking-tight">
              <span className="font-semibold text-white">Journi</span>
              <span className="text-purple-400 font-semibold">.dev</span>
            </span>
          </Link>
        </div>

        <nav className="flex-1 px-4 space-y-1.5 mt-2">
          <NavItem to="/dashboard" icon={<LayoutDashboard size={18} />} label="Dashboard" />
          <NavItem to="/dashboard/skill-tree" icon={<GitBranch size={18} />} label="Skill Tree" />
          <NavItem to="/dashboard/learning-space" icon={<BookOpen size={18} />} label="Learning Space" />
          <NavItem to="/dashboard/code-review" icon={<Code size={18} />} label="Code Review" />
          <NavItem to="/dashboard/leaderboard" icon={<Trophy size={18} />} label="Leaderboard" />
          <NavItem to="/dashboard/roadmaps" icon={<Map size={18} />} label="Roadmap" />
        </nav>

        <div className="p-6 pb-8">
          <div className="flex items-center gap-3 px-2">
            <img src={user ? `https://i.pravatar.cc/150?u=${user.username}` : "https://i.pravatar.cc/150?u=guest"} alt={user ? user.username : "Guest"} className="w-9 h-9 rounded-full border border-white/10" />
            <div>
              <div className="text-sm font-medium text-gray-200">{user ? user.username : "Guest"}</div>
              <div className="text-xs text-gray-500">{user ? `@${user.username}` : "Not logged in"}</div>
            </div>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col h-screen overflow-y-auto bg-[#10111e] rounded-tl-[40px] border-l border-t border-white/[0.04]">
        <Outlet />
      </main>
    </div>
  );
}
