import { BrowserRouter, Routes, Route, useLocation } from "react-router-dom";
import HomePage from "./pages/Home/HomePage";
import SignIn from "./pages/Auth/SignIn";
import SignUp from "./pages/Auth/SignUp";
import DashboardLayout from "./pages/Dashboard/DashboardLayout";
import DashboardOverview from "./pages/Dashboard/DashboardOverview";
import ProfilePage from "./pages/Dashboard/ProfilePage";
import RoadmapsPage from "./pages/Roadmaps/RoadmapsPage";
import RoadmapDetailPage from "./pages/Roadmaps/RoadmapDetailPage";
import PracticePage from "./pages/Roadmaps/PracticePage";
import NotFound from "./pages/NotFound";
import { SlideUpTransition } from "./utils/transitions/SlideUpTransition";
import { AuthProvider } from "./features/auth";

function AnimatedRoutes() {
    const location = useLocation();
    
    return (
        <Routes location={location}>
            <Route path="/" element={<SlideUpTransition key="home"><HomePage /></SlideUpTransition>} />
            <Route path="/signin" element={<SlideUpTransition key="signin"><SignIn /></SlideUpTransition>} />
            <Route path="/signup" element={<SlideUpTransition key="signup"><SignUp /></SlideUpTransition>} />
            <Route path="/dashboard" element={<DashboardLayout />}>
                <Route index element={<DashboardOverview />} />
                <Route path="profile" element={<ProfilePage />} />
                <Route path="roadmaps" element={<RoadmapsPage />} />
                <Route path="roadmaps/:roadmapId" element={<RoadmapDetailPage />} />
                <Route path="roadmaps/:roadmapId/nodes/:nodeId/practice" element={<PracticePage />} />
            </Route>
            <Route path="*" element={<SlideUpTransition key="not-found"><NotFound /></SlideUpTransition>} />
        </Routes>
    );
}

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <AnimatedRoutes />
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;
