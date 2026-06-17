## 1. UI Updates in NotFound.tsx

- [x] 1.1 Import `useNavigate` from `react-router-dom` in `NotFound.tsx`.
- [x] 1.2 Implement a constant `hasHistory` using `window.history.length > 2` to detect if the user navigated from within the app.
- [x] 1.3 Update the rendering logic of the CTA button to conditionally display "Go Back" using a `<button>` that triggers `navigate(-1)` if `hasHistory` is true.
- [x] 1.4 Alternatively, if `hasHistory` is false, the same button will display "Go to Homepage" and trigger `navigate("/")`.

## 2. Verification

- [x] 2.1 Navigate from `/dashboard` to a non-existent route (e.g., `/dashboard/fake-route`) and verify the button says "Go Back" and returns to `/dashboard` when clicked.
- [x] 2.2 Open a new tab and paste a non-existent URL directly (e.g., `localhost:5173/fake`), verify the button says "Go to Homepage" and links to `/`.
- [x] 2.3 Verify button styling is identical in both states (gradient, hover effects, shadow, padding).
- [x] 2.4 Run `npm run build` to ensure no TypeScript compilation errors.
