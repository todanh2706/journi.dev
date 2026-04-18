import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

/* ───────────────────── tiny inline SVG icons ───────────────────── */

const SkillTreeIcon = () => (
    <svg
        width="20"
        height="20"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
    >
        <rect x="3" y="3" width="18" height="18" rx="4" />
        <path d="M8 12h8M12 8v8" />
    </svg>
);

const CodeReviewIcon = () => (
    <svg
        width="18"
        height="18"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
    >
        <circle cx="11" cy="11" r="8" />
        <path d="m21 21-4.35-4.35" />
    </svg>
);

const StreakIcon = () => (
    <svg
        width="18"
        height="18"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
    >
        <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" />
    </svg>
);

/* ──────────────────────── sub‑components ──────────────────────── */

function Logo() {
    return (
        <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-lg flex items-center justify-center text-white font-bold text-sm select-none shadow-lg shadow-purple-500/20">
                {">_"}
            </div>
            <span className="text-lg tracking-tight">
                <span className="font-semibold text-white">Journi</span>
                <span className="text-purple-400 font-semibold">.dev</span>
            </span>
        </div>
    );
}

function SkillTreeCard() {
    const [xp, setXp] = useState(650);

    useEffect(() => {
        const interval = setInterval(() => {
            setXp((prev) => {
                const next = prev + Math.floor(Math.random() * 15) + 1;
                return next >= 1000 ? 650 : next;
            });
        }, 2000);
        return () => clearInterval(interval);
    }, []);

    return (
        <div className="bg-[#1a1b2e]/90 backdrop-blur-xl border border-white/[0.06] rounded-2xl p-5 w-[290px] shadow-2xl shadow-black/40 animate-float-slow">
            <div className="flex items-center gap-2.5 mb-3">
                <div className="w-8 h-8 rounded-lg bg-purple-600/30 flex items-center justify-center text-purple-400">
                    <SkillTreeIcon />
                </div>
                <span className="font-semibold text-white text-[15px]">
                    Skill Tree
                </span>
            </div>
            <p className="text-gray-400 text-[13px] leading-relaxed mb-4">
                Unlock new paradigms and frameworks as you progress.
            </p>
            <div className="space-y-1.5">
                <div className="flex justify-between text-[12px]">
                    <span className="text-gray-500">Lvl. 12</span>
                    <span className="text-gray-500">
                        {xp}/1000{" "}
                        <span className="text-purple-400 font-medium">XP</span>
                    </span>
                </div>
                <div className="h-1.5 bg-white/[0.06] rounded-full overflow-hidden">
                    <div
                        className="h-full bg-gradient-to-r from-purple-500 to-indigo-500 rounded-full transition-all duration-700 ease-out"
                        style={{ width: `${(xp / 1000) * 100}%` }}
                    />
                </div>
            </div>
        </div>
    );
}

function PeerCodeReviewCard() {
    return (
        <div className="bg-[#1a1b2e]/90 backdrop-blur-xl border border-white/[0.06] rounded-2xl p-5 w-[280px] shadow-2xl shadow-black/40 animate-float-medium">
            <div className="flex items-center gap-2.5 mb-4">
                <div className="w-7 h-7 rounded-full bg-white/[0.06] flex items-center justify-center text-purple-400">
                    <CodeReviewIcon />
                </div>
                <span className="font-semibold text-white text-[15px]">
                    Peer Code Review
                </span>
            </div>
            {/* code snippet */}
            <div className="bg-[#0d0e1a] rounded-xl p-3.5 font-mono text-[12px] leading-[1.7] mb-3.5 border border-white/[0.04]">
                <div>
                    <span className="text-purple-400">const</span>{" "}
                    <span className="text-blue-300">optimize</span>{" "}
                    <span className="text-gray-500">=</span>{" "}
                    <span className="text-gray-300">(data)</span>{" "}
                    <span className="text-purple-400">{"=>"}</span>{" "}
                    <span className="text-gray-500">{"{"}</span>
                </div>
                <div className="text-green-400/70 pl-4">
                    {"// TODO: Refactor loop"}
                </div>
                <div className="pl-4">
                    <span className="text-purple-400">return</span>{" "}
                    <span className="text-blue-300">data</span>
                    <span className="text-gray-400">.map(</span>
                    <span className="text-orange-300">x</span>{" "}
                    <span className="text-purple-400">{"=>"}</span>{" "}
                    <span className="text-gray-400">...);</span>
                </div>
                <div className="text-gray-500">{"}"}</div>
            </div>
            <div className="flex items-center gap-2">
                <div className="w-5 h-5 rounded-full bg-gradient-to-br from-amber-400 to-orange-500" />
                <span className="text-gray-500 text-[12px]">
                    Reviewed 2m ago
                </span>
            </div>
        </div>
    );
}

function DailyStreakCard() {
    const streakDays = [true, true, true, true, false];

    return (
        <div className="bg-[#1a1b2e]/90 backdrop-blur-xl border border-white/[0.06] rounded-2xl p-5 w-[200px] shadow-2xl shadow-black/40 animate-float-fast">
            <div className="flex items-center gap-2 mb-3">
                <div className="w-7 h-7 rounded-full bg-white/[0.06] flex items-center justify-center text-purple-400">
                    <StreakIcon />
                </div>
                <span className="font-semibold text-white text-[15px]">
                    Daily Streak
                </span>
            </div>
            <div className="mb-3">
                <span className="text-3xl font-bold text-white">14</span>
                <span className="text-gray-500 text-sm ml-1.5">days</span>
            </div>
            <div className="flex gap-1.5">
                {streakDays.map((active, i) => (
                    <div
                        key={i}
                        className={`h-8 flex-1 rounded-md transition-colors ${
                            active
                                ? "bg-indigo-500/80"
                                : "bg-white/[0.06]"
                        }`}
                    />
                ))}
            </div>
        </div>
    );
}

function StatsBar() {
    const avatars = [
        "bg-gradient-to-br from-purple-400 to-pink-500",
        "bg-gradient-to-br from-blue-400 to-cyan-500",
        "bg-gradient-to-br from-amber-400 to-orange-500",
    ];

    return (
        <div className="flex items-center gap-8 flex-wrap">
            <div>
                <span className="text-3xl font-bold text-white">10k+</span>
                <p className="text-gray-500 text-sm mt-0.5">Active Devs</p>
            </div>
            <div>
                <span className="text-3xl font-bold text-white">50+</span>
                <p className="text-gray-500 text-sm mt-0.5">Tech Stacks</p>
            </div>
            <div className="flex items-center gap-2.5">
                <div className="flex -space-x-2">
                    {avatars.map((bg, i) => (
                        <div
                            key={i}
                            className={`w-8 h-8 rounded-full ${bg} border-2 border-[#0d0e1a] ring-1 ring-white/10`}
                        />
                    ))}
                    <div className="w-8 h-8 rounded-full bg-white/[0.06] border-2 border-[#0d0e1a] flex items-center justify-center text-[10px] text-gray-400 font-medium ring-1 ring-white/10">
                        +
                    </div>
                </div>
                <span className="text-purple-400 text-sm font-medium">
                    Join the community
                </span>
            </div>
        </div>
    );
}

function Footer() {
    return (
        <footer className="border-t border-white/[0.06] px-8 md:px-12 py-6 flex flex-col md:flex-row items-center justify-between gap-4 text-[13px] text-gray-600">
            <span>© 2026 Journi.dev. All rights reserved.</span>
            <div className="flex gap-6">
                <a
                    href="#"
                    className="hover:text-gray-400 transition-colors"
                >
                    Terms
                </a>
                <a
                    href="#"
                    className="hover:text-gray-400 transition-colors"
                >
                    Privacy
                </a>
                <a
                    href="#"
                    className="hover:text-gray-400 transition-colors"
                >
                    System Status
                </a>
            </div>
        </footer>
    );
}

/* ────────────────────────── main page ────────────────────────── */

export default function Welcome() {
    return (
        <div className="min-h-screen bg-[#0d0e1a] text-gray-300 flex flex-col font-sans relative overflow-hidden">
            {/* subtle radial glow behind hero */}
            <div
                className="pointer-events-none absolute top-[-20%] left-[-10%] w-[70%] h-[70%] rounded-full opacity-30"
                style={{
                    background:
                        "radial-gradient(circle, rgba(139,92,246,0.15) 0%, transparent 70%)",
                }}
            />

            <div className="flex-1 flex flex-col">
                {/* ─── header / logo ─── */}
                <header className="px-8 md:px-12 pt-8 pb-2">
                    <Logo />
                </header>

                {/* ─── hero area ─── */}
                <main className="flex-1 px-8 md:px-12 pb-12 flex flex-col lg:flex-row gap-12 lg:gap-8 items-start">
                    {/* left column */}
                    <div className="flex-1 flex flex-col justify-center pt-8 lg:pt-16 max-w-xl">
                        <h1 className="text-5xl md:text-6xl lg:text-7xl font-extrabold leading-[1.05] tracking-tight mb-6">
                            <span className="text-white">LEVEL UP</span>
                            <br />
                            <span className="bg-gradient-to-r from-purple-400 via-violet-400 to-indigo-400 bg-clip-text text-transparent">
                                YOUR CODE.
                            </span>
                        </h1>

                        <p className="text-gray-400 text-base md:text-lg leading-relaxed max-w-md mb-10">
                            The interactive LMS and social network built
                            exclusively for developers. Master new stacks,
                            review code, and climb the skill tree in a
                            dark-mode optimized environment.
                        </p>

                        {/* auth buttons */}
                        <div className="flex flex-col sm:flex-row items-center gap-5 mb-12">
                            <Link
                                id="btn-signup"
                                to="/signup"
                                className="w-full sm:w-auto flex items-center justify-center bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-semibold text-[15px] px-8 py-3.5 rounded-xl hover:from-purple-500 hover:to-indigo-500 transition-all duration-200 shadow-lg shadow-purple-500/25 active:scale-[0.98] no-underline"
                            >
                                Sign Up
                            </Link>

                            <div className="flex items-center gap-3 select-none">
                                <div className="w-6 h-px bg-white/[0.08]" />
                                <span className="text-[11px] text-gray-600 uppercase tracking-widest font-bold">
                                    or
                                </span>
                                <div className="w-6 h-px bg-white/[0.08]" />
                            </div>

                            <Link
                                id="btn-signin"
                                to="/signin"
                                className="w-full sm:w-auto flex items-center justify-center bg-white/[0.03] border border-white/[0.08] text-gray-300 font-semibold text-[15px] px-8 py-3.5 rounded-xl hover:bg-white/[0.06] hover:border-white/[0.12] transition-all duration-200 active:scale-[0.98] no-underline"
                            >
                                Sign In
                            </Link>
                        </div>

                        {/* stats */}
                        <StatsBar />
                    </div>

                    {/* right column – floating cards */}
                    <div className="hidden lg:flex flex-1 relative min-h-[520px] items-start justify-end pt-4">
                        <div className="relative w-full max-w-[400px]">
                            {/* Skill Tree – top right */}
                            <div className="absolute top-0 right-0">
                                <SkillTreeCard />
                            </div>
                            {/* Peer Code Review – middle left */}
                            <div className="absolute top-[140px] -left-10">
                                <PeerCodeReviewCard />
                            </div>
                            {/* Daily Streak – bottom right */}
                            <div className="absolute top-[310px] right-0">
                                <DailyStreakCard />
                            </div>
                        </div>
                    </div>
                </main>
            </div>

            {/* ─── footer ─── */}
            <Footer />
        </div>
    );
}
