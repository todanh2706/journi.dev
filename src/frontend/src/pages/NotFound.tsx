import { ArrowLeft, Home } from "lucide-react";
import { useNavigate } from "react-router-dom";

import { Logo } from "../components/Logo/Logo";

export default function NotFound() {
  const navigate = useNavigate();
  const hasHistory = window.history.length > 2;

  return (
    <div className="flex min-h-screen flex-col bg-canvas text-ink">
      <header className="mx-auto w-full max-w-7xl px-5 py-6 sm:px-8 lg:px-10"><Logo /></header>
      <main className="mx-auto flex w-full max-w-3xl flex-1 items-center px-5 py-12 text-center sm:px-8">
        <section className="w-full">
          <p className="text-sm font-semibold uppercase tracking-[0.22em] text-gold">Error 404</p>
          <h1 className="mt-5 text-5xl font-semibold tracking-[-0.055em] text-ink sm:text-7xl">This path ends here.</h1>
          <p className="mx-auto mt-5 max-w-xl text-base leading-7 text-muted">The page may have moved, or the address may be incomplete. Return to the last useful step and keep going.</p>
          <button type="button" onClick={() => hasHistory ? navigate(-1) : navigate("/")} className="primary-button mt-8">
            {hasHistory ? <ArrowLeft aria-hidden="true" size={17} /> : <Home aria-hidden="true" size={17} />}
            {hasHistory ? "Go back" : "Go to homepage"}
          </button>
        </section>
      </main>
    </div>
  );
}
