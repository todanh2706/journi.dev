import { Link } from "react-router-dom";
import {
  LayoutDashboard,
  GitBranch,
  BookOpen,
  Code,
  Trophy,
  Map,
  Flame,
  Play,
  Grid,
  Zap,
  Database,
  TerminalSquare,
  MessageSquare,
  Users,
  Atom,
  Box
} from "lucide-react";

export default function Dashboard() {
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
          <NavItem icon={<LayoutDashboard size={18} />} label="Dashboard" active />
          <NavItem icon={<GitBranch size={18} />} label="Skill Tree" />
          <NavItem icon={<BookOpen size={18} />} label="Learning Space" />
          <NavItem icon={<Code size={18} />} label="Code Review" />
          <NavItem icon={<Trophy size={18} />} label="Leaderboard" />
          <NavItem icon={<Map size={18} />} label="Roadmap" />
        </nav>

        <div className="p-6 pb-8">
          <div className="flex items-center gap-3 px-2">
            <img src="https://i.pravatar.cc/150?u=a042581f4e29026704d" alt="Alex Dev" className="w-9 h-9 rounded-full border border-white/10" />
            <div>
              <div className="text-sm font-medium text-gray-200">Alex Dev</div>
              <div className="text-xs text-gray-500">@alexcode</div>
            </div>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col h-screen overflow-y-auto bg-[#10111e] rounded-tl-[40px] border-l border-t border-white/[0.04]">
        {/* Header */}
        <header className="px-12 pt-12 pb-8 flex items-end justify-between">
          <div>
            <h1 className="text-[28px] font-bold mb-2">Welcome back, Alex</h1>
            <p className="text-gray-400 text-[15px]">Your learning journey continues. Let's hit that daily goal.</p>
          </div>
          <div className="flex items-center gap-6">
            <div className="flex items-center gap-2 text-sm font-medium text-gray-400 bg-white/[0.03] px-4 py-2.5 rounded-xl border border-white/[0.05]">
              <Flame size={18} className="text-indigo-400" />
              14 Day Streak
            </div>
            <button className="bg-indigo-500 hover:bg-indigo-400 transition-colors text-white px-6 py-2.5 rounded-xl text-sm font-semibold flex items-center gap-2 shadow-lg shadow-indigo-500/25">
              <Play size={16} fill="currentColor" />
              Resume Learning
            </button>
          </div>
        </header>

        <div className="px-12 pb-12 flex gap-8">
          {/* Left Column */}
          <div className="flex-1 space-y-8 min-w-0">
            {/* Heatmap Section */}
            <section>
              <div className="flex items-center justify-between mb-5">
                <div className="flex items-center gap-2 text-[15px] font-semibold text-gray-200">
                  <Grid size={18} className="text-indigo-400" />
                  Contribution Heatmap
                </div>
                <div className="flex bg-[#161729] rounded-lg p-1 text-[13px] font-medium border border-white/[0.03]">
                  <button className="px-4 py-1.5 rounded-md bg-indigo-500/20 text-indigo-300">30 Days</button>
                  <button className="px-4 py-1.5 rounded-md text-gray-500 hover:text-gray-300 transition-colors">6 Months</button>
                  <button className="px-4 py-1.5 rounded-md text-gray-500 hover:text-gray-300 transition-colors">1 Year</button>
                </div>
              </div>
              <div className="bg-[#141527] border border-white/[0.06] rounded-2xl p-7 relative overflow-hidden">
                <div className="absolute top-0 right-0 w-64 h-64 bg-indigo-500/5 blur-[100px] rounded-full pointer-events-none" />
                <div className="flex gap-2.5 mb-8 overflow-x-auto pb-2">
                  {/* Generate dummy heatmap columns */}
                  {Array.from({ length: 24 }).map((_, colIndex) => (
                    <div key={colIndex} className="flex flex-col gap-2.5">
                      {Array.from({ length: 5 }).map((_, rowIndex) => {
                        const intensity = Math.random();
                        let bgClass = "bg-[#1c1d33]"; // empty
                        if (intensity > 0.95) bgClass = "bg-indigo-400"; // high
                        else if (intensity > 0.8) bgClass = "bg-indigo-500"; // medium-high
                        else if (intensity > 0.5) bgClass = "bg-indigo-600/60"; // medium
                        else if (intensity > 0.3) bgClass = "bg-indigo-900/40"; // low

                        return <div key={rowIndex} className={`w-[14px] h-[14px] rounded-[3px] ${bgClass}`} />;
                      })}
                    </div>
                  ))}
                </div>
                <div className="flex items-center justify-between border-t border-white/[0.06] pt-5 mt-2">
                  <div className="flex gap-10">
                    <div>
                      <div className="text-2xl font-bold">245</div>
                      <div className="text-[13px] text-gray-500 mt-1">Total Commits</div>
                    </div>
                    <div>
                      <div className="text-2xl font-bold text-indigo-400">22</div>
                      <div className="text-[13px] text-gray-500 mt-1">Best Day</div>
                    </div>
                  </div>
                  <div className="flex items-center gap-3 text-[13px] text-gray-500">
                    Less
                    <div className="flex gap-1.5">
                      <div className="w-[14px] h-[14px] rounded-[3px] bg-[#1c1d33]" />
                      <div className="w-[14px] h-[14px] rounded-[3px] bg-indigo-900/40" />
                      <div className="w-[14px] h-[14px] rounded-[3px] bg-indigo-600/60" />
                      <div className="w-[14px] h-[14px] rounded-[3px] bg-indigo-500" />
                      <div className="w-[14px] h-[14px] rounded-[3px] bg-indigo-400" />
                    </div>
                    More
                  </div>
                </div>
              </div>
            </section>

            {/* Next Milestones */}
            <section>
              <div className="flex items-center justify-between mb-5">
                <div className="flex items-center gap-2 text-[15px] font-semibold text-gray-200">
                  <Map size={18} className="text-indigo-400" />
                  Next Milestones
                </div>
                <button className="text-indigo-400 text-[13px] font-medium hover:text-indigo-300">
                  View Full Roadmap
                </button>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
                <MilestoneCard
                  title="React Hooks Mastery"
                  id="MS-042"
                  progress={75}
                  status="In Progress"
                  icon={<Atom size={20} className="text-indigo-400" />}
                />
                <MilestoneCard
                  title="Node.js API Design"
                  id="MS-043"
                  progress={0}
                  status="Up Next"
                  icon={<Box size={20} className="text-gray-400" />}
                />
                <MilestoneCard
                  title="Postgres Optimization"
                  id="MS-044"
                  progress={0}
                  status="Locked"
                  icon={<Database size={20} className="text-gray-500" />}
                  locked
                />
              </div>
            </section>

            {/* Quick Actions */}
            <section>
              <div className="flex items-center gap-2 text-[15px] font-semibold text-gray-200 mb-5">
                <Zap size={18} className="text-indigo-400" />
                Quick Actions
              </div>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
                <ActionCard
                  title="Start Challenge"
                  subtitle="Daily algorithm"
                  icon={<TerminalSquare size={20} className="text-indigo-400" />}
                />
                <ActionCard
                  title="Review Feedback"
                  subtitle="3 pending reviews"
                  icon={<MessageSquare size={20} className="text-purple-400" />}
                />
                <ActionCard
                  title="Find Team"
                  subtitle="For hackathon"
                  icon={<Users size={20} className="text-emerald-400" />}
                />
              </div>
            </section>
          </div>

          {/* Right Column */}
          <div className="w-[360px] shrink-0">
            <div className="bg-[#141527] border border-white/[0.06] rounded-2xl p-7 h-full flex flex-col relative overflow-hidden">
              <div className="absolute top-0 right-0 w-64 h-64 bg-indigo-500/5 blur-[100px] rounded-full pointer-events-none" />
              
              <div className="flex items-center justify-between mb-8 relative z-10">
                <div className="flex items-center gap-2 text-[15px] font-semibold text-gray-200">
                  <Trophy size={18} className="text-indigo-400" />
                  Leaderboard
                </div>
                <div className="flex bg-[#161729] rounded-lg p-1 text-[13px] font-medium border border-white/[0.03]">
                  <button className="px-4 py-1.5 rounded-md bg-indigo-500/20 text-indigo-300">Friends</button>
                  <button className="px-4 py-1.5 rounded-md text-gray-500 hover:text-gray-300 transition-colors">Team</button>
                </div>
              </div>

              {/* User Rank Card */}
              <div className="bg-[#1a1b30] border border-white/[0.04] rounded-2xl p-6 flex flex-col items-center mb-8 relative overflow-hidden z-10">
                <div className="absolute inset-0 bg-gradient-to-b from-indigo-500/10 to-transparent pointer-events-none" />
                
                <div className="relative mb-4">
                  <img src="https://i.pravatar.cc/150?u=a042581f4e29026704d" alt="Alex Dev" className="w-[72px] h-[72px] rounded-full border-[3px] border-[#141527] ring-2 ring-indigo-500/50" />
                  <div className="absolute -bottom-2.5 left-1/2 -translate-x-1/2 bg-indigo-500 text-white text-[11px] font-bold px-2.5 py-0.5 rounded-full border-[3px] border-[#1a1b30]">
                    12
                  </div>
                </div>
                <div className="font-semibold text-lg relative z-10 text-gray-100">Alex Dev</div>
                <div className="text-[13px] text-gray-400 mt-1.5 relative z-10 flex items-center gap-2">
                  Rank 12 <span className="w-1 h-1 rounded-full bg-gray-600" /> <span className="text-indigo-300 font-medium">2,450 pts</span>
                </div>
              </div>

              {/* Leaderboard List */}
              <div className="flex-1 space-y-5 mb-8 relative z-10">
                <LeaderboardItem rank={1} name="Sarah_Codes" handle="@sarahc" pts="3,200" avatar="https://i.pravatar.cc/150?u=1" />
                <LeaderboardItem rank={2} name="MikeBuilder" handle="@mikeb" pts="3,050" avatar="https://i.pravatar.cc/150?u=2" />
                <LeaderboardItem rank={3} name="ElenaTech" handle="@elena_t" pts="2,980" avatar="https://i.pravatar.cc/150?u=3" />
                
                <div className="h-px bg-white/[0.04] my-2" />
                
                <LeaderboardItem rank={12} name="Alex Dev" handle="You" pts="2,450" avatar="https://i.pravatar.cc/150?u=a042581f4e29026704d" isUser />
              </div>

              <button className="w-full py-3.5 rounded-xl border border-white/[0.06] text-[13px] font-medium text-gray-400 hover:text-white hover:bg-white/[0.03] transition-colors relative z-10">
                View Full Standings
              </button>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

function NavItem({ icon, label, active }: { icon: React.ReactNode; label: string; active?: boolean }) {
  return (
    <a
      href="#"
      className={`flex items-center gap-3.5 px-4 py-3 rounded-[14px] text-[15px] font-medium transition-colors ${
        active 
          ? "bg-indigo-500/10 text-indigo-300" 
          : "text-gray-400 hover:text-gray-200 hover:bg-white/[0.03]"
      }`}
    >
      <span className={active ? "text-indigo-400" : "text-gray-500"}>{icon}</span>
      {label}
    </a>
  );
}

function MilestoneCard({ title, id, progress, status, icon, locked }: any) {
  return (
    <div className={`bg-[#141527] border border-white/[0.06] rounded-2xl p-6 flex flex-col ${locked ? 'opacity-50 grayscale-[0.5]' : ''}`}>
      <div className="flex items-start justify-between mb-8">
        <div className={`w-11 h-11 rounded-xl flex items-center justify-center border border-white/[0.04] ${locked ? 'bg-white/[0.02]' : 'bg-[#1c1d33] shadow-inner shadow-white/[0.02]'}`}>
          {icon}
        </div>
        <div className={`text-[11px] font-semibold px-2.5 py-1 rounded-md ${
          status === 'In Progress' ? 'bg-indigo-500/15 text-indigo-300 border border-indigo-500/20' :
          status === 'Locked' ? 'bg-white/[0.03] text-gray-500 border border-white/[0.05]' :
          'bg-white/[0.06] text-gray-300 border border-white/[0.05]'
        }`}>
          {status}
        </div>
      </div>
      <div className="font-semibold text-[15px] mb-1.5 text-gray-100">{title}</div>
      <div className="text-[13px] text-gray-500 mb-6 font-mono">ID: {id}</div>
      
      <div className="mt-auto">
        <div className="flex justify-between text-[13px] mb-2.5">
          <span className="text-gray-500">Progress</span>
          <span className={locked ? "text-gray-500" : "text-indigo-400 font-medium"}>{progress}%</span>
        </div>
        <div className="h-2 w-full bg-[#1c1d33] rounded-full overflow-hidden border border-white/[0.02]">
          <div 
            className="h-full bg-indigo-500 rounded-full"
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>
    </div>
  );
}

function ActionCard({ title, subtitle, icon }: any) {
  return (
    <button className="bg-[#141527] border border-white/[0.06] hover:border-white/[0.12] hover:bg-[#18192e] rounded-2xl p-5 flex items-center gap-4 text-left transition-all active:scale-[0.98]">
      <div className="w-12 h-12 rounded-xl bg-[#1c1d33] flex items-center justify-center shrink-0 border border-white/[0.03] shadow-inner shadow-white/[0.02]">
        {icon}
      </div>
      <div>
        <div className="font-semibold text-[15px] text-gray-100 mb-1">{title}</div>
        <div className="text-[13px] text-gray-500">{subtitle}</div>
      </div>
    </button>
  );
}

function LeaderboardItem({ rank, name, handle, pts, avatar, isUser }: any) {
  return (
    <div className={`flex items-center gap-3.5 p-2.5 rounded-xl ${isUser ? 'bg-[#1a1b30] border border-indigo-500/20 shadow-lg shadow-indigo-500/5' : ''}`}>
      <div className={`w-6 text-center text-[15px] font-bold ${
        rank === 1 ? 'text-amber-400 drop-shadow-[0_0_8px_rgba(251,191,36,0.3)]' :
        rank === 2 ? 'text-gray-300 drop-shadow-[0_0_8px_rgba(209,213,219,0.3)]' :
        rank === 3 ? 'text-amber-600' :
        isUser ? 'text-indigo-400' :
        'text-gray-500'
      }`}>
        {rank}
      </div>
      <img src={avatar} alt={name} className="w-9 h-9 rounded-full border border-white/10" />
      <div className="flex-1 min-w-0">
        <div className="text-[14px] font-medium text-gray-200 truncate">{name}</div>
        <div className="text-[12px] text-gray-500 truncate">{handle}</div>
      </div>
      <div className={`text-[14px] font-semibold tracking-tight ${isUser ? 'text-indigo-400' : 'text-indigo-300'}`}>
        {pts}
      </div>
    </div>
  );
}
