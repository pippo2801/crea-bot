import React, { useState } from "react";
import { Header } from "./components/Header";
import { BotGeneratorForm } from "./components/BotGeneratorForm";
import { CodeViewer } from "./components/CodeViewer";
import { ScreenCoordinateHelper } from "./components/ScreenCoordinateHelper";
import { PresetBotList } from "./components/PresetBotList";
import { TermuxGuideModal } from "./components/TermuxGuideModal";
import { GitHubSyncModal } from "./components/GitHubSyncModal";
import { PRESET_BOTS } from "./data/presetBots";
import { BotProject, GenerationOptions, CoordinatePoint } from "./types";
import { Sparkles, Terminal, Smartphone, Info, AlertCircle } from "lucide-react";

export default function App() {
  const [currentBot, setCurrentBot] = useState<BotProject>(PRESET_BOTS[0]);
  const [isLoading, setIsLoading] = useState(false);
  const [isRefining, setIsRefining] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isCoordsOpen, setIsCoordsOpen] = useState(false);
  const [isTermuxModalOpen, setIsTermuxModalOpen] = useState(false);
  const [isGithubModalOpen, setIsGithubModalOpen] = useState(false);
  const [recordedCoordinates, setRecordedCoordinates] = useState<CoordinatePoint[]>([]);

  // Generate bot using Gemini AI server endpoint
  const handleGenerateBot = async (options: GenerationOptions) => {
    try {
      setIsLoading(true);
      setErrorMessage(null);

      const response = await fetch("/api/generate-bot", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(options),
      });

      const data = await response.json();

      if (!response.ok || !data.success) {
        throw new Error(data.error || "Impossibile generare il bot con l'IA.");
      }

      setCurrentBot(data.project);
    } catch (err: any) {
      console.error(err);
      setErrorMessage(err.message || "Errore sconosciuto durante la generazione.");
    } finally {
      setIsLoading(false);
    }
  };

  // Refine existing bot with AI prompt
  const handleRefineBot = async (modificationRequest: string) => {
    try {
      setIsRefining(true);
      setErrorMessage(null);

      const response = await fetch("/api/refine-bot", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          currentFiles: currentBot.files,
          modificationRequest,
          gameName: currentBot.gameName,
        }),
      });

      const data = await response.json();

      if (!response.ok || !data.success) {
        throw new Error(data.error || "Impossibile modificare il bot.");
      }

      setCurrentBot((prev) => ({
        ...prev,
        files: data.files,
        instructions: data.summary ? `${prev.instructions} (Aggiornamento: ${data.summary})` : prev.instructions,
      }));
    } catch (err: any) {
      console.error(err);
      setErrorMessage(err.message || "Errore durante la modifica del bot con l'IA.");
    } finally {
      setIsRefining(false);
    }
  };

  const handleAddCoordinate = (coord: CoordinatePoint) => {
    setRecordedCoordinates((prev) => [...prev, coord]);
  };

  const handleRemoveCoordinate = (id: string) => {
    setRecordedCoordinates((prev) => prev.filter((c) => c.id !== id));
  };

  const handleClearCoordinates = () => {
    setRecordedCoordinates([]);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col selection:bg-emerald-500/20 selection:text-emerald-300">
      {/* Top Application Bar */}
      <Header
        onOpenTermuxGuide={() => setIsTermuxModalOpen(true)}
        onOpenGithubSync={() => setIsGithubModalOpen(true)}
        onToggleCoords={() => setIsCoordsOpen(!isCoordsOpen)}
        isCoordsOpen={isCoordsOpen}
      />

      {/* Main Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 py-6 space-y-6">
        {/* Error notification banner if any */}
        {errorMessage && (
          <div className="p-4 rounded-xl bg-red-950/60 border border-red-800 text-red-200 text-xs flex items-center justify-between gap-3 animate-in fade-in">
            <div className="flex items-center gap-2">
              <AlertCircle className="w-4 h-4 text-red-400 shrink-0" />
              <span>{errorMessage}</span>
            </div>
            <button
              onClick={() => setErrorMessage(null)}
              className="font-bold text-red-400 hover:text-red-200"
            >
              Chiudi
            </button>
          </div>
        )}

        {/* Quick Informative Highlight Banner */}
        <div className="bg-gradient-to-r from-emerald-950/40 via-slate-900 to-teal-950/30 border border-emerald-500/20 rounded-2xl p-4 sm:p-5 flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center shrink-0">
              <Smartphone className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-sm sm:text-base font-semibold text-slate-100">
                Esecuzione e Modifica Diretta su Android con Termux
              </h2>
              <p className="text-xs text-slate-400">
                I bot generati funzionano direttamente sul tuo smartphone tramite comandi ADB locali e possono essere modificati in tempo reale usando <code className="text-emerald-400 font-mono">nano bot.py</code>.
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setIsTermuxModalOpen(true)}
              className="text-xs font-semibold px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 transition-colors"
            >
              Come funziona su Termux
            </button>
            <button
              onClick={() => setIsGithubModalOpen(true)}
              className="text-xs font-semibold px-3 py-1.5 rounded-lg bg-emerald-500 hover:bg-emerald-400 text-slate-950 transition-colors shadow-sm"
            >
              Sincronizza GitHub 'crea-bot'
            </button>
          </div>
        </div>

        {/* Optional Interactive Coordinate Picker Drawer */}
        {isCoordsOpen && (
          <div className="animate-in fade-in slide-in-from-top-4 duration-200">
            <ScreenCoordinateHelper
              resolution={currentBot.targetResolution || "1080x2400"}
              orientation={currentBot.orientation || "portrait"}
              onAddCoordinate={handleAddCoordinate}
              coordinates={recordedCoordinates}
              onRemoveCoordinate={handleRemoveCoordinate}
              onClearCoordinates={handleClearCoordinates}
            />
          </div>
        )}

        {/* Primary Workspace Grid: AI Generator Form + Live Code Viewer */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          {/* Left Column: AI Bot Generator Form */}
          <div className="lg:col-span-5 space-y-6">
            <BotGeneratorForm
              onGenerate={handleGenerateBot}
              isLoading={isLoading}
              selectedCoordinates={recordedCoordinates}
              onRemoveCoordinate={handleRemoveCoordinate}
            />

            {/* Template Presets */}
            <PresetBotList
              currentBotId={currentBot.id}
              onSelectBot={(bot) => setCurrentBot(bot)}
            />
          </div>

          {/* Right Column: Code Viewer & Termux Actions */}
          <div className="lg:col-span-7">
            <CodeViewer
              project={currentBot}
              onRefineWithAI={handleRefineBot}
              isRefining={isRefining}
            />
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-800/80 bg-slate-950 py-4 text-center text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-wrap items-center justify-between gap-2">
          <span>Crea Bot Android © 2026 • Compatibile con Termux, ADB & Python</span>
          <div className="flex items-center gap-3">
            <button
              onClick={() => setIsTermuxModalOpen(true)}
              className="hover:text-slate-300 transition-colors"
            >
              Istruzioni Termux
            </button>
            <span>•</span>
            <button
              onClick={() => setIsGithubModalOpen(true)}
              className="hover:text-slate-300 transition-colors"
            >
              GitHub Repository
            </button>
          </div>
        </div>
      </footer>

      {/* Modals */}
      <TermuxGuideModal
        isOpen={isTermuxModalOpen}
        onClose={() => setIsTermuxModalOpen(false)}
      />
      <GitHubSyncModal
        isOpen={isGithubModalOpen}
        onClose={() => setIsGithubModalOpen(false)}
      />
    </div>
  );
}
