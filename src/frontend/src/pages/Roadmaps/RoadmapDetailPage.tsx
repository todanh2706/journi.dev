import { useCallback, useEffect, useState } from "react";
import { AlertCircle, ArrowLeft, LoaderCircle, Map } from "lucide-react";
import { Link, useParams } from "react-router-dom";

import { useAuth } from "../../features/auth";
import { RoadmapCanvas, roadmapService, type RoadmapWithNodes } from "../../features/roadmaps";

export default function RoadmapDetailPage() {
  const { user, isLoading: authLoading } = useAuth();
  const { roadmapId } = useParams<{ roadmapId: string }>();
  const [data, setData] = useState<RoadmapWithNodes | null>(null);
  const [loading, setLoading] = useState(Boolean(roadmapId));
  const [error, setError] = useState<string | null>(null);

  const loadRoadmap = useCallback(async () => {
    if (!roadmapId) return;

    try {
      setData(await roadmapService.getRoadmapWithNodes(roadmapId));
    } catch {
      setError("This learning path could not be loaded. Check your connection and try again.");
    } finally {
      setLoading(false);
    }
  }, [roadmapId]);

  useEffect(() => {
    if (authLoading || !user || !roadmapId) return;
    let active = true;

    roadmapService.getRoadmapWithNodes(roadmapId)
      .then((roadmap) => {
        if (active) setData(roadmap);
      })
      .catch(() => {
        if (active) setError("This learning path could not be loaded. Check your connection and try again.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [authLoading, roadmapId, user]);

  const retryRoadmap = () => {
    setLoading(true);
    setError(null);
    void loadRoadmap();
  };

  const displayError = roadmapId ? error : "This roadmap link is missing its identifier.";

  return (
    <div className="w-full px-4 py-7 sm:px-6 sm:py-9 lg:px-10 lg:py-10">
      <header className="mx-auto mb-7 w-full max-w-[1500px]">
        <Link to="/dashboard/roadmaps" className="inline-flex items-center gap-2 rounded-lg text-sm font-medium text-muted transition-colors hover:text-gold-strong">
          <ArrowLeft aria-hidden="true" size={16} />
          Back to roadmaps
        </Link>

        {authLoading ? (
          <section className="app-panel flex min-h-[560px] items-center justify-center p-8" aria-live="polite">
            <div className="text-center text-muted">
              <LoaderCircle aria-hidden="true" size={27} className="mx-auto animate-spin text-gold motion-reduce:animate-none" />
              <p className="mt-3 text-sm">Restoring your session…</p>
            </div>
          </section>
        ) : !user ? (
          <section className="app-panel p-7 text-center sm:p-10">
            <h2 className="text-xl font-semibold text-ink">Sign in to open this roadmap</h2>
            <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-muted">Your progress and unlocked nodes are linked to your account.</p>
            <Link to="/signin" className="primary-button mt-5">Sign in</Link>
          </section>
        ) : loading ? (
          <div className="mt-6 space-y-3" aria-hidden="true">
            <div className="h-8 w-72 max-w-full animate-pulse rounded-lg bg-surface-elevated motion-reduce:animate-none" />
            <div className="h-4 w-[34rem] max-w-full animate-pulse rounded bg-surface motion-reduce:animate-none" />
          </div>
        ) : data ? (
          <div className="mt-6 max-w-4xl">
            <p className="eyebrow">Learning roadmap</p>
            <h1 className="mt-3 text-3xl font-semibold tracking-[-0.045em] text-ink sm:text-4xl">{data.title}</h1>
            {data.description ? <p className="mt-3 text-sm leading-6 text-muted sm:text-base">{data.description}</p> : null}
          </div>
        ) : null}
      </header>

      <div className="mx-auto w-full max-w-[1500px]">
        {loading ? (
          <section className="app-panel flex min-h-[560px] items-center justify-center p-8" aria-live="polite">
            <div className="text-center text-muted">
              <LoaderCircle aria-hidden="true" size={27} className="mx-auto animate-spin text-gold motion-reduce:animate-none" />
              <p className="mt-3 text-sm">Building roadmap workspace…</p>
            </div>
          </section>
        ) : displayError ? (
          <section role="alert" className="rounded-2xl border border-danger/30 bg-danger/10 p-6 sm:p-8">
            <AlertCircle aria-hidden="true" size={24} className="text-danger" />
            <h2 className="mt-4 text-xl font-semibold text-ink">Unable to load this roadmap</h2>
            <p className="mt-2 max-w-lg text-sm leading-6 text-muted">{displayError}</p>
            {roadmapId ? <button type="button" onClick={retryRoadmap} className="secondary-button mt-5">Try again</button> : null}
          </section>
        ) : !data || data.nodes.length === 0 ? (
          <section className="app-panel p-7 text-center sm:p-10">
            <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-xl border border-line-strong bg-surface-elevated text-gold">
              <Map aria-hidden="true" size={22} />
            </div>
            <h2 className="mt-5 text-xl font-semibold text-ink">No skill nodes yet</h2>
            <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-muted">This roadmap exists, but its learning sequence has not been published.</p>
          </section>
        ) : (
          <RoadmapCanvas roadmap={data} />
        )}
      </div>
    </div>
  );
}
