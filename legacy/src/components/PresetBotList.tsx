import React from "react";
import { Bot, Cpu, Play, Terminal, ArrowRight } from "lucide-react";
import { PRESET_BOTS } from "../data/presetBots";
import { BotProject } from "../types";

interface PresetBotListProps {
  currentBotId: string;
  onSelectBot: (bot: BotProject) => void;
}

export const PresetBotList: React.FC<PresetBotListProps> = ({ currentBotId, onSelectBot }) => {
  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4 sm:p-5 shadow-xl">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <Bot className="w-4 h-4 text-emerald-400" />
          <h3 className="font-semibold text-slate-100 text-sm">Modelli Bot Predefiniti (Pronti all'uso)</h3>
        </div>
        <span className="text-[11px] text-slate-400">Clicca per caricare o personalizzare</span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
        {PRESET_BOTS.map((preset) => {
          const isSelected = preset.id === currentBotId;
          return (
            <button
              key={preset.id}
              id={`btn-preset-${preset.id}`}
              onClick={() => onSelectBot(preset)}
              className={`text-left p-3.5 rounded-xl border transition-all flex flex-col justify-between group ${
                isSelected
                  ? "bg-slate-800 border-emerald-500 shadow-md shadow-emerald-500/10 ring-1 ring-emerald-500/20"
                  : "bg-slate-950/60 border-slate-800 hover:border-slate-700 hover:bg-slate-900"
              }`}
            >
              <div>
                <div className="flex items-center justify-between gap-2 mb-1.5">
                  <span className="text-xs font-bold text-slate-100 group-hover:text-emerald-300 transition-colors">
                    {preset.title}
                  </span>
                  <span
                    className={`text-[10px] font-mono uppercase font-semibold px-1.5 py-0.5 rounded ${
                      preset.language === "python"
                        ? "bg-emerald-500/10 text-emerald-400 border border-emerald-500/20"
                        : "bg-amber-500/10 text-amber-400 border border-amber-500/20"
                    }`}
                  >
                    {preset.language}
                  </span>
                </div>
                <p className="text-[11px] text-slate-400 line-clamp-2 leading-relaxed mb-2">
                  {preset.instructions}
                </p>
              </div>

              <div className="flex items-center justify-between pt-2 border-t border-slate-800/80 text-[10px] text-slate-500 font-mono">
                <span>{preset.gameName}</span>
                <span className="flex items-center gap-1 text-slate-400 group-hover:text-emerald-300 transition-colors">
                  {isSelected ? "Attivo" : "Carica"}
                  <ArrowRight className="w-3 h-3" />
                </span>
              </div>
            </button>
          );
        })}
      </div>
    </div>
  );
};
