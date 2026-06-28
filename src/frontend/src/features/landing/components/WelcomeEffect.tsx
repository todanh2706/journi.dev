import { useState, useCallback } from "react";
import { TerminalEffect } from "./TerminalEffect";
import { ConstellationEffect } from "./ConstellationEffect";

export function WelcomeEffect() {
    const [phase, setPhase] = useState<"terminal" | "transition" | "constellation">("terminal");

    const handleTerminalComplete = useCallback(() => {
        setPhase("transition");
        setTimeout(() => {
            setPhase("constellation");
        }, 500); // Wait for terminal fade out
    }, []);

    return (
        <div className="flex w-full flex-col items-center justify-center min-h-[400px]">
            {phase !== "constellation" && (
                <div className={`w-full max-w-2xl ${phase === "transition" ? "animate-fade-out" : ""}`}>
                    <TerminalEffect onComplete={handleTerminalComplete} />
                </div>
            )}

            {phase === "constellation" && (
                <ConstellationEffect />
            )}
        </div>
    );
}
