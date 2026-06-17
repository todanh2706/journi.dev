import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Map, Loader2, AlertCircle, ArrowLeft, CheckCircle, Circle, Lock, BookOpen, Code, Folder, CheckSquare, Zap } from "lucide-react";
import { roadmapService } from "../../services/roadmap.service";
import { type RoadmapWithNodes, type SkillNode } from "../../types/roadmap";

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

  const getNodeIcon = (node: SkillNode) => {
    if (node.isLocked) return <Lock size={20} className="text-gray-500" />;
    if (node.progressStatus === 'COMPLETED') return <CheckCircle size={20} className="text-green-400" />;
    if (node.progressStatus === 'IN_PROGRESS') return <Loader2 size={20} className="text-indigo-400 animate-spin" />;
    return <Circle size={20} className="text-indigo-400" />;
  };

  const getNodeTypeIcon = (type: string) => {
    switch (type) {
      case 'LESSON':
        return <BookOpen size={16} className="opacity-70" />;
      case 'PRACTICE':
        return <Code size={16} className="opacity-70" />;
      case 'PROJECT':
        return <Folder size={16} className="opacity-70" />;
      case 'QUIZ':
        return <CheckSquare size={16} className="opacity-70" />;
      case 'CHALLENGE':
        return <Zap size={16} className="opacity-70" />;
      default:
        return <BookOpen size={16} className="opacity-70" />;
    }
  };

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

      <div className="px-12 pb-12 max-w-4xl">
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
          <div className="relative">
            {/* Vertical Line */}
            <div className="absolute left-6 top-10 bottom-10 w-0.5 bg-indigo-500/20 -translate-x-1/2 rounded-full" />
            
            <div className="space-y-6">
              {data.nodes.sort((a, b) => a.orderIndex - b.orderIndex).map((node) => {
                const isLocked = node.isLocked;
                
                return (
                  <div key={node.nodeId} className={`relative flex items-start group ${isLocked ? 'opacity-60' : ''}`}>
                    {/* Node Connector Icon */}
                    <div className={`relative z-10 w-12 h-12 shrink-0 flex items-center justify-center rounded-full bg-[#0a0a14] border-4 border-[#0a0a14] transition-transform ${!isLocked ? 'group-hover:scale-110' : ''}`}>
                      <div className={`w-10 h-10 rounded-full flex items-center justify-center bg-[#141527] border border-white/[0.06] shadow-sm ${!isLocked ? 'shadow-indigo-500/10' : ''}`}>
                        {getNodeIcon(node)}
                      </div>
                    </div>
                    
                    {/* Node Card */}
                    <div className="ml-6 flex-1 bg-[#141527] hover:bg-[#1a1b30] border border-white/[0.06] hover:border-indigo-500/30 rounded-2xl p-6 transition-all duration-300 relative overflow-hidden">
                      {/* Glassmorphism gradient effect */}
                      {!isLocked && (
                        <div className="absolute top-0 right-0 w-32 h-32 bg-indigo-500/5 blur-[50px] rounded-full pointer-events-none group-hover:bg-indigo-500/10 transition-colors" />
                      )}
                      
                      <div className="relative z-10">
                        <div className="flex items-center justify-between mb-2">
                          <span className="text-[11px] font-bold px-2 py-1 bg-white/[0.05] text-gray-300 rounded-md tracking-wider flex items-center gap-1.5 uppercase">
                            {getNodeTypeIcon(node.nodeType)}
                            {node.nodeType}
                          </span>
                          
                          {node.progressStatus === 'COMPLETED' && (
                            <span className="text-[11px] font-bold px-2 py-1 bg-green-500/10 text-green-400 rounded-md">
                              COMPLETED
                            </span>
                          )}
                        </div>
                        
                        <h3 className={`text-lg font-bold mb-2 ${!isLocked ? 'text-gray-100 group-hover:text-indigo-300 transition-colors' : 'text-gray-300'}`}>
                          {node.orderIndex}. {node.title}
                        </h3>
                        
                        {/* temporarily to use slug as summary */}
                        <p className="text-[14px] text-gray-400"> 
                          {node.slug.split('-').join(' ')} 
                        </p>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>
    </>
  );
}
