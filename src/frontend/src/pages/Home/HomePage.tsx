import { ArrowRight } from "lucide-react";
import { Link, Navigate } from "react-router-dom";

import { Logo } from "../../components/Logo/Logo";
import { useAuth } from "../../features/auth";
import { WelcomeEffect } from "../../features/landing/components/WelcomeEffect";

export default function HomePage() {
    const { user, isLoading } = useAuth();

    // If authenticated, redirect to dashboard.
    // The welcome animation is exclusively for unauthenticated users.
    if (!isLoading && user) {
        return <Navigate to="/dashboard" replace />;
    }

    return (
        <div className="min-h-screen bg-canvas text-ink flex flex-col">
            <header className="mx-auto flex w-full max-w-7xl items-center justify-between px-5 py-6 sm:px-8 lg:px-10">
                <Logo />
                {isLoading ? (
                    <span className="secondary-button cursor-wait text-muted">Loading…</span>
                ) : (
                    <Link to="/signin" className="secondary-button">
                        Sign in
                    </Link>
                )}
            </header>

            <main className="flex-1 flex flex-col items-center justify-center w-full max-w-7xl mx-auto px-5 pb-16 pt-12 sm:px-8 sm:pt-20 lg:px-10">
                <section className="w-full flex flex-col items-center text-center">
                    <p className="eyebrow">Developer roadmap tracker</p>
                    <h1 className="mt-5 max-w-3xl text-5xl font-semibold leading-[1.02] tracking-[-0.06em] text-ink sm:text-6xl lg:text-7xl">
                        One path.<br />A clear next skill.
                    </h1>
                    <p className="mt-6 max-w-xl text-base leading-7 text-muted sm:text-lg">
                        Journi.dev turns a developer career roadmap into a sequence you can actually follow—without losing the bigger picture.
                    </p>

                    <div className="mt-12 w-full max-w-3xl">
                        <WelcomeEffect />
                    </div>

                    <div className="mt-12 flex flex-col gap-3 sm:flex-row justify-center">
                        {isLoading ? (
                            <span className="primary-button cursor-wait opacity-70">Loading…</span>
                        ) : (
                            <Link to="/signup" className="primary-button">
                                Create an account
                                <ArrowRight aria-hidden="true" size={17} />
                            </Link>
                        )}
                        {!isLoading && !user ? (
                            <Link to="/signin" className="secondary-button">I already have an account</Link>
                        ) : null}
                    </div>
                </section>
            </main>

            <footer className="border-t border-line px-5 py-6 text-center text-xs text-subtle">
                Journi.dev · Built around the next useful step.
            </footer>
        </div>
    );
}
