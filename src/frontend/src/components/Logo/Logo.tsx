import { Link } from "react-router-dom";

export function Logo() {
    return (
        <Link to="/" className="flex items-center gap-2.5 no-underline">
            <div className="w-9 h-9 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-lg flex items-center justify-center text-white font-bold text-sm select-none shadow-lg shadow-purple-500/20">
                {">_"}
            </div>
            <span className="text-lg tracking-tight">
                <span className="font-semibold text-white">Journi</span>
                <span className="text-purple-400 font-semibold">.dev</span>
            </span>
        </Link>
    );
}
