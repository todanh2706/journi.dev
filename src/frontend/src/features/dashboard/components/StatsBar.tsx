export function StatsBar() {
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