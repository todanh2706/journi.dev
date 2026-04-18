import { useState } from "react";
import { Link } from "react-router-dom";

/* ───────────────────── inline SVG icons ───────────────────── */

const GitHubIcon = () => (
    <svg
        width="20"
        height="20"
        viewBox="0 0 24 24"
        fill="currentColor"
        aria-hidden="true"
    >
        <path d="M12 .5C5.37.5 0 5.78 0 12.292c0 5.21 3.438 9.63 8.205 11.188.6.112.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61-.546-1.385-1.333-1.754-1.333-1.754-1.089-.745.083-.73.083-.73 1.205.085 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.418-1.305.762-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23a11.5 11.5 0 0 1 3.003-.404c1.02.005 2.047.138 3.006.404 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.694.825.576C20.565 21.917 24 17.495 24 12.292 24 5.78 18.627.5 12 .5z" />
    </svg>
);

const GoogleIcon = () => (
    <svg width="20" height="20" viewBox="0 0 24 24" aria-hidden="true">
        <path
            d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z"
            fill="#4285F4"
        />
        <path
            d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
            fill="#34A853"
        />
        <path
            d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
            fill="#FBBC05"
        />
        <path
            d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
            fill="#EA4335"
        />
    </svg>
);

const EyeIcon = () => (
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
        <path d="M2.062 12.348a1 1 0 0 1 0-.696 10.75 10.75 0 0 1 19.876 0 1 1 0 0 1 0 .696 10.75 10.75 0 0 1-19.876 0" />
        <circle cx="12" cy="12" r="3" />
    </svg>
);

const EyeOffIcon = () => (
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
        <path d="M10.733 5.076a10.744 10.744 0 0 1 11.205 6.575 1 1 0 0 1 0 .696 10.747 10.747 0 0 1-1.444 2.49" />
        <path d="M14.084 14.158a3 3 0 0 1-4.242-4.242" />
        <path d="M17.479 17.499a10.75 10.75 0 0 1-15.417-5.151 1 1 0 0 1 0-.696 10.75 10.75 0 0 1 4.446-5.143" />
        <path d="m2 2 20 20" />
    </svg>
);

const ArrowLeftIcon = () => (
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
        <path d="m12 19-7-7 7-7" />
        <path d="M19 12H5" />
    </svg>
);

const MailIcon = () => (
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
        <rect x="2" y="4" width="20" height="16" rx="2" />
        <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
    </svg>
);

const LockIcon = () => (
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
        <rect width="18" height="11" x="3" y="11" rx="2" ry="2" />
        <path d="M7 11V7a5 5 0 0 1 10 0v4" />
    </svg>
);

/* ──────────────────────── sub‑components ──────────────────────── */

function Logo() {
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

function SocialButton({
    icon,
    label,
    id,
}: {
    icon: React.ReactNode;
    label: string;
    id: string;
}) {
    return (
        <button
            id={id}
            className="flex items-center justify-center gap-2.5 w-full bg-white/[0.03] border border-white/[0.08] text-gray-300 font-medium text-sm py-3 rounded-xl hover:bg-white/[0.07] hover:border-white/[0.14] transition-all duration-200 cursor-pointer active:scale-[0.98]"
        >
            {icon}
            {label}
        </button>
    );
}

/* ────────────────────────── main page ────────────────────────── */

export default function SignIn() {
    const [showPassword, setShowPassword] = useState(false);
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [rememberMe, setRememberMe] = useState(false);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        // TODO: hook up auth
        console.log("Sign in:", { email, password, rememberMe });
    };

    return (
        <div className="min-h-screen bg-[#0d0e1a] text-gray-300 flex font-sans relative overflow-hidden">
            {/* ─── ambient glows ─── */}
            <div
                className="pointer-events-none fixed top-[-30%] right-[-10%] w-[60%] h-[60%] rounded-full opacity-20"
                style={{
                    background:
                        "radial-gradient(circle, rgba(139,92,246,0.18) 0%, transparent 70%)",
                }}
            />
            <div
                className="pointer-events-none fixed bottom-[-20%] left-[-10%] w-[50%] h-[50%] rounded-full opacity-15"
                style={{
                    background:
                        "radial-gradient(circle, rgba(79,70,229,0.15) 0%, transparent 70%)",
                }}
            />

            {/* ─── left decorative panel (lg+) ─── */}
            <div className="hidden lg:flex flex-col justify-between w-[480px] xl:w-[520px] bg-[#12131f] border-r border-white/[0.04] p-10 relative overflow-hidden">
                {/* subtle grid pattern */}
                <div
                    className="pointer-events-none absolute inset-0 opacity-[0.03]"
                    style={{
                        backgroundImage:
                            "linear-gradient(rgba(255,255,255,0.1) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.1) 1px, transparent 1px)",
                        backgroundSize: "40px 40px",
                    }}
                />

                <div className="relative z-10">
                    <Logo />
                </div>

                <div className="relative z-10 space-y-6">
                    {/* decorative code block */}
                    <div className="bg-[#0d0e1a]/80 backdrop-blur rounded-2xl p-5 border border-white/[0.04] shadow-2xl shadow-black/30">
                        <div className="flex items-center gap-2 mb-4">
                            <div className="w-3 h-3 rounded-full bg-red-500/60" />
                            <div className="w-3 h-3 rounded-full bg-yellow-500/60" />
                            <div className="w-3 h-3 rounded-full bg-green-500/60" />
                            <span className="ml-2 text-[11px] text-gray-600 font-mono">
                                welcome.ts
                            </span>
                        </div>
                        <div className="font-mono text-[13px] leading-[1.8] text-gray-500">
                            <div>
                                <span className="text-purple-400">const</span>{" "}
                                <span className="text-blue-300">developer</span>{" "}
                                <span className="text-gray-600">=</span>{" "}
                                <span className="text-gray-400">{"{"}</span>
                            </div>
                            <div className="pl-5">
                                <span className="text-green-400/70">
                                    name
                                </span>
                                <span className="text-gray-600">: </span>
                                <span className="text-amber-300/80">
                                    "you"
                                </span>
                                <span className="text-gray-600">,</span>
                            </div>
                            <div className="pl-5">
                                <span className="text-green-400/70">
                                    level
                                </span>
                                <span className="text-gray-600">: </span>
                                <span className="text-orange-300">
                                    Infinity
                                </span>
                                <span className="text-gray-600">,</span>
                            </div>
                            <div className="pl-5">
                                <span className="text-green-400/70">
                                    passion
                                </span>
                                <span className="text-gray-600">: </span>
                                <span className="text-amber-300/80">
                                    "unstoppable"
                                </span>
                            </div>
                            <div>
                                <span className="text-gray-400">{"}"}</span>
                                <span className="text-gray-600">;</span>
                            </div>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <h2 className="text-2xl font-bold text-white leading-tight">
                            Your journey to mastery
                            <br />
                            <span className="bg-gradient-to-r from-purple-400 to-indigo-400 bg-clip-text text-transparent">
                                starts here.
                            </span>
                        </h2>
                        <p className="text-gray-500 text-sm leading-relaxed max-w-sm">
                            Join 10,000+ developers leveling up their skills
                            through interactive challenges and peer code
                            reviews.
                        </p>
                    </div>
                </div>

                <div className="relative z-10 text-[12px] text-gray-600">
                    © 2026 Journi.dev. All rights reserved.
                </div>
            </div>

            {/* ─── right panel: sign-in form ─── */}
            <div className="flex-1 flex items-center justify-center p-6 md:p-10">
                <div className="w-full max-w-[420px]">
                    {/* back link (visible on mobile + desktop) */}
                    <Link
                        to="/"
                        className="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-300 transition-colors mb-8 no-underline"
                    >
                        <ArrowLeftIcon />
                        Back to home
                    </Link>

                    {/* mobile logo */}
                    <div className="lg:hidden mb-8">
                        <Logo />
                    </div>

                    {/* heading */}
                    <div className="mb-8">
                        <h1 className="text-3xl font-bold text-white mb-2">
                            Welcome back
                        </h1>
                        <p className="text-gray-500 text-[15px]">
                            Sign in to continue your learning journey.
                        </p>
                    </div>

                    {/* social login */}
                    <div className="grid grid-cols-2 gap-3 mb-6">
                        <SocialButton
                            id="btn-signin-github"
                            icon={<GitHubIcon />}
                            label="GitHub"
                        />
                        <SocialButton
                            id="btn-signin-google"
                            icon={<GoogleIcon />}
                            label="Google"
                        />
                    </div>

                    {/* divider */}
                    <div className="flex items-center gap-4 mb-6">
                        <div className="flex-1 h-px bg-white/[0.06]" />
                        <span className="text-[12px] text-gray-600 uppercase tracking-widest font-medium">
                            or continue with email
                        </span>
                        <div className="flex-1 h-px bg-white/[0.06]" />
                    </div>

                    {/* form */}
                    <form onSubmit={handleSubmit} className="space-y-4">
                        {/* email */}
                        <div className="space-y-1.5">
                            <label
                                htmlFor="email"
                                className="block text-[13px] font-medium text-gray-400"
                            >
                                Email address
                            </label>
                            <div className="relative">
                                <div className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-600">
                                    <MailIcon />
                                </div>
                                <input
                                    id="email"
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    placeholder="you@example.com"
                                    className="w-full bg-white/[0.03] border border-white/[0.08] rounded-xl py-3 pl-11 pr-4 text-sm text-white placeholder:text-gray-600 outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/20 transition-all duration-200"
                                    required
                                />
                            </div>
                        </div>

                        {/* password */}
                        <div className="space-y-1.5">
                            <div className="flex items-center justify-between">
                                <label
                                    htmlFor="password"
                                    className="block text-[13px] font-medium text-gray-400"
                                >
                                    Password
                                </label>
                                <a
                                    href="#"
                                    className="text-[12px] text-purple-400 hover:text-purple-300 transition-colors no-underline"
                                >
                                    Forgot password?
                                </a>
                            </div>
                            <div className="relative">
                                <div className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-600">
                                    <LockIcon />
                                </div>
                                <input
                                    id="password"
                                    type={showPassword ? "text" : "password"}
                                    value={password}
                                    onChange={(e) =>
                                        setPassword(e.target.value)
                                    }
                                    placeholder="••••••••"
                                    className="w-full bg-white/[0.03] border border-white/[0.08] rounded-xl py-3 pl-11 pr-11 text-sm text-white placeholder:text-gray-600 outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/20 transition-all duration-200"
                                    required
                                />
                                <button
                                    type="button"
                                    onClick={() =>
                                        setShowPassword(!showPassword)
                                    }
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-600 hover:text-gray-400 transition-colors cursor-pointer bg-transparent border-none p-0.5"
                                    aria-label={
                                        showPassword
                                            ? "Hide password"
                                            : "Show password"
                                    }
                                >
                                    {showPassword ? (
                                        <EyeOffIcon />
                                    ) : (
                                        <EyeIcon />
                                    )}
                                </button>
                            </div>
                        </div>

                        {/* remember me */}
                        <div className="flex items-center gap-2.5 pt-1">
                            <button
                                type="button"
                                role="checkbox"
                                aria-checked={rememberMe}
                                onClick={() => setRememberMe(!rememberMe)}
                                className={`w-[18px] h-[18px] rounded-[5px] border flex items-center justify-center transition-all duration-200 cursor-pointer ${
                                    rememberMe
                                        ? "bg-purple-500 border-purple-500"
                                        : "bg-transparent border-white/[0.12] hover:border-white/[0.2]"
                                }`}
                            >
                                {rememberMe && (
                                    <svg
                                        width="12"
                                        height="12"
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="white"
                                        strokeWidth="3"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                    >
                                        <path d="M20 6 9 17l-5-5" />
                                    </svg>
                                )}
                            </button>
                            <span className="text-[13px] text-gray-500">
                                Remember me for 30 days
                            </span>
                        </div>

                        {/* submit */}
                        <button
                            id="btn-signin-submit"
                            type="submit"
                            className="w-full bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-semibold text-sm py-3.5 rounded-xl hover:from-purple-500 hover:to-indigo-500 transition-all duration-200 shadow-lg shadow-purple-500/20 cursor-pointer active:scale-[0.98] mt-2!"
                        >
                            Sign in
                        </button>
                    </form>

                    {/* sign up link */}
                    <p className="text-center text-sm text-gray-500 mt-8">
                        Don't have an account?{" "}
                        <a
                            href="#"
                            className="text-purple-400 hover:text-purple-300 font-medium transition-colors no-underline"
                        >
                            Create one for free
                        </a>
                    </p>

                    {/* footer (mobile) */}
                    <div className="lg:hidden mt-12 flex items-center justify-center gap-4 text-[12px] text-gray-600">
                        <a
                            href="#"
                            className="hover:text-gray-400 transition-colors no-underline"
                        >
                            Terms
                        </a>
                        <span className="text-gray-700">·</span>
                        <a
                            href="#"
                            className="hover:text-gray-400 transition-colors no-underline"
                        >
                            Privacy
                        </a>
                        <span className="text-gray-700">·</span>
                        <a
                            href="#"
                            className="hover:text-gray-400 transition-colors no-underline"
                        >
                            Help
                        </a>
                    </div>
                </div>
            </div>
        </div>
    );
}
