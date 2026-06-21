import { useCallback, useEffect, useState } from "react";
import { AlertCircle, ArrowRight, LoaderCircle, Map, Route } from "lucide-react";
import { Link } from "react-router-dom";

import { useAuth } from "../../features/auth";
import { roadmapService, type Roadmap } from "../../features/roadmaps";

export default function DashboardOverview() {
  const { user, isLoading: authLoading } = useAuth();
  const [roadmaps, setRoadmaps] = useState<Roadmap[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadRoadmaps = useCallback(async () => {
    if (!user) return;

    try {
      const response = await roadmapService.getRoadmaps();
      setRoadmaps(Array.isArray(response) ? response : []);
    } catch {
      setError("Roadmaps could not be loaded. Check your connection and try again.");
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    if (!user) return;
    let active = true;

    roadmapService.getRoadmaps()
      .then((response) => {
        if (active) setRoadmaps(Array.isArray(response) ? response : []);
      })
      .catch(() => {
        if (active) setError("Roadmaps could not be loaded. Check your connection and try again.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [user]);

  const retryRoadmaps = () => {
    setLoading(true);
    setError(null);
    void loadRoadmaps();
  };

  return (
    <div className="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 sm:py-10 lg:px-10 lg:py-12">
      <header className="max-w-3xl">
        <p className="eyebrow">Learning workspace</p>
        <h1 className="mt-3 text-3xl font-semibold tracking-[-0.045em] text-ink sm:text-4xl">
          {user ? `Welcome back, ${user.username}` : "Your roadmap starts here"}
        </h1>
        <p className="mt-3 text-sm leading-6 text-muted sm:text-base">
          {user
            ? "Choose a roadmap, inspect the next skill, and keep the full path in view."
            : "Sign in to open the roadmap catalog and keep your learning workspace tied to your account."}
        </p>
      </header>

      {authLoading ? (
        <section className="app-panel mt-8 flex min-h-56 items-center justify-center p-8" aria-live="polite">
          <div className="text-center text-muted">
            <LoaderCircle aria-hidden="true" size={26} className="mx-auto animate-spin text-gold motion-reduce:animate-none" />
            <p className="mt-3 text-sm">Restoring your session…</p>
          </div>
        </section>
      ) : !user ? (
        <section className="app-panel mt-8 flex flex-col gap-5 p-6 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 className="text-lg font-semibold text-ink">Sign in to continue</h2>
            <p className="mt-1 text-sm text-muted">Your authenticated session unlocks the roadmap workspace.</p>
          </div>
          <Link to="/signin" className="primary-button shrink-0">Sign in <ArrowRight aria-hidden="true" size={17} /></Link>
        </section>
      ) : loading ? (
        <section className="app-panel mt-8 flex min-h-56 items-center justify-center p-8" aria-live="polite">
          <div className="text-center text-muted">
            <LoaderCircle aria-hidden="true" size={26} className="mx-auto animate-spin text-gold motion-reduce:animate-none" />
            <p className="mt-3 text-sm">Loading available roadmaps…</p>
          </div>
        </section>
      ) : error ? (
        <section role="alert" className="mt-8 rounded-2xl border border-danger/30 bg-danger/10 p-6">
          <AlertCircle aria-hidden="true" size={23} className="text-danger" />
          <h2 className="mt-4 text-lg font-semibold text-ink">Roadmaps are temporarily unavailable</h2>
          <p className="mt-2 text-sm text-muted">{error}</p>
          <button type="button" onClick={retryRoadmaps} className="secondary-button mt-5">Try again</button>
        </section>
      ) : roadmaps.length === 0 ? (
        <section className="app-panel mt-8 p-6 sm:p-8">
          <Map aria-hidden="true" size={24} className="text-gold" />
          <h2 className="mt-4 text-xl font-semibold text-ink">No roadmaps are available yet</h2>
          <p className="mt-2 max-w-lg text-sm leading-6 text-muted">The catalog is connected, but it does not currently contain a published path.</p>
        </section>
      ) : (
        <section className="mt-9" aria-labelledby="available-roadmaps-heading">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="eyebrow">Available now</p>
              <h2 id="available-roadmaps-heading" className="mt-2 text-2xl font-semibold tracking-[-0.035em] text-ink">Choose a path</h2>
            </div>
            <Link to="/dashboard/roadmaps" className="inline-flex items-center gap-2 rounded-lg text-sm font-medium text-gold hover:text-gold-strong">
              View all roadmaps <ArrowRight aria-hidden="true" size={16} />
            </Link>
          </div>

          <div className="mt-5 grid gap-4 md:grid-cols-2">
            {roadmaps.slice(0, 4).map((roadmap) => (
              <Link key={roadmap.roadmapId} to={`/dashboard/roadmaps/${roadmap.roadmapId}`} className="app-panel group flex min-h-48 flex-col p-5 transition-colors hover:border-line-strong hover:bg-surface-elevated sm:p-6">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-line-strong bg-surface-elevated text-gold">
                    <Route aria-hidden="true" size={19} />
                  </div>
                  <span className="text-xs font-medium uppercase tracking-[0.12em] text-subtle">Structured path</span>
                </div>
                <h3 className="mt-6 text-lg font-semibold text-ink group-hover:text-gold-strong">{roadmap.title}</h3>
                {roadmap.description ? <p className="mt-2 line-clamp-2 text-sm leading-6 text-muted">{roadmap.description}</p> : null}
                <span className="mt-auto inline-flex items-center gap-2 pt-5 text-sm font-medium text-gold">Open roadmap <ArrowRight aria-hidden="true" size={16} /></span>
              </Link>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}
