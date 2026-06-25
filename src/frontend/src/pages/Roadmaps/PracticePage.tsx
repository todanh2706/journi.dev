import axios from "axios";
import { useCallback, useEffect, useRef, useState, type FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import { AlertCircle, ArrowLeft, Check, CheckCircle2, Clock3, Code2, ExternalLink, FileCode2, GitBranch, GitFork, Lightbulb, ListChecks, LoaderCircle, RefreshCw, RotateCcw, Send, ShieldCheck, XCircle } from "lucide-react";

import { canRetryEvaluation, canSubmitRevision, focusPracticeHeading, isActiveSubmission, pollingDelay, practiceService, roadmapRoute, shouldRefreshRoadmap, SUBMISSION_STATUS_TITLES, validateSubmission, type CreateSubmissionInput, type PracticeChallenge, type PracticeSubmission } from "../../features/practice";
import { roadmapService, type LearningResource } from "../../features/roadmaps";

const initialForm: CreateSubmissionInput = { repositoryUrl: "", branch: "main", commitSha: "" };

export default function PracticePage() {
  const { roadmapId, nodeId } = useParams();
  const headingRef = useRef<HTMLHeadingElement>(null);
  const pollingAttemptRef = useRef(0);
  const refreshedSubmissionRef = useRef<string | null>(null);
  const [challenge, setChallenge] = useState<PracticeChallenge | null>(null);
  const [resources, setResources] = useState<LearningResource[]>([]);
  const [history, setHistory] = useState<PracticeSubmission[]>([]);
  const [currentSubmission, setCurrentSubmission] = useState<PracticeSubmission | null>(null);
  const [form, setForm] = useState<CreateSubmissionInput>(initialForm);
  const [fieldErrors, setFieldErrors] = useState<ReturnType<typeof validateSubmission>>({});
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<{ kind: "locked" | "unsupported" | "missing" | "network"; message: string } | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [pollError, setPollError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [retrying, setRetrying] = useState(false);
  const [pollNonce, setPollNonce] = useState(0);
  const [roadmapRefreshed, setRoadmapRefreshed] = useState(false);

  const backPath = roadmapRoute(roadmapId);

  const loadWorkspace = useCallback(async () => {
    if (!roadmapId || !nodeId) {
      setLoadError({ kind: "missing", message: "This practice route is incomplete." });
      setLoading(false);
      return;
    }
    setLoading(true);
    setLoadError(null);
    try {
      const [loadedChallenge, roadmap] = await Promise.all([
        practiceService.getChallenge(nodeId),
        roadmapService.getRoadmapWithNodes(roadmapId),
      ]);
      const loadedHistory = await practiceService.getHistory(loadedChallenge.challengeId);
      setChallenge(loadedChallenge);
      setResources(roadmap.nodes.find((node) => node.nodeId === nodeId)?.learningResources ?? []);
      setHistory(loadedHistory);
      setCurrentSubmission(loadedHistory[0] ?? null);
      setForm((value) => value.repositoryUrl ? value : { ...value, repositoryUrl: loadedChallenge.starterRepositoryUrl });
      requestAnimationFrame(() => focusPracticeHeading(headingRef.current));
    } catch (error) {
      const status = axios.isAxiosError(error) ? error.response?.status : undefined;
      const message = axios.isAxiosError<{ message?: string }>(error) ? error.response?.data?.message : undefined;
      if (status === 403) setLoadError({ kind: "locked", message: message ?? "Complete the prerequisite nodes before opening this practice." });
      else if (status === 400) setLoadError({ kind: "unsupported", message: message ?? "This node does not support GitHub practice." });
      else if (status === 404) setLoadError({ kind: "missing", message: message ?? "No required challenge is available for this node." });
      else setLoadError({ kind: "network", message: "The practice workspace could not be loaded. Check your connection and try again." });
    } finally {
      setLoading(false);
    }
  }, [nodeId, roadmapId]);

  useEffect(() => {
    const timer = window.setTimeout(() => void loadWorkspace(), 0);
    return () => window.clearTimeout(timer);
  }, [loadWorkspace]);

  const activeSubmissionId = currentSubmission && isActiveSubmission(currentSubmission.status)
    ? currentSubmission.submissionId
    : null;
  useEffect(() => {
    if (!activeSubmissionId) return;
    let cancelled = false;
    let timer: number | undefined;
    let requestInFlight = false;

    const schedule = () => {
      timer = window.setTimeout(() => void poll(), document.hidden ? 1000 : pollingDelay(pollingAttemptRef.current));
    };
    const poll = async () => {
      if (cancelled) return;
      if (document.hidden || requestInFlight) {
        schedule();
        return;
      }
      requestInFlight = true;
      try {
        const updated = await practiceService.getSubmission(activeSubmissionId);
        if (cancelled) return;
        setCurrentSubmission(updated);
        setHistory((items) => items.map((item) => item.submissionId === updated.submissionId ? updated : item));
        setPollError(null);
        pollingAttemptRef.current += 1;
      } catch {
        if (!cancelled) setPollError("Status updates are paused because the network request failed.");
        return;
      } finally {
        requestInFlight = false;
      }
      if (!cancelled) schedule();
    };

    schedule();
    return () => {
      cancelled = true;
      if (timer) window.clearTimeout(timer);
    };
  }, [activeSubmissionId, pollNonce]);

  useEffect(() => {
    if (!currentSubmission || !roadmapId || !shouldRefreshRoadmap(currentSubmission.status, refreshedSubmissionRef.current, currentSubmission.submissionId)) return;
    refreshedSubmissionRef.current = currentSubmission.submissionId;
    roadmapService.getRoadmapWithNodes(roadmapId)
      .then(() => setRoadmapRefreshed(true))
      .catch(() => setRoadmapRefreshed(false));
  }, [currentSubmission, roadmapId]);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!challenge || !canSubmitRevision(submitting, currentSubmission?.status) || !challenge.submissionEnabled) return;
    const errors = validateSubmission(form);
    setFieldErrors(errors);
    if (Object.keys(errors).length > 0) return;

    setSubmitting(true);
    setSubmitError(null);
    try {
      const submitted = await practiceService.createSubmission(challenge.challengeId, {
        repositoryUrl: form.repositoryUrl.trim(),
        branch: form.branch.trim(),
        commitSha: form.commitSha.trim().toLowerCase(),
      });
      pollingAttemptRef.current = 0;
      setCurrentSubmission(submitted);
      setHistory((items) => [submitted, ...items.filter((item) => item.submissionId !== submitted.submissionId)]);
    } catch (error) {
      const message = axios.isAxiosError<{ message?: string }>(error) ? error.response?.data?.message : undefined;
      setSubmitError(message ?? "This revision could not be submitted. Verify that the repository and commit are public, then try again.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleInfrastructureRetry = async () => {
    if (!currentSubmission || !canRetryEvaluation(currentSubmission.status, currentSubmission.retryable) || retrying) return;
    setRetrying(true);
    setSubmitError(null);
    try {
      const retried = await practiceService.retrySubmission(currentSubmission.submissionId);
      pollingAttemptRef.current = 0;
      setCurrentSubmission(retried);
      setHistory((items) => items.map((item) => item.submissionId === retried.submissionId ? retried : item));
    } catch (error) {
      const message = axios.isAxiosError<{ message?: string }>(error) ? error.response?.data?.message : undefined;
      setSubmitError(message ?? "The infrastructure retry could not be started.");
    } finally {
      setRetrying(false);
    }
  };

  if (loading) return <WorkspaceState icon={<LoaderCircle className="animate-spin motion-reduce:animate-none" />} title="Loading practice workspace" message="Checking challenge access and your submission history." backPath={backPath} />;
  if (loadError) return <WorkspaceState icon={<AlertCircle />} title={loadError.kind === "locked" ? "Practice is locked" : "Practice is unavailable"} message={loadError.message} backPath={backPath} retry={loadError.kind === "network" ? loadWorkspace : undefined} />;
  if (!challenge) return <WorkspaceState icon={<AlertCircle />} title="Practice is unavailable" message="The challenge response was empty." backPath={backPath} />;

  return (
    <div className="mx-auto w-full max-w-6xl px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
      <Link to={backPath} className="inline-flex min-h-11 items-center gap-2 rounded-lg text-sm font-medium text-muted transition-colors hover:text-gold-strong">
        <ArrowLeft aria-hidden="true" size={17} /> Back to roadmap
      </Link>

      <header className="mt-5 border-b border-line pb-6">
        <div className="flex flex-col justify-between gap-5 md:flex-row md:items-end">
          <div className="max-w-3xl">
            <p className="eyebrow">GitHub practice · {challenge.difficulty}</p>
            <h1 ref={headingRef} tabIndex={-1} className="mt-3 text-3xl font-semibold tracking-tight text-ink sm:text-4xl">{challenge.title}</h1>
            <p className="mt-3 max-w-2xl text-sm leading-6 text-muted sm:text-base">{challenge.description}</p>
          </div>
          <div className="grid grid-cols-2 gap-2 sm:flex">
            <Metric label="Passing score" value={`${challenge.passingScore}/${challenge.maxScore}`} />
            <Metric label="Time limit" value={`${Math.ceil(challenge.timeoutSeconds / 60)} min`} />
          </div>
        </div>
      </header>

      <div className="mt-6 grid gap-6 lg:grid-cols-[minmax(0,1fr)_23rem]">
        <div className="space-y-5">
          <Section icon={<Code2 size={17} />} title="Brief">
            <p className="whitespace-pre-wrap text-sm leading-7 text-muted">{challenge.instructions}</p>
            <a href={challenge.starterRepositoryUrl} target="_blank" rel="noopener noreferrer" className="secondary-button mt-4">
              <GitFork aria-hidden="true" size={17} /> Open starter repository <ExternalLink aria-hidden="true" size={14} />
            </a>
          </Section>

          <Section icon={<ListChecks size={17} />} title="Acceptance criteria">
            <ul className="space-y-2.5">
              {challenge.acceptanceCriteria.map((criterion) => <li key={criterion} className="flex gap-3 rounded-xl border border-line bg-canvas/45 p-3.5 text-sm leading-6 text-muted"><span className="mt-0.5 inline-flex h-5 w-5 shrink-0 items-center justify-center rounded-md border border-gold/30 bg-gold/10 text-gold"><Check size={12} /></span>{criterion}</li>)}
            </ul>
          </Section>

          <Section icon={<FileCode2 size={17} />} title="Expected artifacts">
            <ul className="grid gap-2 sm:grid-cols-2">{challenge.expectedArtifacts.map((artifact) => <li key={artifact} className="rounded-lg border border-line bg-canvas px-3 py-2.5 font-mono text-xs text-gold-strong">{artifact}</li>)}</ul>
          </Section>

          <Section icon={<Lightbulb size={17} />} title="Hints and resources">
            <ul className="space-y-2 text-sm leading-6 text-muted">{challenge.hints.map((hint) => <li key={hint} className="flex gap-2"><span aria-hidden="true" className="text-gold">•</span>{hint}</li>)}</ul>
            {resources.length > 0 ? <div className="mt-4 grid gap-2 sm:grid-cols-2">{resources.map((resource) => <a key={resource.sourceUrl} href={resource.sourceUrl} target="_blank" rel="noopener noreferrer" className="rounded-xl border border-line bg-canvas/45 p-3 transition-colors hover:border-line-strong"><span className="flex items-start justify-between gap-3 text-sm font-medium text-ink">{resource.title}<ExternalLink size={14} className="shrink-0 text-gold" /></span><span className="mt-1 block text-xs text-subtle">{resource.sourceType}</span></a>)}</div> : null}
          </Section>
        </div>

        <aside className="space-y-5 lg:sticky lg:top-6 lg:self-start">
          <SubmissionStatusCard submission={currentSubmission} submissionEnabled={challenge.submissionEnabled} pollError={pollError} roadmapRefreshed={roadmapRefreshed} onPollRetry={() => { setPollError(null); setPollNonce((value) => value + 1); }} onInfrastructureRetry={() => void handleInfrastructureRetry()} retrying={retrying} />

          {challenge.progressStatus !== "COMPLETED" ? (
            <Section icon={<Send size={17} />} title="Submit revision">
              {!challenge.submissionEnabled ? <div className="rounded-xl border border-line bg-canvas/45 p-4 text-sm leading-6 text-muted"><ShieldCheck size={18} className="mb-2 text-gold" />Automated evaluation for this challenge is not enabled yet. You can review the brief while the grader contract is validated.</div> : (
                <form onSubmit={(event) => void handleSubmit(event)} noValidate className="space-y-4">
                  <FormField id="repository-url" label="Public GitHub repository" value={form.repositoryUrl} error={fieldErrors.repositoryUrl} placeholder="https://github.com/you/catalog" onChange={(repositoryUrl) => setForm((value) => ({ ...value, repositoryUrl }))} />
                  <FormField id="branch" label="Branch" value={form.branch} error={fieldErrors.branch} placeholder="main" icon={<GitBranch size={15} />} onChange={(branch) => setForm((value) => ({ ...value, branch }))} />
                  <FormField id="commit-sha" label="Full commit SHA" value={form.commitSha} error={fieldErrors.commitSha} placeholder="40 hexadecimal characters" monospace onChange={(commitSha) => setForm((value) => ({ ...value, commitSha }))} />
                  {submitError ? <p role="alert" className="rounded-lg border border-danger/30 bg-danger/10 p-3 text-xs leading-5 text-red-100">{submitError}</p> : null}
                  <button type="submit" disabled={!canSubmitRevision(submitting, currentSubmission?.status)} className="primary-button w-full">
                    {submitting ? <LoaderCircle size={16} className="animate-spin motion-reduce:animate-none" /> : <Send size={16} />}
                    {submitting ? "Verifying revision" : "Submit for evaluation"}
                  </button>
                </form>
              )}
            </Section>
          ) : null}

          <Section icon={<Clock3 size={17} />} title="Attempt history">
            {history.length === 0 ? <p className="text-sm leading-6 text-muted">No revisions submitted yet.</p> : <ol className="space-y-2">{history.map((attempt) => <li key={attempt.submissionId} className="rounded-xl border border-line bg-canvas/45 p-3"><div className="flex items-center justify-between gap-3"><span className="text-sm font-medium text-ink">Attempt {attempt.attemptNumber}</span><StatusLabel status={attempt.status} /></div><code className="mt-2 block truncate text-xs text-subtle">{attempt.commitSha.slice(0, 12)}</code></li>)}</ol>}
          </Section>
        </aside>
      </div>
    </div>
  );
}

function Section({ icon, title, children }: { icon: React.ReactNode; title: string; children: React.ReactNode }) {
  return <section className="app-panel p-4 sm:p-5"><h2 className="flex items-center gap-2 text-sm font-semibold text-ink"><span className="text-gold">{icon}</span>{title}</h2><div className="mt-4">{children}</div></section>;
}

function Metric({ label, value }: { label: string; value: string }) {
  return <div className="min-w-28 rounded-xl border border-line bg-surface px-3.5 py-3"><p className="text-[10px] font-semibold uppercase tracking-[0.12em] text-subtle">{label}</p><p className="mt-1 text-sm font-semibold text-ink">{value}</p></div>;
}

function FormField({ id, label, value, error, placeholder, onChange, icon, monospace }: { id: string; label: string; value: string; error?: string; placeholder: string; onChange: (value: string) => void; icon?: React.ReactNode; monospace?: boolean }) {
  return <div><label htmlFor={id} className="mb-2 block text-xs font-semibold text-muted">{label}</label><div className="relative">{icon ? <span className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 text-subtle">{icon}</span> : null}<input id={id} value={value} onChange={(event) => onChange(event.target.value)} placeholder={placeholder} aria-invalid={Boolean(error)} aria-describedby={error ? `${id}-error` : undefined} className={`app-input ${icon ? "pl-10" : ""} ${monospace ? "font-mono text-xs" : ""} ${error ? "border-danger" : ""}`} /></div>{error ? <p id={`${id}-error`} className="mt-1.5 text-xs leading-5 text-red-200">{error}</p> : null}</div>;
}

function SubmissionStatusCard({ submission, submissionEnabled, pollError, roadmapRefreshed, onPollRetry, onInfrastructureRetry, retrying }: { submission: PracticeSubmission | null; submissionEnabled: boolean; pollError: string | null; roadmapRefreshed: boolean; onPollRetry: () => void; onInfrastructureRetry: () => void; retrying: boolean }) {
  if (!submission) return <Section icon={<ShieldCheck size={17} />} title="Evaluation status"><p className="text-sm leading-6 text-muted">{submissionEnabled ? "Submit an immutable commit when your solution is ready." : "You can review the brief now. Submission history will appear after automated evaluation is enabled."}</p></Section>;
  const presentation = {
    SUBMITTED: { icon: <Clock3 />, body: "Your exact commit is waiting for an isolated grader." },
    EVALUATING: { icon: <LoaderCircle className="animate-spin motion-reduce:animate-none" />, body: "Deterministic tests are running with network and resource limits." },
    PASSED: { icon: <CheckCircle2 />, body: "This practice node is complete and dependent nodes have been recalculated." },
    NEEDS_CHANGES: { icon: <XCircle />, body: "Review the feedback, push a corrected commit, and submit its new SHA." },
    FAILED: { icon: <AlertCircle />, body: "Your solution was not marked wrong. Retry the same attempt when the grader is available." },
  }[submission.status];
  return <Section icon={presentation.icon} title={SUBMISSION_STATUS_TITLES[submission.status]}><div aria-live="polite"><StatusLabel status={submission.status} />{submission.score !== null ? <p className="mt-3 text-2xl font-semibold text-ink">{submission.score}<span className="text-sm font-medium text-subtle"> points</span></p> : null}<p className="mt-3 text-sm leading-6 text-muted">{submission.resultSummary ?? presentation.body}</p>{submission.feedback.length > 0 ? <ul className="mt-4 space-y-2">{submission.feedback.map((item) => <li key={`${item.criterion}-${item.message}`} className="rounded-lg border border-line bg-canvas/45 p-3 text-xs leading-5"><p className="font-semibold text-ink">{item.passed ? "Passed" : "Review"}: {item.criterion}</p><p className="mt-1 text-muted">{item.message}</p></li>)}</ul> : null}{roadmapRefreshed && submission.status === "PASSED" ? <p className="mt-3 text-xs text-green-200">Roadmap progress refreshed.</p> : null}{pollError ? <div role="alert" className="mt-3 rounded-lg border border-warning/30 bg-warning/10 p-3 text-xs leading-5 text-ink"><p>{pollError}</p><button type="button" onClick={onPollRetry} className="mt-2 inline-flex items-center gap-1.5 font-semibold text-gold-strong"><RefreshCw size={13} /> Retry status update</button></div> : null}{submission.retryable ? <button type="button" onClick={onInfrastructureRetry} disabled={retrying} className="secondary-button mt-4 w-full">{retrying ? <LoaderCircle size={16} className="animate-spin" /> : <RotateCcw size={16} />}{retrying ? "Starting retry" : "Retry evaluation"}</button> : null}</div></Section>;
}

function StatusLabel({ status }: { status: PracticeSubmission["status"] }) {
  const styles = status === "PASSED" ? "border-success/30 bg-success/10 text-green-200" : status === "FAILED" || status === "NEEDS_CHANGES" ? "border-danger/30 bg-danger/10 text-red-200" : "border-gold/30 bg-gold/10 text-gold-strong";
  return <span className={`inline-flex rounded-lg border px-2.5 py-1 text-[10px] font-semibold uppercase tracking-[0.11em] ${styles}`}>{status.replace("_", " ")}</span>;
}

function WorkspaceState({ icon, title, message, backPath, retry }: { icon: React.ReactNode; title: string; message: string; backPath: string; retry?: () => Promise<void> }) {
  return <div className="mx-auto flex min-h-[70vh] max-w-xl items-center px-4 py-12"><div className="app-panel w-full p-6 text-center"><div className="mx-auto flex h-11 w-11 items-center justify-center rounded-xl border border-line bg-canvas text-gold">{icon}</div><h1 className="mt-4 text-xl font-semibold text-ink">{title}</h1><p className="mt-2 text-sm leading-6 text-muted">{message}</p><div className="mt-5 flex flex-wrap justify-center gap-3"><Link to={backPath} className="secondary-button"><ArrowLeft size={16} /> Back to roadmap</Link>{retry ? <button type="button" onClick={() => void retry()} className="primary-button"><RefreshCw size={16} /> Try again</button> : null}</div></div></div>;
}
