import { useCallback, useEffect, useState } from "react";
import { AlertCircle, ArrowRight, LoaderCircle, Map, Route } from "lucide-react";
import { Link } from "react-router-dom";

import { roadmapService, type Roadmap } from "../../features/roadmaps";

export default function RoadmapsPage() {
  const [roadmaps, setRoadmaps] = useState<Roadmap[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadRoadmaps = useCallback(async () => {
    try {
      const response = await roadmapService.getRoadmaps();
      setRoadmaps(Array.isArray(response) ? response : []);
    } catch {
      setError("The roadmap catalog could not be loaded. Check your connection and try again.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let active = true;

    roadmapService.getRoadmaps()
      .then((response) => {
        if (active) setRoadmaps(Array.isArray(response) ? response : []);
      })
      .catch(() => {
        if (active) setError("The roadmap catalog could not be loaded. Check your connection and try again.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, []);

  const retryRoadmaps = () => {
    setLoading(true);
    setError(null);
    void loadRoadmaps();
  };

  return (
    <div className="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 sm:py-10 lg:px-10 lg:py-12">
      <header className="max-w-3xl">
        <p className="eyebrow">Roadmap catalog</p>
        <h1 className="mt-3 text-3xl font-semibold tracking-[-0.045em] text-ink sm:text-4xl">Choose your learning path</h1>
        <p className="mt-3 text-sm leading-6 text-muted sm:text-base">
          Open a predefined career path, inspect every skill, and keep the sequence visible while you learn.
        </p>
      </header>

      {loading ? (
        <section className="app-panel mt-8 flex min-h-64 items-center justify-center p-8" aria-live="polite">
          <div className="text-center text-muted">
            <LoaderCircle aria-hidden="true" size={27} className="mx-auto animate-spin text-gold motion-reduce:animate-none" />
            <p className="mt-3 text-sm">Loading roadmap catalog…</p>
          </div>
        </section>
      ) : error ? (
        <section role="alert" className="mt-8 rounded-2xl border border-danger/30 bg-danger/10 p-6 sm:p-8">
          <AlertCircle aria-hidden="true" size={24} className="text-danger" />
          <h2 className="mt-4 text-xl font-semibold text-ink">Unable to load roadmaps</h2>
          <p className="mt-2 max-w-lg text-sm leading-6 text-muted">{error}</p>
          <button type="button" onClick={retryRoadmaps} className="secondary-button mt-5">Try again</button>
        </section>
      ) : roadmaps.length === 0 ? (
        <section className="app-panel mt-8 p-7 text-center sm:p-10">
          <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-xl border border-line-strong bg-surface-elevated text-gold">
            <Map aria-hidden="true" size={22} />
          </div>
          <h2 className="mt-5 text-xl font-semibold text-ink">No published roadmaps</h2>
          <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-muted">The catalog is connected but does not currently contain a path.</p>
        </section>
      ) : (
        <section className="mt-8 grid gap-4 md:grid-cols-2 xl:grid-cols-3" aria-label="Available roadmaps">
          {roadmaps.map((roadmap) => (
            <Link
              key={roadmap.roadmapId}
              to={`/dashboard/roadmaps/${roadmap.roadmapId}`}
              className="app-panel group flex min-h-64 flex-col p-5 transition-colors hover:border-line-strong hover:bg-surface-elevated sm:p-6"
            >
              <div className="flex items-start justify-between gap-4">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-line-strong bg-surface-elevated text-gold">
                  <Route aria-hidden="true" size={19} />
                </div>
                {roadmap.visibility === "PUBLIC" ? <span className="rounded-lg border border-line px-2 py-1 text-[11px] font-semibold uppercase tracking-[0.12em] text-subtle">Public</span> : null}
              </div>

              <h2 className="mt-6 text-lg font-semibold leading-snug text-ink group-hover:text-gold-strong">{roadmap.title}</h2>
              {roadmap.description ? <p className="mt-2 line-clamp-3 text-sm leading-6 text-muted">{roadmap.description}</p> : null}
              <span className="mt-auto inline-flex items-center gap-2 pt-6 text-sm font-medium text-gold">
                Open roadmap <ArrowRight aria-hidden="true" size={16} />
              </span>
            </Link>
          ))}
        </section>
      )}
    </div>
  );
}
