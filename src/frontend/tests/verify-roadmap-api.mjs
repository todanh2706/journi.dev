import assert from "node:assert/strict";

const baseUrl = process.env.JOURNI_API_BASE_URL ?? "http://backend:8080/api/v1";
const runId = Date.now();
const credentials = {
  username: `roadmap_verify_${runId}`,
  email: `roadmap_verify_${runId}@example.com`,
  password: "RoadmapVerify123!",
};

const jsonRequest = async (path, options = {}) => {
  const response = await fetch(`${baseUrl}${path}`, options);
  if (!response.ok) {
    throw new Error(`${options.method ?? "GET"} ${path} failed with HTTP ${response.status}`);
  }
  return response.json();
};

const signup = await jsonRequest("/auth/signup", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify(credentials),
});
assert.equal(signup.username, credentials.username);

const csrfResponse = await fetch(`${baseUrl}/auth/csrf`);
assert.equal(csrfResponse.ok, true);
const csrf = await csrfResponse.json();
const csrfCookie = csrfResponse.headers.get("set-cookie")?.split(";", 1)[0];
assert.ok(csrfCookie, "CSRF endpoint must set its validation cookie");

const login = await jsonRequest("/auth/login", {
  method: "POST",
  headers: {
    "Content-Type": "application/json",
    Cookie: csrfCookie,
    [csrf.headerName]: csrf.token,
  },
  body: JSON.stringify({ username: credentials.username, password: credentials.password }),
});
assert.ok(login.token);

const authenticatedRequest = (path, options = {}) => jsonRequest(path, {
  ...options,
  headers: {
    ...options.headers,
    Authorization: `Bearer ${login.token}`,
  },
});

const roadmaps = await authenticatedRequest("/roadmaps");
const roadmap = roadmaps.find((candidate) => candidate.slug === "backend-java-spring-boot-developer");
assert.ok(roadmap, "Seeded Backend Java Spring Boot roadmap must exist");

const nodesBefore = await authenticatedRequest(`/roadmaps/${roadmap.roadmapId}/nodes`);
const sortedBefore = [...nodesBefore].sort((left, right) => left.orderIndex - right.orderIndex);
const rootBefore = sortedBefore[0];
const nextBefore = sortedBefore[1];

assert.equal(rootBefore.progressStatus, "AVAILABLE");
assert.equal(rootBefore.isLocked, false);
assert.equal(typeof rootBefore.summary, "string");
assert.ok(rootBefore.checklist.length >= 3);
assert.ok(rootBefore.learningResources.length >= 2);

assert.equal(nextBefore.progressStatus, "LOCKED");
assert.equal(nextBefore.isLocked, true);
assert.equal(nextBefore.contentJson, null);
assert.equal(nextBefore.summary, null);
assert.deepEqual(nextBefore.checklist, []);
assert.deepEqual(nextBefore.learningResources, []);

const completion = await authenticatedRequest(`/users/me/progress/nodes/${rootBefore.nodeId}/complete`, {
  method: "POST",
});
assert.equal(completion.status, "COMPLETED");

const nodesAfter = await authenticatedRequest(`/roadmaps/${roadmap.roadmapId}/nodes`);
const sortedAfter = [...nodesAfter].sort((left, right) => left.orderIndex - right.orderIndex);
const rootAfter = sortedAfter[0];
const nextAfter = sortedAfter[1];

assert.equal(rootAfter.progressStatus, "COMPLETED");
assert.equal(rootAfter.isLocked, false);
assert.equal(nextAfter.progressStatus, "AVAILABLE");
assert.equal(nextAfter.isLocked, false);
assert.equal(typeof nextAfter.summary, "string");
assert.ok(nextAfter.checklist.length >= 3);
assert.ok(nextAfter.learningResources.length >= 2);

console.log("Verified roadmap API flow: AVAILABLE -> COMPLETED, next node LOCKED -> AVAILABLE, gated details hidden then revealed.");
