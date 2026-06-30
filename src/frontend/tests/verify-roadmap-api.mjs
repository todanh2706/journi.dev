import assert from "node:assert/strict";

const baseUrl = process.env.JOURNI_API_BASE_URL ?? "http://localhost:8000/api/v1";
const collectionsStarterRepositoryUrl = "https://github.com/todanh2706/journi-practice-collections-and-generics";
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
const rootDetailsBefore = await authenticatedRequest(`/skill-nodes/${rootBefore.nodeId}`);
const nextDetailsBefore = await authenticatedRequest(`/skill-nodes/${nextBefore.nodeId}`);
const progressBefore = await authenticatedRequest("/users/me/progress");

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
assert.equal(rootDetailsBefore.progressStatus, "AVAILABLE");
assert.equal(rootDetailsBefore.isLocked, false);
assert.equal(typeof rootDetailsBefore.summary, "string");
assert.ok(rootDetailsBefore.checklist.length >= 3);
assert.ok(rootDetailsBefore.learningResources.length >= 2);
assert.equal(nextDetailsBefore.progressStatus, "LOCKED");
assert.equal(nextDetailsBefore.isLocked, true);
assert.equal(nextDetailsBefore.contentJson, null);
assert.equal(nextDetailsBefore.summary, null);
assert.deepEqual(nextDetailsBefore.checklist, []);
assert.deepEqual(nextDetailsBefore.learningResources, []);
assert.deepEqual(progressBefore, []);

const completion = await authenticatedRequest(`/users/me/progress/nodes/${rootBefore.nodeId}/complete`, {
  method: "POST",
});
assert.equal(completion.status, "COMPLETED");

const nodesAfter = await authenticatedRequest(`/roadmaps/${roadmap.roadmapId}/nodes`);
const sortedAfter = [...nodesAfter].sort((left, right) => left.orderIndex - right.orderIndex);
const rootAfter = sortedAfter[0];
const nextAfter = sortedAfter[1];
const nextDetailsAfter = await authenticatedRequest(`/skill-nodes/${nextAfter.nodeId}`);
const progressAfter = await authenticatedRequest("/users/me/progress");

assert.equal(rootAfter.progressStatus, "COMPLETED");
assert.equal(rootAfter.isLocked, false);
assert.equal(nextAfter.progressStatus, "AVAILABLE");
assert.equal(nextAfter.isLocked, false);
assert.equal(typeof nextAfter.summary, "string");
assert.ok(nextAfter.checklist.length >= 3);
assert.ok(nextAfter.learningResources.length >= 2);
assert.equal(nextDetailsAfter.progressStatus, "AVAILABLE");
assert.equal(nextDetailsAfter.isLocked, false);
assert.equal(typeof nextDetailsAfter.summary, "string");
assert.ok(nextDetailsAfter.checklist.length >= 3);
assert.ok(nextDetailsAfter.learningResources.length >= 2);
assert.equal(progressAfter.length, 1);
assert.equal(progressAfter[0].nodeId, rootBefore.nodeId);
assert.equal(progressAfter[0].status, "COMPLETED");
assert.ok(progressAfter[0].completedAt);

const secondCompletion = await authenticatedRequest(`/users/me/progress/nodes/${nextAfter.nodeId}/complete`, {
  method: "POST",
});
assert.equal(secondCompletion.status, "COMPLETED");

const nodesAfterSecondCompletion = await authenticatedRequest(`/roadmaps/${roadmap.roadmapId}/nodes`);
const sortedAfterSecondCompletion = [...nodesAfterSecondCompletion].sort((left, right) => left.orderIndex - right.orderIndex);
const thirdAfterSecondCompletion = sortedAfterSecondCompletion[2];
assert.equal(thirdAfterSecondCompletion.slug, "oop-in-java");
assert.equal(thirdAfterSecondCompletion.progressStatus, "AVAILABLE");
assert.equal(thirdAfterSecondCompletion.isLocked, false);

const thirdCompletion = await authenticatedRequest(`/users/me/progress/nodes/${thirdAfterSecondCompletion.nodeId}/complete`, {
  method: "POST",
});
assert.equal(thirdCompletion.status, "COMPLETED");

const nodesAfterThirdCompletion = await authenticatedRequest(`/roadmaps/${roadmap.roadmapId}/nodes`);
const sortedAfterThirdCompletion = [...nodesAfterThirdCompletion].sort((left, right) => left.orderIndex - right.orderIndex);
const firstPracticeNode = sortedAfterThirdCompletion.find((node) => node.slug === "collections-and-generics");
assert.ok(firstPracticeNode, "Collections and Generics practice node must exist");
assert.equal(firstPracticeNode.progressStatus, "AVAILABLE");
assert.equal(firstPracticeNode.isLocked, false);
assert.equal(firstPracticeNode.starterRepositoryUrl, collectionsStarterRepositoryUrl);
assert.equal(firstPracticeNode.starterRepositoryUrl.includes("journi.dev"), false);

const practiceChallenge = await authenticatedRequest(`/skill-nodes/${firstPracticeNode.nodeId}/challenge`);
assert.equal(practiceChallenge.nodeId, firstPracticeNode.nodeId);
assert.equal(practiceChallenge.starterRepositoryUrl, collectionsStarterRepositoryUrl);
assert.equal(practiceChallenge.starterRepositoryUrl.includes("journi.dev"), false);
assert.deepEqual(practiceChallenge.expectedArtifacts, [
  "src/practice/collections-and-generics/LibraryCatalog.java",
  "src/practice/collections-and-generics/Book.java",
  "src/practice/collections-and-generics/README.md",
]);

console.log("Verified roadmap API flow: individual node details stay gated until unlock, lesson completion is persisted, seeded practice unlock exposes the curated starter repository, and the next seeded node unlocks immediately.");
