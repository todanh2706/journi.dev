import { Link } from "react-router-dom";

/* ──────────────────────── sub‑components ──────────────────────── */
import { Logo } from "../../components/Logo/Logo";
import {
    DailyStreakCard,
    Footer,
    PeerCodeReviewCard,
    SkillTreeCard,
    StatsBar,
} from "../../features/dashboard";
import { useAuth } from "../../features/auth";

/* ────────────────────────── main page ────────────────────────── */

export default function Welcome() {
    const { user } = useAuth();

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
                            {user ? (
                                <div className="flex flex-col items-center sm:items-start gap-3">
                                    <span className="text-gray-300 font-medium">Welcome back, {user.username}!</span>
                                    <Link
                                        id="btn-dashboard"
                                        to="/dashboard"
                                        className="w-full sm:w-auto flex items-center justify-center bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-semibold text-[15px] px-8 py-3.5 rounded-xl hover:from-purple-500 hover:to-indigo-500 transition-all duration-200 shadow-lg shadow-purple-500/25 active:scale-[0.98] no-underline"
                                    >
                                        Go to Dashboard
                                    </Link>
                                </div>
                            ) : (
                                <>
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
                                </>
                            )}
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
