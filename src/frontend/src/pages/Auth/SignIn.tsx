import { useEffect, useRef, useState, type FormEvent } from "react";
import { AxiosError } from "axios";
import { Eye, EyeOff, LockKeyhole, UserRound } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";

import { AuthShell } from "../../features/auth/components/AuthShell";
import { login } from "../../features/auth";

export default function SignIn() {
  const navigate = useNavigate();
  const errorRef = useRef<HTMLDivElement>(null);
  const [showPassword, setShowPassword] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (error) errorRef.current?.focus();
  }, [error]);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (isSubmitting) return;

    setError("");
    setIsSubmitting(true);

    try {
      const response = await login({ username, password });
      localStorage.setItem("access_token", response.token);
      navigate("/dashboard", { replace: true });
    } catch (requestError) {
      setError(
        requestError instanceof AxiosError && requestError.response?.status === 401
          ? "That username and password combination was not recognized."
          : "Sign in is unavailable right now. Check your connection and try again.",
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AuthShell
      eyebrow="Welcome back"
      title="Continue your roadmap"
      description="Sign in with the username you created for Journi.dev."
      footer={<>New to Journi.dev? <Link to="/signup" className="font-medium text-gold hover:text-gold-strong">Create an account</Link></>}
    >
      {error ? (
        <div ref={errorRef} tabIndex={-1} role="alert" className="mb-5 rounded-xl border border-danger/30 bg-danger/10 px-4 py-3 text-sm leading-5 text-red-200">
          {error}
        </div>
      ) : null}

      <form onSubmit={handleSubmit} className="space-y-5">
        <label className="block space-y-2" htmlFor="signin-username">
          <span className="text-sm font-medium text-ink">Username</span>
          <span className="relative block">
            <UserRound aria-hidden="true" size={18} className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 text-subtle" />
            <input
              id="signin-username"
              name="username"
              autoComplete="username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              placeholder="Your username"
              className="app-input pl-11"
              required
            />
          </span>
        </label>

        <label className="block space-y-2" htmlFor="signin-password">
          <span className="text-sm font-medium text-ink">Password</span>
          <span className="relative block">
            <LockKeyhole aria-hidden="true" size={18} className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 text-subtle" />
            <input
              id="signin-password"
              name="password"
              type={showPassword ? "text" : "password"}
              autoComplete="current-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Your password"
              className="app-input px-11"
              required
            />
            <button
              type="button"
              onClick={() => setShowPassword((visible) => !visible)}
              className="absolute right-2.5 top-1/2 flex h-9 w-9 -translate-y-1/2 items-center justify-center rounded-lg text-subtle transition-colors hover:bg-surface-elevated hover:text-ink"
              aria-label={showPassword ? "Hide password" : "Show password"}
            >
              {showPassword ? <EyeOff aria-hidden="true" size={18} /> : <Eye aria-hidden="true" size={18} />}
            </button>
          </span>
        </label>

        <button id="btn-signin-submit" type="submit" disabled={isSubmitting || !username || !password} className="primary-button w-full">
          {isSubmitting ? "Signing in…" : "Sign in"}
        </button>
      </form>
    </AuthShell>
  );
}
