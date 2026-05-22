import { Link } from "react-router-dom";
import { Logo } from "../components/Logo/Logo";

export default function NotFound() {
    return (
        <div className="min-h-screen bg-[#0d0e1a] text-gray-300 flex flex-col font-sans relative overflow-hidden">
            {/* subtle radial glow */}
            <div
                className="pointer-events-none absolute top-[20%] left-[20%] w-[60%] h-[60%] rounded-full opacity-20"
                style={{
                    background:
                        "radial-gradient(circle, rgba(139,92,246,0.15) 0%, transparent 70%)",
                }}
            />

            <div className="flex-1 flex flex-col justify-between">
                {/* Header */}
                <header className="px-8 md:px-12 pt-8 pb-2">
                    <Logo />
                </header>

                {/* Main Content */}
                <main className="flex-grow flex flex-col items-center justify-center px-6 text-center z-10">
                    <div className="relative">
                        {/* 404 Backdrop Glow */}
                        <div className="absolute -inset-4 bg-gradient-to-r from-purple-600 to-indigo-600 rounded-full blur-3xl opacity-10 animate-pulse"></div>
                        
                        <h1 className="relative text-[120px] md:text-[180px] font-black leading-none tracking-tighter bg-gradient-to-b from-white via-gray-200 to-gray-600 bg-clip-text text-transparent drop-shadow-2xl">
                            404
                        </h1>
                    </div>

                    <h2 className="text-2xl md:text-3xl font-extrabold text-white mt-2 mb-4 tracking-tight">
                        LOST IN THE CODE
                    </h2>
                    
                    <p className="text-gray-400 max-w-md mb-8 text-sm md:text-base leading-relaxed">
                        The resource you are looking for has been compiled away, deprecated, or moved to a different branch.
                    </p>

                    <Link
                        id="btn-back-home"
                        to="/"
                        className="flex items-center justify-center bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-semibold text-[15px] px-8 py-3.5 rounded-xl hover:from-purple-500 hover:to-indigo-500 transition-all duration-200 shadow-lg shadow-purple-500/25 active:scale-[0.98] no-underline"
                    >
                        Return to Homepage
                    </Link>
                </main>

                {/* Footer Spacer */}
                <div className="pb-16"></div>
            </div>
        </div>
    );
}
