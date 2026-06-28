import { useEffect, useState } from "react";
import { Terminal } from "lucide-react";

interface TerminalEffectProps {
    onComplete: () => void;
}

export function TerminalEffect({ onComplete }: TerminalEffectProps) {
    const [isTyping, setIsTyping] = useState(true);

    useEffect(() => {
        // Stop typing animation after it completes (1.5s based on CSS animation)
        const typingTimer = setTimeout(() => {
            setIsTyping(false);
        }, 1500);

        // Trigger transition to next phase after a short pause
        const completionTimer = setTimeout(() => {
            onComplete();
        }, 2200);

        return () => {
            clearTimeout(typingTimer);
            clearTimeout(completionTimer);
        };
    }, [onComplete]); // Depend on onComplete (wrapped in useCallback in parent)

    return (
        <div className="flex w-full max-w-2xl flex-col rounded-xl border border-line bg-shell shadow-2xl overflow-hidden">
            <div className="flex items-center gap-2 border-b border-line bg-surface px-4 py-3">
                <Terminal className="h-4 w-4 text-muted" />
                <span className="text-xs font-semibold text-muted">journi-cli</span>
            </div>
            <div className="p-6 text-left font-mono text-sm sm:text-base text-gold-strong">
                <div className="inline-flex items-center">
                    <span className="mr-3 text-subtle">&gt;</span>
                    <span className={isTyping ? "animate-typewriter" : ""}>
                        journi init --role=backend_engineer
                    </span>
                    {!isTyping && (
                        <span className="ml-[2px] h-5 w-2 animate-pulse bg-gold"></span>
                    )}
                </div>
            </div>
        </div>
    );
}
