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

export function PeerCodeReviewCard() {
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