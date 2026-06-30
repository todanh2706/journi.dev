import assert from "node:assert/strict";
import test from "node:test";

import { decodeAccessToken } from "../src/features/auth/utils/decodeAccessToken.ts";
import { AUTH_ENDPOINT_PATHS, normalizeApiPath } from "../src/services/authEndpoints.ts";

const originalWindow = globalThis.window;

function restoreWindow() {
  if (originalWindow === undefined) {
    delete globalThis.window;
    return;
  }
  globalThis.window = originalWindow;
}

function installWindowStub() {
  globalThis.window = {
    atob: (value) => Buffer.from(value, "base64").toString("binary"),
    location: { origin: "http://journi.local" },
  };
}

function createToken(payload) {
  const header = Buffer.from(JSON.stringify({ alg: "none", typ: "JWT" })).toString("base64url");
  const body = Buffer.from(JSON.stringify(payload)).toString("base64url");
  return `${header}.${body}.signature`;
}

test("keeps the auth endpoint allowlist aligned with refresh-safe routes", () => {
  assert.deepEqual([...AUTH_ENDPOINT_PATHS].sort(), [
    "/auth/csrf",
    "/auth/login",
    "/auth/logout",
    "/auth/refresh",
    "/auth/signup",
  ]);
});

test("normalizes absolute and relative API paths before auth retry checks", () => {
  assert.equal(
    normalizeApiPath("http://localhost:8000/api/v1/auth/refresh", "http://localhost:8000/api/v1"),
    "/auth/refresh",
  );
  assert.equal(
    normalizeApiPath("/api/v1/roadmaps/123/nodes", "http://localhost:8000/api/v1"),
    "/roadmaps/123/nodes",
  );
  assert.equal(normalizeApiPath("roadmaps", "http://localhost:8000/api/v1"), "/roadmaps");
  assert.equal(normalizeApiPath(undefined, "http://localhost:8000/api/v1"), "/");
});

test("decodes a non-expired access token into the current auth user", () => {
  installWindowStub();
  try {
    const iat = Math.floor(Date.now() / 1000) - 60;
    const exp = iat + 3600;
    const token = createToken({ sub: "journi-user", iat, exp });

    assert.deepEqual(decodeAccessToken(token), {
      username: "journi-user",
      iat,
      exp,
    });
  } finally {
    restoreWindow();
  }
});

test("rejects expired, malformed, and missing access tokens", () => {
  installWindowStub();
  try {
    const expiredToken = createToken({
      sub: "journi-user",
      iat: Math.floor(Date.now() / 1000) - 7200,
      exp: Math.floor(Date.now() / 1000) - 3600,
    });

    assert.equal(decodeAccessToken(expiredToken), null);
    assert.equal(decodeAccessToken("not-a-jwt"), null);
    assert.equal(decodeAccessToken(null), null);
  } finally {
    restoreWindow();
  }
});
