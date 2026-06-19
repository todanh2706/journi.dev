import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Map, Loader2, AlertCircle, ArrowLeft } from "lucide-react";
import { RoadmapCanvas, roadmapService, type RoadmapWithNodes } from "../../features/roadmaps";

export default function RoadmapDetailPage() {
  const { roadmapId } = useParams<{ roadmapId: string }>();
  const navigate = useNavigate();
  const [data, setData] = useState<RoadmapWithNodes | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchRoadmapDetails = async () => {
      if (!roadmapId) return;
      try {
        setLoading(true);
        setError(null);
        const roadmapWithNodes = await roadmapService.getRoadmapWithNodes(roadmapId);
        setData(roadmapWithNodes);
      } catch (err: unknown) {
        console.error("Failed to fetch roadmap details:", err);
        setError("Failed to load the learning path. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchRoadmapDetails();
  }, [roadmapId]);

  return (
    <>
      <header className="px-12 pt-12 pb-8">
        <button 
          onClick={() => navigate('/dashboard/roadmaps')}
          className="flex items-center text-gray-400 hover:text-indigo-400 transition-colors mb-6 text-sm font-medium"
        >
          <ArrowLeft size={16} className="mr-2" />
          Back to Roadmaps
        </button>
        
        {loading ? (
           <div className="h-10 w-64 bg-white/5 rounded-lg animate-pulse mb-2"></div>
        ) : data ? (
           <>
             <h1 className="text-[28px] font-bold mb-2 flex items-center">
                {data.title}
             </h1>
             <p className="text-gray-400 text-[15px] max-w-3xl">{data.description}</p>
           </>
        ) : null}
      </header>

      <div className="px-4 pb-12 sm:px-8 xl:px-12">
        {loading ? (
          <div className="flex flex-col items-center justify-center py-20 text-gray-400">
            <Loader2 size={32} className="animate-spin text-indigo-400 mb-4" />
            <p>Loading your path...</p>
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
        ) : !data || data.nodes.length === 0 ? (
          <div className="bg-[#141527] border border-white/[0.06] rounded-2xl p-12 text-center relative overflow-hidden">
             <div className="absolute top-0 right-0 w-64 h-64 bg-indigo-500/5 blur-[100px] rounded-full pointer-events-none" />
             <div className="relative z-10 flex flex-col items-center">
               <div className="w-16 h-16 bg-white/[0.03] border border-white/[0.05] rounded-full flex items-center justify-center mb-4">
                 <Map size={24} className="text-indigo-400" />
               </div>
               <h3 className="text-xl font-bold mb-2">No Content Yet</h3>
               <p className="text-gray-400 max-w-md">This roadmap doesn't have any skill nodes configured yet.</p>
             </div>
          </div>
        ) : (
          <RoadmapCanvas roadmap={data} />
        )}
      </div>
    </>
  );
}
