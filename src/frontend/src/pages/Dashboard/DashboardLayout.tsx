import { Outlet, useLocation } from "react-router-dom";
import { Sidebar } from "../../features/dashboard";
import { SlideUpTransition } from "../../utils/transitions/SlideUpTransition";

export default function DashboardLayout() {
  const location = useLocation();
  
  return (
    <div className="flex min-h-screen bg-[#0d0e1a] text-white font-sans selection:bg-indigo-500/30">
      <Sidebar />

      {/* Main Content */}
      <main className="flex-1 flex flex-col h-screen overflow-y-auto bg-[#10111e] rounded-tl-[40px] border-l border-t border-white/[0.04]">
        <SlideUpTransition key={location.pathname} bgClass="before:bg-[#10111e]">
          <Outlet />
        </SlideUpTransition>
      </main>
    </div>
  );
}
