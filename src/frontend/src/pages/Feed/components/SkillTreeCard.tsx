import { useState, useEffect } from "react";

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

export function SkillTreeCard() {
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