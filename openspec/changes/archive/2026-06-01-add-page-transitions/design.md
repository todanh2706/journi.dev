## Context
We are implementing a smooth and modern page transition effect (fade-in) when navigating from the "Home" page to the "Sign In" page in our React 19 + TypeScript + Vite project using React Router 7. The logic must be centralized for reusability.

## Goals / Non-Goals

**Goals:**
- Centralize page transition logic to ensure reusability.
- Provide a fade-in transition (300-500ms duration, ease-in-out easing) on route changes.

**Non-Goals:**
- Completely overhauling the UI or adding complex animations like layout shifts or hero animations. Only a fade-in transition is expected.

## Decisions
- **Transition Approach**: We will create a `PageTransition` wrapper component in `src/frontend/src/utils/transitions/PageTransition.tsx`.
- **Implementation**: The wrapper component will leverage React's `useEffect` and standard CSS transitions (via Tailwind classes or inline styles) to apply a fade-in effect upon component mount. This avoids needing heavy third-party animation libraries if not already present, ensuring a lightweight and performant solution.

## Risks / Trade-offs
- **Risk:** Unmount/mount timing in React Router might cut off exit animations if we try to do full cross-fades.
- **Mitigation:** A simple fade-in on mount is the most robust and performant approach that meets the requirements without requiring complex exit animation handling like `AnimatePresence`.
