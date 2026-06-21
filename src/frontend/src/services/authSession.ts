type AccessTokenListener = (token: string | null) => void;

const AUTH_CHANNEL_NAME = "journi-auth";
const listeners = new Set<AccessTokenListener>();
let accessToken: string | null = null;

const authChannel = typeof BroadcastChannel === "undefined"
  ? null
  : new BroadcastChannel(AUTH_CHANNEL_NAME);

authChannel?.addEventListener("message", (event: MessageEvent<unknown>) => {
  if (isSessionEndedMessage(event.data)) {
    setAccessTokenLocally(null);
  }
});

export function getAccessToken(): string | null {
  return accessToken;
}

export function setAccessToken(token: string): void {
  setAccessTokenLocally(token);
}

export function endSession(options: { broadcast?: boolean } = {}): void {
  setAccessTokenLocally(null);
  if (options.broadcast) {
    authChannel?.postMessage({ type: "session-ended" });
  }
}

export function subscribeAccessToken(listener: AccessTokenListener): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

function setAccessTokenLocally(token: string | null): void {
  accessToken = token;
  listeners.forEach((listener) => listener(token));
}

function isSessionEndedMessage(value: unknown): value is { type: "session-ended" } {
  return typeof value === "object"
    && value !== null
    && "type" in value
    && value.type === "session-ended";
}
