## Context

Journi.dev currently lacks a dedicated, high-impact welcome experience for unauthenticated users. To better align with the "Premium Dark Learning OS" vision, we are building a high-tech "Code-to-Galaxy" welcome animation. This animation will start with a terminal typing effect and transition into a glowing, interactive roadmap layout (constellation effect), ending with a CTA to login or signup.

## Goals / Non-Goals

**Goals:**
- Create an engaging, animated landing experience at `/` for unauthenticated users.
- Use only CSS, TailwindCSS v4, and React for animations to comply with `DESIGN.md`.
- Keep the design aligned with the dark gold aesthetic (`bg-canvas`, `bg-shell`, `text-gold`).
- Ensure high performance without heavy libraries.

**Non-Goals:**
- Adding a new heavy 3D library (Three.js) or animation library (Framer Motion, GSAP).
- Redesigning the authenticated roadmap canvas (`@xyflow/react` is for authenticated users, our animation is a visual fake/representation).
- Complex backend logic (this is a pure frontend enhancement).

## Decisions

**1. Animation Implementation**
- *Decision:* Use CSS `@keyframes` defined in `src/frontend/src/index.css` combined with React state hooks to manage animation phases (typing phase -> explosion phase -> roadmap phase).
- *Rationale:* Adheres to `DESIGN.md` rules restricting new animation libraries. React state allows conditional rendering of the terminal text vs the constellation nodes.

**2. Component Structure**
- *Decision:* Create a new feature folder `features/landing/` with components `TerminalEffect`, `ConstellationEffect`, and the page `pages/Home/HomePage.tsx`.
- *Rationale:* Follows the domain placement rules in `DESIGN.md`. (Using Home as it exists in pages directory).

**3. Visual Language**
- *Decision:* Use existing tokens (`var(--color-gold)`, `var(--color-canvas)`) and Tailwind utilities (e.g., `text-gold`, `bg-canvas`). The nodes will use box-shadows (`shadow-[0_0_15px_var(--color-gold)]`) to create the glowing effect.

## Risks / Trade-offs

- **Risk:** CSS animations might become complex to manage across multiple React component states.
  - **Mitigation:** Break the animation down into clear sequential phases managed by a single `useEffect` with timeouts, keeping the CSS classes simple (e.g., `.fade-in`, `.pulse-glow`).
- **Risk:** Performance issues on low-end devices due to many glowing DOM nodes.
  - **Mitigation:** Limit the constellation to a small number of representative nodes (e.g., 10-15) rather than a full 100-node graph.
