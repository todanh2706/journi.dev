import { BrowserRouter, Routes, Route, useLocation } from "react-router-dom";
import Welcome from "./pages/Feed/Welcome";
import SignIn from "./pages/Auth/SignIn";
import SignUp from "./pages/Auth/SignUp";
import Dashboard from "./pages/Feed/Dashboard";
import NotFound from "./pages/NotFound";
import { SlideUpTransition } from "./utils/transitions/SlideUpTransition";

function AnimatedRoutes() {
    const location = useLocation();
    
    // Keying the SlideUpTransition by pathname ensures React unmounts and remounts
    // the entire component tree inside it on every navigation, 
    // guaranteeing the initial transition effect fires flawlessly.
    return (
        <SlideUpTransition key={location.pathname}>
            <Routes location={location}>
                <Route path="/" element={<Welcome />} />
                <Route path="/signin" element={<SignIn />} />
                <Route path="/signup" element={<SignUp />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="*" element={<NotFound />} />
            </Routes>
        </SlideUpTransition>
    );
}

function App() {
    return (
        <BrowserRouter>
            <AnimatedRoutes />
        </BrowserRouter>
    );
}

export default App;
