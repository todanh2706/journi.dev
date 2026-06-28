## Why

Journi.dev currently lacks a high-impact welcome experience for unauthenticated users when they first land on the site. A "Premium Dark Learning OS" aesthetic requires an initial hook that feels tech-forward, futuristic, and sets the tone before the user logs in to view their roadmap. This change implements the "Code-to-Galaxy" terminal effect on the public landing page to instantly convey the platform's developer-centric, roadmap-driven value proposition.

## What Changes

- Add a new public landing page (`Home` or `Welcome`) as the default `/` route for unauthenticated users.
- Implement a CSS-driven terminal typing animation ("journi init...").
- Implement a transition where the terminal text dissolves into a glowing gold node graph representing a roadmap.
- Add clear Call-to-Action (CTA) buttons to Login or Sign Up.
- Ensure the animation uses existing TailwindCSS utilities or custom keyframes in `index.css` without introducing heavy external animation libraries, adhering to `DESIGN.md`.

## Capabilities

### New Capabilities
- `landing-welcome`: Introduces the public landing page and the high-tech terminal-to-roadmap welcome animation.

### Modified Capabilities
None.

## Impact

- **Frontend Routes**: Updates the router to display the new landing page at `/` for guests.
- **Frontend Components**: Creates new components for the welcome animation (e.g., `TerminalWelcome`, `RoadmapConstellation`).
- **CSS**: Adds custom keyframes to `src/frontend/src/index.css` for the typing and node expansion effect.
- **Dependencies**: None. Strict adherence to avoiding new animation libraries.
