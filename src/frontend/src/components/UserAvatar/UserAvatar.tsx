interface UserAvatarProps {
  username: string;
  className?: string;
}

export function UserAvatar({ username, className = "h-10 w-10" }: UserAvatarProps) {
  const initial = username.trim().charAt(0).toUpperCase() || "J";

  return (
    <span
      aria-hidden="true"
      className={`inline-flex shrink-0 items-center justify-center rounded-xl border border-line-strong bg-surface-elevated font-semibold text-gold ${className}`}
    >
      {initial}
    </span>
  );
}
