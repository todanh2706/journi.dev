import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";

/* ──────────────────────── sub‑components ──────────────────────── */
import { GitHubIcon } from "./components/Icons/GitHubIcon";
import { GoogleIcon } from "./components/Icons/GoogleIcon";
import { EyeIcon } from "./components/Icons/EyeIcon";
import { EyeOffIcon } from "./components/Icons/EyeOffIcon";
import { ArrowLeftIcon } from "./components/Icons/ArrowLeftIcon";
import { MailIcon } from "./components/Icons/MailIcon";
import { LockIcon } from "./components/Icons/LockIcon";
import { Logo } from "../../components/Logo/Logo";
import { SocialButton } from "./components/Buttons/SocialButton";
import { AxiosError } from "axios";

/* ────────────────────────── api ────────────────────────── */
import { login } from "./services/auth";



/* ────────────────────────── main page ────────────────────────── */

export default function SignIn() {
    const navigate = useNavigate();
    const [showPassword, setShowPassword] = useState(false);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [rememberMe, setRememberMe] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError("");
        
        try {
            const response = await login({ username, password });
            localStorage.setItem("access_token", response.token);
            navigate("/dashboard");
        } catch (err) {
            if (err instanceof AxiosError && err.response?.status === 401) {
                setError("Invalid username or password. Please try again.");
            } else {
                setError("We couldn't sign you in right now. Please try again later.");
            }
            console.error("Sign in failed:", err);
        }
    };

    return (
        <div className="min-h-screen bg-[#0d0e1a] text-gray-300 flex font-sans relative overflow-hidden">
            {/* ─── ambient glows ─── */}
            <div
                className="pointer-events-none absolute top-[-30%] right-[-10%] w-[60%] h-[60%] rounded-full opacity-20"
                style={{
                    background:
                        "radial-gradient(circle, rgba(139,92,246,0.18) 0%, transparent 70%)",
                }}
            />
            <div
                className="pointer-events-none absolute bottom-[-20%] left-[-10%] w-[50%] h-[50%] rounded-full opacity-15"
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
                    {error && (
                        <div className="mb-6 p-3 bg-red-500/10 border border-red-500/20 rounded-xl text-red-400 text-sm">
                            {error}
                        </div>
                    )}

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
                        {/* username */}
                        <div className="space-y-1.5">
                            <label
                                htmlFor="username"
                                className="block text-[13px] font-medium text-gray-400"
                            >
                                Username or Email
                            </label>
                            <div className="relative">
                                <div className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-600">
                                    <MailIcon />
                                </div>
                                <input
                                    id="username"
                                    type="text"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    placeholder="Enter your username or email"
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
                                className={`w-[18px] h-[18px] rounded-[5px] border flex items-center justify-center transition-all duration-200 cursor-pointer ${rememberMe
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
                            href="/signup"
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
