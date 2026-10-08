import React from "react";
import { Bot, Terminal, Github, Smartphone, Sparkles, Crosshair } from "lucide-react";

interface HeaderProps {
  onOpenTermuxGuide: () => void;
  onOpenGithubSync: () => void;
  onToggleCoords: () => void;
  isCoordsOpen: boolean;
}

export const Header: React.FC<HeaderProps> = ({
  onOpenTermuxGuide,
  onOpenGithubSync,
  onToggleCoords,
  isCoordsOpen,
}) => {
  return (
    <header className="border-b border-slate-800 bg-slate-950/80 backdrop-blur-md sticky top-0 z-30">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-3.5 flex flex-wrap items-center justify-between gap-3">
        {/* Brand & Logo */}
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-600 to-teal-400 p-0.5 shadow-lg shadow-emerald-500/20">
            <div className="w-full h-full bg-slate-950 rounded-[10px] flex items-center justify-center">
              <Bot className="w-5 h-5 text-emerald-400" />
            </div>
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="font-bold text-lg text-slate-100 tracking-tight">Crea Bot Android</h1>
              <span className="text-[10px] uppercase font-semibold px-2 py-0.5 rounded-full bg-emerald-500/15 text-emerald-300 border border-emerald-500/30">
                Termux Native
              </span>
            </div>
            <p className="text-xs text-slate-400">
              Generatore di automazione e macro con IA per giochi Android
            </p>
          </div>
        </div>

        {/* Action badges & tools */}
        <div className="flex items-center gap-2 flex-wrap">
          <button
            id="btn-toggle-coords"
            onClick={onToggleCoords}
            className={`text-xs font-medium px-3 py-1.5 rounded-lg border transition-all flex items-center gap-1.5 ${
              isCoordsOpen
                ? "bg-teal-500/20 border-teal-400 text-teal-200 shadow-sm"
                : "bg-slate-900 border-slate-700 text-slate-300 hover:border-slate-600 hover:bg-slate-800"
            }`}
          >
            <Crosshair className="w-3.5 h-3.5 text-teal-400" />
            <span className="hidden sm:inline">Rileva</span> Coordinate
          </button>

          <button
            id="btn-termux-guide"
            onClick={onOpenTermuxGuide}
            className="text-xs font-medium px-3 py-1.5 rounded-lg border border-slate-700 bg-slate-900 hover:bg-slate-800 text-slate-300 transition-colors flex items-center gap-1.5"
          >
            <Terminal className="w-3.5 h-3.5 text-amber-400" />
            <span>Guida Termux</span>
          </button>

          <button
            id="btn-github-sync"
            onClick={onOpenGithubSync}
            className="text-xs font-medium px-3 py-1.5 rounded-lg border border-emerald-600/40 bg-emerald-950/40 hover:bg-emerald-900/40 text-emerald-300 transition-colors flex items-center gap-1.5"
          >
            <Github className="w-3.5 h-3.5 text-emerald-400" />
            <span>Repo 'crea-bot'</span>
          </button>
        </div>
      </div>
    </header>
  );
};
