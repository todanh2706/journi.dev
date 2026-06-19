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


export function DailyStreakCard() {
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
