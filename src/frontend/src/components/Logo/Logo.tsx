import { Link } from "react-router-dom";

export function Logo() {
    return (
        <Link
            to="/"
            aria-label="Journi.dev home"
            className="inline-flex items-center gap-2.5 rounded-lg no-underline"
        >
            <img src="/logo.png" alt="" aria-hidden="true" className="h-8 w-8 object-contain" />
            <span className="text-lg tracking-[-0.025em]">
                <span className="font-semibold text-ink">Journi</span>
                <span className="font-semibold text-gold">.dev</span>
            </span>
        </Link>
    );
}
