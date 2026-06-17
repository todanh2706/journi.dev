import { BrowserRouter, Routes, Route, useLocation } from "react-router-dom";
import Welcome from "./pages/Feed/Welcome";
import SignIn from "./pages/Auth/SignIn";
import SignUp from "./pages/Auth/SignUp";
import DashboardLayout from "./pages/Feed/DashboardLayout";
import DashboardOverview from "./pages/Feed/DashboardOverview";
import RoadmapsPage from "./pages/Roadmaps/RoadmapsPage";
import RoadmapDetailPage from "./pages/Roadmaps/RoadmapDetailPage";
import NotFound from "./pages/NotFound";
import { SlideUpTransition } from "./utils/transitions/SlideUpTransition";

function AnimatedRoutes() {
    const location = useLocation();
    
    return (
        <Routes location={location}>
            <Route path="/" element={<SlideUpTransition key="home"><Welcome /></SlideUpTransition>} />
            <Route path="/signin" element={<SlideUpTransition key="signin"><SignIn /></SlideUpTransition>} />
            <Route path="/signup" element={<SlideUpTransition key="signup"><SignUp /></SlideUpTransition>} />
            <Route path="/dashboard" element={<DashboardLayout />}>
                <Route index element={<DashboardOverview />} />
                <Route path="roadmaps" element={<RoadmapsPage />} />
                <Route path="roadmaps/:roadmapId" element={<RoadmapDetailPage />} />
            </Route>
            <Route path="*" element={<SlideUpTransition key="not-found"><NotFound /></SlideUpTransition>} />
        </Routes>
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
