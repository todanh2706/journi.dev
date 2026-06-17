import { Link } from "react-router-dom";

export function Logo() {
    return (
        <Link to="/" className="flex items-center gap-2.5 no-underline">
            <img src="logo.png" className="w-8 h-auto"/>
            <span className="text-lg tracking-tight">
                <span className="font-semibold text-white">Journi</span>
                <span className="text-purple-400 font-semibold">.dev</span>
            </span>
        </Link>
    );
}
