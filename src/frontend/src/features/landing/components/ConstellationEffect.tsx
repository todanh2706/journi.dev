export function ConstellationEffect() {
    // Hardcoded constellation layout for the welcome effect
    const nodes = [
        { id: 1, x: 20, y: 50, label: "Basics" },
        { id: 2, x: 40, y: 30, label: "Java" },
        { id: 3, x: 60, y: 40, label: "Spring Boot" },
        { id: 4, x: 80, y: 60, label: "Database" },
        { id: 5, x: 50, y: 70, label: "API" },
    ];

    const edges = [
        { from: 1, to: 2 },
        { from: 2, to: 3 },
        { from: 3, to: 4 },
        { from: 1, to: 5 },
        { from: 5, to: 4 },
    ];

    return (
        <div className="relative h-64 w-full max-w-3xl animate-fade-in-up">
            {/* Draw edges (lines) */}
            <svg className="absolute inset-0 h-full w-full pointer-events-none">
                {edges.map((edge, idx) => {
                    const fromNode = nodes.find((n) => n.id === edge.from);
                    const toNode = nodes.find((n) => n.id === edge.to);
                    if (!fromNode || !toNode) return null;

                    return (
                        <line
                            key={idx}
                            x1={`${fromNode.x}%`}
                            y1={`${fromNode.y}%`}
                            x2={`${toNode.x}%`}
                            y2={`${toNode.y}%`}
                            className="stroke-line"
                            strokeWidth="2"
                        />
                    );
                })}
            </svg>

            {/* Draw nodes */}
            {nodes.map((node) => (
                <div
                    key={node.id}
                    className="absolute flex flex-col items-center justify-center -translate-x-1/2 -translate-y-1/2"
                    style={{ left: `${node.x}%`, top: `${node.y}%` }}
                >
                    <div className="h-4 w-4 rounded-full border-2 border-gold bg-canvas animate-node-pulse shadow-[0_0_15px_var(--color-gold)]"></div>
                    <span className="mt-2 text-xs font-semibold text-gold-strong whitespace-nowrap">
                        {node.label}
                    </span>
                </div>
            ))}
        </div>
    );
}
