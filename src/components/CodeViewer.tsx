import React, { useState } from "react";
import { Copy, Check, Download, Terminal, Edit3, Sparkles, FileCode, Archive, ExternalLink } from "lucide-react";
import { BotProject, BotFile } from "../types";

interface CodeViewerProps {
  project: BotProject;
  onRefineWithAI: (request: string) => Promise<void>;
  isRefining: boolean;
}

export const CodeViewer: React.FC<CodeViewerProps> = ({
  project,
  onRefineWithAI,
  isRefining,
}) => {
  const [activeFileIndex, setActiveFileIndex] = useState(0);
  const [copied, setCopied] = useState(false);
  const [aiRefinementText, setAiRefinementText] = useState("");
  const [isDownloadingZip, setIsDownloadingZip] = useState(false);

  const activeFile: BotFile = project.files[activeFileIndex] || project.files[0] || {
    name: "bot.py",
    language: "python",
    content: "# Nessun file",
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(activeFile.content);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDownloadSingle = () => {
    const blob = new Blob([activeFile.content], { type: "text/plain;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = activeFile.name;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  const handleDownloadZip = async () => {
    try {
      setIsDownloadingZip(true);
      const res = await fetch("/api/download-zip", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          botTitle: project.title || "crea_bot_android",
          files: project.files,
        }),
      });

      if (!res.ok) throw new Error("Errore durante il download dello zip");

      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `${(project.title || "crea_bot").toLowerCase().replace(/[^a-z0-9_-]/g, "_")}.zip`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (e: any) {
      alert("Errore download ZIP: " + e.message);
    } finally {
      setIsDownloadingZip(false);
    }
  };

  const handleRefineSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!aiRefinementText.trim()) return;
    await onRefineWithAI(aiRefinementText.trim());
    setAiRefinementText("");
  };

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-xl flex flex-col">
      {/* Bot Header summary */}
      <div className="p-4 sm:p-5 border-b border-slate-800 bg-slate-950/70 flex flex-wrap items-center justify-between gap-3">
        <div>
          <div className="flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse" />
            <h3 className="font-bold text-slate-100 text-base sm:text-lg tracking-tight">
              {project.title}
            </h3>
            <span className="text-[11px] font-mono px-2 py-0.5 rounded bg-slate-800 text-slate-300 border border-slate-700">
              {project.gameName}
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-0.5 max-w-2xl">
            {project.instructions || "Bot configurato e pronto all'uso su Termux."}
          </p>
        </div>

        {/* Download Buttons */}
        <div className="flex items-center gap-2">
          <button
            id="btn-download-single"
            onClick={handleDownloadSingle}
            className="text-xs font-medium px-3 py-1.5 rounded-xl border border-slate-700 bg-slate-800 hover:bg-slate-700 text-slate-200 transition-colors flex items-center gap-1.5"
            title={`Scarica ${activeFile.name}`}
          >
            <Download className="w-3.5 h-3.5" />
            <span>Scarica {activeFile.name}</span>
          </button>

          <button
            id="btn-download-zip"
            onClick={handleDownloadZip}
            disabled={isDownloadingZip}
            className="text-xs font-semibold px-3 py-1.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 transition-colors flex items-center gap-1.5 shadow-sm shadow-emerald-500/20"
          >
            <Archive className="w-3.5 h-3.5" />
            <span>{isDownloadingZip ? "Compressione..." : "Scarica ZIP Completo"}</span>
          </button>
        </div>
      </div>

      {/* Termux Run & Edit Quick Bar */}
      <div className="px-4 py-2.5 bg-slate-950 border-b border-slate-800 flex flex-wrap items-center justify-between gap-2 text-xs">
        <div className="flex items-center gap-2 flex-wrap">
          <span className="text-slate-400 font-medium flex items-center gap-1">
            <Terminal className="w-3.5 h-3.5 text-amber-400" />
            Avvio rapido Termux:
          </span>
          <code className="bg-slate-900 border border-slate-800 px-2.5 py-1 rounded-md text-amber-300 font-mono text-[11px] select-all">
            {project.language === "python" ? "python bot.py" : "bash bot.sh"}
          </code>
        </div>

        <div className="flex items-center gap-2 text-slate-400">
          <span className="flex items-center gap-1">
            <Edit3 className="w-3.5 h-3.5 text-teal-400" />
            Modifica sul telefono con:
          </span>
          <code className="bg-slate-900 border border-slate-800 px-2 py-0.5 rounded text-teal-300 font-mono text-[11px] select-all">
            nano {activeFile.name}
          </code>
        </div>
      </div>

      {/* File Tabs Bar */}
      <div className="bg-slate-950/90 border-b border-slate-800 px-4 flex items-center justify-between gap-2 overflow-x-auto">
        <div className="flex items-center gap-1 py-2">
          {project.files.map((file, idx) => {
            const isActive = idx === activeFileIndex;
            return (
              <button
                key={file.name}
                id={`tab-file-${file.name.replace(".", "-")}`}
                onClick={() => setActiveFileIndex(idx)}
                className={`text-xs font-mono font-medium px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 ${
                  isActive
                    ? "bg-slate-800 text-emerald-400 border border-slate-700 shadow-sm"
                    : "text-slate-400 hover:text-slate-200 hover:bg-slate-900/60"
                }`}
              >
                <FileCode className="w-3.5 h-3.5" />
                <span>{file.name}</span>
              </button>
            );
          })}
        </div>

        {/* Copy Active File Button */}
        <button
          id="btn-copy-code"
          onClick={handleCopy}
          className="text-xs font-medium px-2.5 py-1 rounded-lg bg-slate-900 hover:bg-slate-800 text-slate-300 border border-slate-800 transition-colors flex items-center gap-1.5"
        >
          {copied ? (
            <>
              <Check className="w-3.5 h-3.5 text-emerald-400" />
              <span className="text-emerald-400">Copiato!</span>
            </>
          ) : (
            <>
              <Copy className="w-3.5 h-3.5" />
              <span>Copia codice</span>
            </>
          )}
        </button>
      </div>

      {/* Code Editor Body */}
      <div className="relative bg-[#0b0f19] p-4 font-mono text-xs overflow-x-auto max-h-[460px] overflow-y-auto leading-relaxed selection:bg-emerald-500/30">
        <pre className="text-slate-200 whitespace-pre">
          {activeFile.content}
        </pre>
      </div>

      {/* AI Refinement Box */}
      <div className="p-4 border-t border-slate-800 bg-slate-950/80">
        <form onSubmit={handleRefineSubmit} className="flex gap-2 items-center">
          <div className="relative flex-1">
            <input
              type="text"
              id="input-refine-ai"
              value={aiRefinementText}
              onChange={(e) => setAiRefinementText(e.target.value)}
              placeholder="Chiedi all'IA di modificare questo bot (es: 'Aggiungi una pausa di 10s ogni 10 cicli', 'Aggiungi suono/vibrazione')..."
              disabled={isRefining}
              className="w-full bg-slate-900 border border-slate-800 focus:border-teal-500 focus:ring-1 focus:ring-teal-500/30 rounded-xl px-3.5 py-2 text-xs text-slate-100 placeholder-slate-500 transition-colors"
            />
          </div>
          <button
            type="submit"
            id="btn-refine-submit"
            disabled={isRefining || !aiRefinementText.trim()}
            className="px-3.5 py-2 rounded-xl bg-teal-500/20 hover:bg-teal-500/30 border border-teal-500/40 text-teal-300 font-medium text-xs disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center gap-1.5 whitespace-nowrap"
          >
            {isRefining ? (
              <>
                <div className="w-3 h-3 border-2 border-teal-300 border-t-transparent rounded-full animate-spin" />
                <span>Modifica in corso...</span>
              </>
            ) : (
              <>
                <Sparkles className="w-3.5 h-3.5 text-teal-400" />
                <span>Aggiorna con IA</span>
              </>
            )}
          </button>
        </form>
      </div>
    </div>
  );
};
