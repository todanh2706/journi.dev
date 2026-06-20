import type { ReactNode } from "react";
import { ArrowLeft, Check } from "lucide-react";
import { Link } from "react-router-dom";

import { Logo } from "../../../components/Logo/Logo";

interface AuthShellProps {
  eyebrow: string;
  title: string;
  description: string;
  children: ReactNode;
  footer: ReactNode;
}

const journeySteps = [
  "Choose a career roadmap",
  "Open the next available skill",
  "Work through the path in order",
];

export function AuthShell({ eyebrow, title, description, children, footer }: AuthShellProps) {
  return (
    <main className="min-h-screen bg-canvas text-ink lg:grid lg:grid-cols-[minmax(320px,0.78fr)_minmax(0,1.22fr)]">
      <section className="hidden border-r border-line bg-shell p-10 lg:flex lg:min-h-screen lg:flex-col lg:justify-between xl:p-14">
        <Logo />

        <div className="max-w-sm">
          <p className="eyebrow">A clearer way forward</p>
          <h2 className="mt-4 text-3xl font-semibold leading-tight tracking-[-0.04em] text-ink">
            Learn the next skill,<br />not the whole internet.
          </h2>
          <p className="mt-4 text-sm leading-6 text-muted">
            Journi.dev keeps the learning path visible so you can spend less time deciding what comes next.
          </p>

          <ol className="mt-8 space-y-3">
            {journeySteps.map((step, index) => (
              <li key={step} className="flex items-center gap-3 border-t border-line py-3 first:border-t-0">
                <span className="flex h-7 w-7 shrink-0 items-center justify-center rounded-lg bg-gold text-gold-ink">
                  <Check aria-hidden="true" size={15} strokeWidth={2.4} />
                </span>
                <span className="text-sm text-ink">
                  <span className="mr-2 text-subtle">0{index + 1}</span>
                  {step}
                </span>
              </li>
            ))}
          </ol>
        </div>

        <p className="text-xs text-subtle">© 2026 Journi.dev</p>
      </section>

      <section className="flex min-h-screen items-center justify-center px-4 py-8 sm:px-6 lg:px-10">
        <div className="w-full max-w-[450px]">
          <div className="mb-8 flex items-center justify-between lg:block">
            <Link to="/" className="inline-flex items-center gap-2 rounded-lg text-sm text-muted transition-colors hover:text-ink">
              <ArrowLeft aria-hidden="true" size={16} />
              Back to home
            </Link>
            <div className="lg:hidden">
              <Logo />
            </div>
          </div>

          <div className="app-panel p-5 sm:p-7">
            <header className="mb-7">
              <p className="eyebrow">{eyebrow}</p>
              <h1 className="mt-3 text-3xl font-semibold tracking-[-0.04em] text-ink">{title}</h1>
              <p className="mt-2 text-sm leading-6 text-muted">{description}</p>
            </header>
            {children}
          </div>

          <div className="mt-6 text-center text-sm text-muted">{footer}</div>
        </div>
      </section>
    </main>
  );
}
