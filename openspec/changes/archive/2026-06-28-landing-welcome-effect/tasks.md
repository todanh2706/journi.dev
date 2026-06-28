## 1. Setup CSS Animations

- [x] 1.1 Add `@keyframes` for typing effect (e.g., `typewriter`, `blink-caret`) to `src/frontend/src/index.css`
- [x] 1.2 Add `@keyframes` for pulse/glow effect (e.g., `node-pulse`, `fade-in`) to `src/frontend/src/index.css`
- [x] 1.3 Add utility classes matching the keyframes for easy application in components

## 2. Components Implementation

- [x] 2.1 Create `TerminalEffect` component (`src/frontend/src/features/landing/components/TerminalEffect.tsx`)
- [x] 2.2 Create `ConstellationEffect` component (`src/frontend/src/features/landing/components/ConstellationEffect.tsx`)
- [x] 2.3 Create a container or use state hooks to sequence the transition between the two effects

## 3. Landing Page Assembly

- [x] 3.1 Create `HomePage` component (`src/frontend/src/pages/Home/HomePage.tsx`) integrating the animated components
- [x] 3.2 Add "Log In" and "Sign Up" CTA buttons using existing `.primary-button` / `.secondary-button` classes
- [x] 3.3 Link CTAs to `/login` and `/signup` routes using React Router

## 4. Routing and Verification

- [x] 4.1 Update the main router config (e.g., in `App.tsx` or router definition) to point the `/` route to `HomePage` for unauthenticated users
- [x] 4.2 Run frontend build and verify the typing and galaxy animations display correctly without layout shifts
- [x] 4.3 Verify clicking login/signup navigates to correct routes
