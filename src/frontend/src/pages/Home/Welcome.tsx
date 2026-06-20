import { ArrowRight, BookOpenCheck, LockKeyhole, Map } from "lucide-react";
import { Link } from "react-router-dom";

import { Logo } from "../../components/Logo/Logo";
import { useAuth } from "../../features/auth";

const steps = [
  { icon: Map, label: "Choose a predefined career roadmap" },
  { icon: BookOpenCheck, label: "Open each skill and follow its learning material" },
  { icon: LockKeyhole, label: "Complete prerequisites to reveal what comes next" },
];

export default function Welcome() {
  const { user } = useAuth();

  return (
    <div className="min-h-screen bg-canvas text-ink">
      <header className="mx-auto flex w-full max-w-7xl items-center justify-between px-5 py-6 sm:px-8 lg:px-10">
        <Logo />
        <Link to={user ? "/dashboard" : "/signin"} className="secondary-button">
          {user ? "Open workspace" : "Sign in"}
        </Link>
      </header>

      <main className="mx-auto grid w-full max-w-7xl gap-12 px-5 pb-16 pt-12 sm:px-8 sm:pt-20 lg:grid-cols-[minmax(0,1.05fr)_minmax(360px,0.75fr)] lg:items-center lg:px-10 lg:pb-24 lg:pt-28">
        <section>
          <p className="eyebrow">Developer roadmap tracker</p>
          <h1 className="mt-5 max-w-3xl text-5xl font-semibold leading-[1.02] tracking-[-0.06em] text-ink sm:text-6xl lg:text-7xl">
            One path.<br />A clear next skill.
          </h1>
          <p className="mt-6 max-w-xl text-base leading-7 text-muted sm:text-lg">
            Journi.dev turns a developer career roadmap into a sequence you can actually follow—without losing the bigger picture.
          </p>

          <div className="mt-9 flex flex-col gap-3 sm:flex-row">
            <Link to={user ? "/dashboard/roadmaps" : "/signup"} className="primary-button">
              {user ? "Browse roadmaps" : "Create an account"}
              <ArrowRight aria-hidden="true" size={17} />
            </Link>
            {!user ? (
              <Link to="/signin" className="secondary-button">I already have an account</Link>
            ) : null}
          </div>
        </section>

        <section className="app-panel overflow-hidden" aria-labelledby="journey-heading">
          <div className="border-b border-line px-5 py-5 sm:px-6">
            <p className="eyebrow">The MVP journey</p>
            <h2 id="journey-heading" className="mt-2 text-xl font-semibold text-ink">From roadmap to momentum</h2>
          </div>
          <ol>
            {steps.map(({ icon: Icon, label }, index) => (
              <li key={label} className="flex gap-4 border-b border-line px-5 py-5 last:border-b-0 sm:px-6">
                <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl border border-line-strong bg-surface-elevated text-gold">
                  <Icon aria-hidden="true" size={19} />
                </div>
                <div>
                  <p className="text-xs font-semibold uppercase tracking-[0.13em] text-subtle">Step {index + 1}</p>
                  <p className="mt-1 text-sm leading-6 text-ink">{label}</p>
                </div>
              </li>
            ))}
          </ol>
        </section>
      </main>

      <footer className="border-t border-line px-5 py-6 text-center text-xs text-subtle">
        Journi.dev · Built around the next useful step.
      </footer>
    </div>
  );
}
