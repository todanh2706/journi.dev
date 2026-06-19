
interface LeaderboardItemProps {
  rank: number,
  name: string,
  handle: string,
  pts: string,
  avatar: string,
  isUser?: boolean
}

export function LeaderboardItem({ rank, name, handle, pts, avatar, isUser }: LeaderboardItemProps) {
  return (
    <div className={`flex items-center gap-3.5 p-2.5 rounded-xl ${isUser ? 'bg-[#1a1b30] border border-indigo-500/20 shadow-lg shadow-indigo-500/5' : ''}`}>
      <div className={`w-6 text-center text-[15px] font-bold ${
        rank === 1 ? 'text-amber-400 drop-shadow-[0_0_8px_rgba(251,191,36,0.3)]' :
        rank === 2 ? 'text-gray-300 drop-shadow-[0_0_8px_rgba(209,213,219,0.3)]' :
        rank === 3 ? 'text-amber-600' :
        isUser ? 'text-indigo-400' :
        'text-gray-500'
      }`}>
        {rank}
      </div>
      <img src={avatar} alt={name} className="w-9 h-9 rounded-full border border-white/10" />
      <div className="flex-1 min-w-0">
        <div className="text-[14px] font-medium text-gray-200 truncate">{name}</div>
        <div className="text-[12px] text-gray-500 truncate">{handle}</div>
      </div>
      <div className={`text-[14px] font-semibold tracking-tight ${isUser ? 'text-indigo-400' : 'text-indigo-300'}`}>
        {pts}
      </div>
    </div>
  );
}
