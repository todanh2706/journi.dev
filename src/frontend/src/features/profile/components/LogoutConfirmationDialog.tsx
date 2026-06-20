import { useEffect, useRef } from "react";
import { AlertTriangle, LoaderCircle, LogOut, X } from "lucide-react";

interface LogoutConfirmationDialogProps {
  open: boolean;
  pending: boolean;
  error: string | null;
  onCancel: () => void;
  onConfirm: () => void;
}

export function LogoutConfirmationDialog({
  open,
  pending,
  error,
  onCancel,
  onConfirm,
}: LogoutConfirmationDialogProps) {
  const dialogRef = useRef<HTMLDivElement>(null);
  const cancelButtonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    if (!open) return;

    const previouslyFocused = document.activeElement as HTMLElement | null;
    if (pending) {
      dialogRef.current?.focus();
    } else {
      cancelButtonRef.current?.focus();
    }

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape" && !pending) {
        onCancel();
        return;
      }

      if (event.key !== "Tab" || !dialogRef.current) return;

      const focusableElements = Array.from(
        dialogRef.current.querySelectorAll<HTMLElement>(
          'button:not([disabled]), [href], input:not([disabled]), [tabindex]:not([tabindex="-1"])',
        ),
      );
      if (focusableElements.length === 0) {
        event.preventDefault();
        dialogRef.current.focus();
        return;
      }

      const firstElement = focusableElements[0];
      const lastElement = focusableElements[focusableElements.length - 1];

      if (event.shiftKey && document.activeElement === firstElement) {
        event.preventDefault();
        lastElement.focus();
      } else if (!event.shiftKey && document.activeElement === lastElement) {
        event.preventDefault();
        firstElement.focus();
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => {
      document.removeEventListener("keydown", handleKeyDown);
      previouslyFocused?.focus();
    };
  }, [onCancel, open, pending]);

  if (!open) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-[#080912]/80 p-4 backdrop-blur-sm"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !pending) onCancel();
      }}
    >
      <div
        ref={dialogRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="logout-dialog-title"
        aria-describedby="logout-dialog-description"
        tabIndex={-1}
        className="w-full max-w-md rounded-2xl border border-white/[0.08] bg-[#151622] p-5 shadow-[0_28px_90px_rgba(0,0,0,0.55)] sm:p-6"
      >
        <div className="flex items-start justify-between gap-4">
          <div className="flex h-11 w-11 items-center justify-center rounded-xl border border-red-400/15 bg-red-400/[0.08] text-red-300">
            <AlertTriangle aria-hidden="true" size={21} strokeWidth={1.8} />
          </div>
          <button
            type="button"
            onClick={onCancel}
            disabled={pending}
            aria-label="Close logout confirmation"
            className="flex h-9 w-9 cursor-pointer items-center justify-center rounded-lg text-gray-500 transition-colors hover:bg-white/[0.05] hover:text-gray-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-400/50 disabled:cursor-not-allowed disabled:opacity-40"
          >
            <X aria-hidden="true" size={18} strokeWidth={1.8} />
          </button>
        </div>

        <h2 id="logout-dialog-title" className="mt-5 text-xl font-semibold tracking-[-0.02em] text-gray-100">
          Log out of Journi.dev?
        </h2>
        <p id="logout-dialog-description" className="mt-2 text-sm leading-6 text-gray-500">
          This ends your current browser session and removes its locally stored access token. You will need to sign in again.
        </p>

        {error && (
          <div role="alert" className="mt-4 rounded-xl border border-red-400/15 bg-red-400/[0.07] px-3.5 py-3 text-sm leading-5 text-red-200">
            {error}
          </div>
        )}

        <div className="mt-6 flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
          <button
            ref={cancelButtonRef}
            type="button"
            onClick={onCancel}
            disabled={pending}
            className="cursor-pointer rounded-xl border border-white/[0.08] bg-white/[0.03] px-4 py-2.5 text-sm font-medium text-gray-300 transition-colors hover:bg-white/[0.06] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-400/50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={onConfirm}
            disabled={pending}
            className="inline-flex min-w-32 cursor-pointer items-center justify-center gap-2 rounded-xl bg-red-500 px-4 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-red-400 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-red-300/60 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {pending ? (
              <>
                <LoaderCircle aria-hidden="true" size={17} className="animate-spin motion-reduce:animate-none" />
                Logging out
              </>
            ) : (
              <>
                <LogOut aria-hidden="true" size={17} strokeWidth={1.8} />
                Log out
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
