import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Map, Loader2, AlertCircle, ArrowRight } from "lucide-react";
import { roadmapService } from "../../services/roadmap.service";
import { type Roadmap } from "../../types/roadmap";

export default function RoadmapsPage() {
  const [roadmaps, setRoadmaps] = useState<Roadmap[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchRoadmaps = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await roadmapService.getRoadmaps();
        // Fallback for API structure differences (if wrapped in data)
        const roadmapsData = Array.isArray(data) ? data : (data as { data?: Roadmap[] }).data || [];
        setRoadmaps(roadmapsData);
      } catch (err: unknown) {
        console.error("Failed to fetch roadmaps:", err);
        // Note: the backend might return 404 if the endpoint is missing or no roadmaps exist yet.
        setError("Failed to load learning roadmaps. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchRoadmaps();
  }, []);

  return (
    <>
      <header className="px-12 pt-12 pb-8">
        <h1 className="text-[28px] font-bold mb-2">Learning Roadmaps</h1>
        <p className="text-gray-400 text-[15px]">Select a path to master new skills and advance your career.</p>
      </header>

      <div className="px-12 pb-12">
        {loading ? (
          <div className="flex flex-col items-center justify-center py-20 text-gray-400">
            <Loader2 size={32} className="animate-spin text-indigo-400 mb-4" />
            <p>Loading your pathways...</p>
          </div>
        ) : error ? (
          <div className="bg-red-500/10 border border-red-500/20 rounded-2xl p-6 flex flex-col items-center justify-center text-center">
            <AlertCircle size={32} className="text-red-400 mb-3" />
            <h3 className="text-lg font-semibold text-red-300 mb-1">Houston, we have a problem</h3>
            <p className="text-red-400/80 mb-4">{error}</p>
            <button 
              onClick={() => window.location.reload()}
              className="px-4 py-2 bg-red-500/20 hover:bg-red-500/30 text-red-300 rounded-lg transition-colors text-sm font-medium"
            >
              Try Again
            </button>
          </div>
        ) : roadmaps.length === 0 ? (
          <div className="bg-[#141527] border border-white/[0.06] rounded-2xl p-12 text-center relative overflow-hidden">
             <div className="absolute top-0 right-0 w-64 h-64 bg-indigo-500/5 blur-[100px] rounded-full pointer-events-none" />
             <div className="relative z-10 flex flex-col items-center">
               <div className="w-16 h-16 bg-white/[0.03] border border-white/[0.05] rounded-full flex items-center justify-center mb-4">
                 <Map size={24} className="text-indigo-400" />
               </div>
               <h3 className="text-xl font-bold mb-2">No Roadmaps Found</h3>
               <p className="text-gray-400 max-w-md">There are currently no active roadmaps available. Please check back later or contact an administrator.</p>
             </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {roadmaps.map((roadmap) => (
              <div 
                key={roadmap.roadmapId} 
                onClick={() => navigate(`/dashboard/roadmaps/${roadmap.roadmapId}`)}
                className="bg-[#141527] hover:bg-[#1a1b30] border border-white/[0.06] hover:border-indigo-500/30 rounded-2xl p-6 transition-all duration-300 group cursor-pointer relative overflow-hidden flex flex-col h-full"
              >
                <div className="absolute top-0 right-0 w-32 h-32 bg-indigo-500/5 blur-[50px] rounded-full pointer-events-none group-hover:bg-indigo-500/10 transition-colors" />
                
                <div className="relative z-10 flex flex-col h-full">
                  <div className="flex items-start justify-between mb-4">
                    <div className="w-10 h-10 bg-indigo-500/10 text-indigo-400 rounded-xl flex items-center justify-center">
                      <Map size={20} />
                    </div>
                    {roadmap.visibility === 'PUBLIC' && (
                      <span className="text-[11px] font-bold px-2 py-1 bg-white/[0.05] text-gray-300 rounded-md">
                        PUBLIC
                      </span>
                    )}
                  </div>
                  
                  <h3 className="text-lg font-bold text-gray-100 mb-2 group-hover:text-indigo-300 transition-colors">
                    {roadmap.title}
                  </h3>
                  
                  <p className="text-[14px] text-gray-400 mb-6 flex-1 line-clamp-3">
                    {roadmap.description || "Start this learning path to achieve mastery in this domain."}
                  </p>
                  
                  <div className="flex items-center text-[13px] font-medium text-indigo-400 group-hover:text-indigo-300 mt-auto">
                    View Path 
                    <ArrowRight size={14} className="ml-1.5 opacity-0 -translate-x-2 group-hover:opacity-100 group-hover:translate-x-0 transition-all" />
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
}
