import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";

/* ──────────────────────── icons ──────────────────────── */
import { GitHubIcon } from "./components/Icons/GitHubIcon";
import { GoogleIcon } from "./components/Icons/GoogleIcon";
import { EyeIcon } from "./components/Icons/EyeIcon";
import { EyeOffIcon } from "./components/Icons/EyeOffIcon";
import { ArrowLeftIcon } from "./components/Icons/ArrowLeftIcon";
import { MailIcon } from "./components/Icons/MailIcon";
import { LockIcon } from "./components/Icons/LockIcon";
import { UserIcon } from "./components/Icons/UserIcon";

/* ──────────────────────── sub‑components ──────────────────────── */
import { Logo } from "../../components/Logo/Logo";
import { SocialButton } from "./components/Buttons/SocialButton";
import { getSignupErrorMessage, signup } from "./services/auth";

/* ────────────────────────── main page ────────────────────────── */
export default function SignUp() {
    const [showPassword, setShowPassword] = useState(false);
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [agreeTerms, setAgreeTerms] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();

        if (!agreeTerms || !username || !email || !password || isSubmitting) {
            return;
        }

        setIsSubmitting(true);
        setErrorMessage("");
        setSuccessMessage("");

        try {
            const createdUser = await signup({
                username,
                email,
                password,
            });

            setSuccessMessage(
                `Account created for ${createdUser.username}. You can sign in now.`
            );
            setPassword("");
            setAgreeTerms(false);
        } catch (error) {
            setErrorMessage(getSignupErrorMessage(error));
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="min-h-screen bg-[#0d0e1a] text-gray-300 flex font-sans relative overflow-hidden">
            {/* ─── ambient glows ─── */}
            <div
                className="pointer-events-none fixed top-[-30%] right-[-10%] w-[60%] h-[60%] rounded-full opacity-20"
                style={{
                    background: "radial-gradient(circle, rgba(139,92,246,0.18) 0%, transparent 70%)",
                }}
            />
            <div
                className="pointer-events-none fixed bottom-[-20%] left-[-10%] w-[50%] h-[50%] rounded-full opacity-15"
                style={{
                    background: "radial-gradient(circle, rgba(79,70,229,0.15) 0%, transparent 70%)",
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
                                signup.ts
                            </span>
                        </div>
                        <div className="font-mono text-[13px] leading-[1.8] text-gray-500">
                            <div>
                                <span className="text-purple-400">async function</span>{" "}
                                <span className="text-blue-300">joinCommunity</span>
                                <span className="text-gray-400">(</span>
                                <span className="text-orange-300">dev</span>
                                <span className="text-gray-400">) {"{"}</span>
                            </div>
                            <div className="pl-5">
                                <span className="text-purple-400">await</span>{" "}
                                <span className="text-blue-300">dev</span>
                                <span className="text-gray-400">.</span>
                                <span className="text-blue-300">levelUp</span>
                                <span className="text-gray-400">();</span>
                            </div>
                            <div className="pl-5">
                                <span className="text-purple-400">return</span>{" "}
                                <span className="text-amber-300/80">"Success"</span>
                                <span className="text-gray-600">;</span>
                            </div>
                            <div>
                                <span className="text-gray-400">{"}"}</span>
                            </div>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <h2 className="text-2xl font-bold text-white leading-tight">
                            Build the future
                            <br />
                            <span className="bg-gradient-to-r from-purple-400 to-indigo-400 bg-clip-text text-transparent">
                                line by line.
                            </span>
                        </h2>
                        <p className="text-gray-500 text-sm leading-relaxed max-w-sm">
                            Join a community of passionate developers. Share your knowledge, review code, and climb the leaderboards.
                        </p>
                    </div>
                </div>

                <div className="relative z-10 text-[12px] text-gray-600">
                    © 2026 Journi.dev. All rights reserved.
                </div>
            </div>

            {/* ─── right panel: sign-up form ─── */}
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
                            Create an account
                        </h1>
                        <p className="text-gray-500 text-[15px]">
                            Join the interactive developer network.
                        </p>
                    </div>

                    {/* social login */}
                    <div className="grid grid-cols-2 gap-3 mb-6">
                        <SocialButton
                            id="btn-signup-github"
                            icon={<GitHubIcon />}
                            label="GitHub"
                        />
                        <SocialButton
                            id="btn-signup-google"
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
                                Username
                            </label>
                            <div className="relative">
                                <div className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-600">
                                    <UserIcon />
                                </div>
                                <input
                                    id="username"
                                    type="text"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    placeholder="alexdev"
                                    className="w-full bg-white/[0.03] border border-white/[0.08] rounded-xl py-3 pl-11 pr-4 text-sm text-white placeholder:text-gray-600 outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/20 transition-all duration-200"
                                    required
                                />
                            </div>
                            <p className="text-[12px] text-gray-600">
                                Pick the username you&apos;ll use to sign in on
                                Journi.dev.
                            </p>
                        </div>

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
                            <label
                                htmlFor="password"
                                className="block text-[13px] font-medium text-gray-400"
                            >
                                Password
                            </label>
                            <div className="relative">
                                <div className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-600">
                                    <LockIcon />
                                </div>
                                <input
                                    id="password"
                                    type={showPassword ? "text" : "password"}
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder="••••••••"
                                    className="w-full bg-white/[0.03] border border-white/[0.08] rounded-xl py-3 pl-11 pr-11 text-sm text-white placeholder:text-gray-600 outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/20 transition-all duration-200"
                                    required
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-600 hover:text-gray-400 transition-colors cursor-pointer bg-transparent border-none p-0.5"
                                    aria-label={showPassword ? "Hide password" : "Show password"}
                                >
                                    {showPassword ? <EyeOffIcon /> : <EyeIcon />}
                                </button>
                            </div>
                        </div>

                        {/* terms */}
                        <div className="flex items-start gap-2.5 pt-1">
                            <button
                                type="button"
                                role="checkbox"
                                aria-checked={agreeTerms}
                                onClick={() => setAgreeTerms(!agreeTerms)}
                                className={`mt-0.5 w-[18px] h-[18px] shrink-0 rounded-[5px] border flex items-center justify-center transition-all duration-200 cursor-pointer ${agreeTerms
                                    ? "bg-purple-500 border-purple-500"
                                    : "bg-transparent border-white/[0.12] hover:border-white/[0.2]"
                                    }`}
                            >
                                {agreeTerms && (
                                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M20 6 9 17l-5-5" />
                                    </svg>
                                )}
                            </button>
                            <span className="text-[13px] text-gray-500 leading-snug">
                                I agree to the{" "}
                                <a href="#" className="text-purple-400 hover:text-purple-300 transition-colors">Terms of Service</a>
                                {" "}and{" "}
                                <a href="#" className="text-purple-400 hover:text-purple-300 transition-colors">Privacy Policy</a>.
                            </span>
                        </div>

                        {errorMessage ? (
                            <div className="rounded-xl border border-red-400/20 bg-red-500/10 px-4 py-3 text-sm text-red-200">
                                {errorMessage}
                            </div>
                        ) : null}

                        {successMessage ? (
                            <div className="rounded-xl border border-emerald-400/20 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-200">
                                <p>{successMessage}</p>
                                <Link
                                    to="/signin"
                                    className="mt-2 inline-block text-emerald-100 underline underline-offset-4"
                                >
                                    Continue to sign in
                                </Link>
                            </div>
                        ) : null}

                        {/* submit */}
                        <button
                            id="btn-signup-submit"
                            type="submit"
                            disabled={!agreeTerms || !username || !email || !password || isSubmitting}
                            className={`w-full text-white font-semibold text-sm py-3.5 rounded-xl transition-all duration-200 shadow-lg shadow-purple-500/20 mt-4! ${agreeTerms && username && email && password && !isSubmitting
                                ? "bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 cursor-pointer active:scale-[0.98]"
                                : "bg-white/[0.05] text-gray-400 cursor-not-allowed border border-white/[0.05]"
                                }`}
                        >
                            {isSubmitting ? "Creating account..." : "Create account"}
                        </button>
                    </form>

                    {/* sign in link */}
                    <p className="text-center text-sm text-gray-500 mt-8">
                        Already have an account?{" "}
                        <Link
                            to="/signin"
                            className="text-purple-400 hover:text-purple-300 font-medium transition-colors no-underline"
                        >
                            Sign in
                        </Link>
                    </p>

                    {/* footer (mobile) */}
                    <div className="lg:hidden mt-12 flex items-center justify-center gap-4 text-[12px] text-gray-600">
                        <a href="#" className="hover:text-gray-400 transition-colors no-underline">Terms</a>
                        <span className="text-gray-700">·</span>
                        <a href="#" className="hover:text-gray-400 transition-colors no-underline">Privacy</a>
                        <span className="text-gray-700">·</span>
                        <a href="#" className="hover:text-gray-400 transition-colors no-underline">Help</a>
                    </div>
                </div>
            </div>
        </div>
    );
}
