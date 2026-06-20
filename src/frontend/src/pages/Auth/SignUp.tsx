import { useState, type FormEvent } from "react";
import { Eye, EyeOff, LockKeyhole, Mail, UserRound } from "lucide-react";
import { Link } from "react-router-dom";

import { AuthShell } from "../../features/auth/components/AuthShell";
import { getSignupErrorMessage, signup } from "../../features/auth";

export default function SignUp() {
  const [showPassword, setShowPassword] = useState(false);
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [agreeTerms, setAgreeTerms] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!agreeTerms || !username || !email || !password || isSubmitting) return;

    setIsSubmitting(true);
    setErrorMessage("");
    setSuccessMessage("");

    try {
      const createdUser = await signup({ username, email, password });
      setSuccessMessage(`Account created for ${createdUser.username}. You can sign in now.`);
      setPassword("");
      setAgreeTerms(false);
    } catch (error) {
      setErrorMessage(getSignupErrorMessage(error));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AuthShell
      eyebrow="Start your path"
      title="Create your account"
      description="Set up the credentials you will use to access your learning workspace."
      footer={<>Already have an account? <Link to="/signin" className="font-medium text-gold hover:text-gold-strong">Sign in</Link></>}
    >
      <form onSubmit={handleSubmit} className="space-y-5">
        <AuthField id="signup-username" label="Username" icon={UserRound} value={username} onChange={setUsername} autoComplete="username" placeholder="alexdev" />
        <AuthField id="signup-email" label="Email address" icon={Mail} value={email} onChange={setEmail} autoComplete="email" placeholder="you@example.com" type="email" />

        <label className="block space-y-2" htmlFor="signup-password">
          <span className="text-sm font-medium text-ink">Password</span>
          <span className="relative block">
            <LockKeyhole aria-hidden="true" size={18} className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 text-subtle" />
            <input
              id="signup-password"
              name="password"
              type={showPassword ? "text" : "password"}
              autoComplete="new-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Create a password"
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

        <label className="flex items-start gap-3 text-sm leading-5 text-muted">
          <input
            type="checkbox"
            checked={agreeTerms}
            onChange={(event) => setAgreeTerms(event.target.checked)}
            className="mt-0.5 h-4 w-4 rounded border-line accent-gold"
          />
          <span>I agree to create an account under the current Journi.dev terms.</span>
        </label>

        {errorMessage ? <div role="alert" className="rounded-xl border border-danger/30 bg-danger/10 px-4 py-3 text-sm text-red-200">{errorMessage}</div> : null}
        {successMessage ? (
          <div role="status" className="rounded-xl border border-success/30 bg-success/10 px-4 py-3 text-sm text-green-200">
            <p>{successMessage}</p>
            <Link to="/signin" className="mt-2 inline-block font-medium text-green-100 underline underline-offset-4">Continue to sign in</Link>
          </div>
        ) : null}

        <button id="btn-signup-submit" type="submit" disabled={!agreeTerms || !username || !email || !password || isSubmitting} className="primary-button w-full">
          {isSubmitting ? "Creating account…" : "Create account"}
        </button>
      </form>
    </AuthShell>
  );
}

interface AuthFieldProps {
  id: string;
  label: string;
  icon: typeof UserRound;
  value: string;
  onChange: (value: string) => void;
  autoComplete: string;
  placeholder: string;
  type?: "text" | "email";
}

function AuthField({ id, label, icon: Icon, value, onChange, autoComplete, placeholder, type = "text" }: AuthFieldProps) {
  return (
    <label className="block space-y-2" htmlFor={id}>
      <span className="text-sm font-medium text-ink">{label}</span>
      <span className="relative block">
        <Icon aria-hidden="true" size={18} className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 text-subtle" />
        <input id={id} name={id} type={type} value={value} onChange={(event) => onChange(event.target.value)} autoComplete={autoComplete} placeholder={placeholder} className="app-input pl-11" required />
      </span>
    </label>
  );
}
