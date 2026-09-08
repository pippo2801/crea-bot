import React, { useState } from "react";
import { Sparkles, Play, ShieldAlert, Cpu, Layers, HelpCircle } from "lucide-react";
import { GenerationOptions, CoordinatePoint } from "../types";

interface BotGeneratorFormProps {
  onGenerate: (options: GenerationOptions) => Promise<void>;
  isLoading: boolean;
  selectedCoordinates: CoordinatePoint[];
  onRemoveCoordinate: (id: string) => void;
}

const PRESET_PROMPTS = [
  {
    title: "Auto-Clicker Anti-Ban",
    prompt: "Crea un auto-clicker ultra veloce per accumulare risorse, con ritardo casuale tra 150ms e 350ms e variazione casuale della coordinata di ±10 pixel per non farsi bannare.",
    game: "Idle Clicker / Minigiochi",
  },
  {
    title: "Loop Farm Dungeon",
    prompt: "Avvia la battaglia, attendi 40 secondi che finisca il livello, clicca al centro per raccogliere le ricompense di vittoria, poi clicca sul pulsante 'Ripeti' in basso a destra. Ripeti per 50 cicli.",
    game: "RPG / Gacha Battler",
  },
  {
    title: "Raccolta Oraria Risorse",
    prompt: "Esegui un ciclo di raccolta ogni 15 minuti: tocca i 4 edifici del villaggio per raccogliere oro ed elisir, poi metti il bot in pausa sonno e riprendi.",
    game: "Strategia / Villaggio",
  },
  {
    title: "Gacha Summon & Reroll",
    prompt: "Premi il tasto Evoca 10x, salta l'animazione toccando in alto a destra, attendi il caricamento e salva uno screenshot dello schermo su /sdcard/gacha.png.",
    game: "Gacha Anime",
  },
];

export const BotGeneratorForm: React.FC<BotGeneratorFormProps> = ({
  onGenerate,
  isLoading,
  selectedCoordinates,
  onRemoveCoordinate,
}) => {
  const [gameName, setGameName] = useState("Gioco Android");
  const [prompt, setPrompt] = useState(
    "Crea un bot per cliccare ripetutamente con variazioni umane di tempo e coordinate, per evitare il ban durante il farming notturno."
  );
  const [resolution, setResolution] = useState("1080x2400");
  const [orientation, setOrientation] = useState<"portrait" | "landscape">("portrait");
  const [language, setLanguage] = useState<"python" | "bash">("python");
  const [antiBanJitter, setAntiBanJitter] = useState(true);
  const [failSafeKillswitch, setFailSafeKillswitch] = useState(true);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!prompt.trim()) return;

    await onGenerate({
      gameName: gameName.trim() || "Gioco Android",
      prompt,
      resolution,
      orientation,
      language,
      antiBanJitter,
      failSafeKillswitch,
      coordinateSequence: selectedCoordinates,
    });
  };

  return (
    <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 shadow-xl relative overflow-hidden">
      {/* Decorative glow */}
      <div className="absolute -top-16 -right-16 w-36 h-36 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none" />

      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-emerald-500/20 flex items-center justify-center text-emerald-400">
            <Sparkles className="w-4 h-4" />
          </div>
          <div>
            <h2 className="font-semibold text-slate-100 text-sm sm:text-base">
              Genera Bot con Intelligenza Artificiale
            </h2>
            <p className="text-xs text-slate-400">
              Descrivi l'automazione che desideri: l'IA scriverà lo script pronto per Termux
            </p>
          </div>
        </div>
      </div>

      {/* Suggested prompts */}
      <div className="mb-4">
        <label className="text-xs font-medium text-slate-400 mb-1.5 block">
          Suggerimenti pronti:
        </label>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
          {PRESET_PROMPTS.map((item, idx) => (
            <button
              key={idx}
              type="button"
              onClick={() => {
                setPrompt(item.prompt);
                setGameName(item.game);
              }}
              className="text-left p-2 rounded-lg bg-slate-950/60 border border-slate-800 hover:border-emerald-500/40 hover:bg-slate-800/80 transition-all group"
            >
              <span className="text-[11px] font-semibold text-slate-200 group-hover:text-emerald-300 block truncate">
                {item.title}
              </span>
              <span className="text-[10px] text-slate-500 block truncate">{item.game}</span>
            </button>
          ))}
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Game Name & Language */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div>
            <label className="text-xs font-medium text-slate-300 block mb-1">
              Nome del Gioco / App
            </label>
            <input
              type="text"
              id="input-game-name"
              value={gameName}
              onChange={(e) => setGameName(e.target.value)}
              placeholder="es. Clash of Clans, Roblox, Coin Master, RPG"
              className="w-full bg-slate-950 border border-slate-800 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500/30 rounded-xl px-3.5 py-2 text-sm text-slate-100 placeholder-slate-600 transition-colors"
            />
          </div>

          <div>
            <label className="text-xs font-medium text-slate-300 block mb-1">
              Linguaggio dello Script
            </label>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                id="btn-lang-python"
                onClick={() => setLanguage("python")}
                className={`text-xs font-medium py-2 px-3 rounded-xl border flex items-center justify-center gap-1.5 transition-all ${
                  language === "python"
                    ? "bg-emerald-500/15 border-emerald-500 text-emerald-300"
                    : "bg-slate-950 border-slate-800 text-slate-400 hover:bg-slate-800"
                }`}
              >
                <Cpu className="w-3.5 h-3.5" />
                Python (Consigliato)
              </button>
              <button
                type="button"
                id="btn-lang-bash"
                onClick={() => setLanguage("bash")}
                className={`text-xs font-medium py-2 px-3 rounded-xl border flex items-center justify-center gap-1.5 transition-all ${
                  language === "bash"
                    ? "bg-emerald-500/15 border-emerald-500 text-emerald-300"
                    : "bg-slate-950 border-slate-800 text-slate-400 hover:bg-slate-800"
                }`}
              >
                <Play className="w-3.5 h-3.5" />
                Bash (Ultra-Light)
              </button>
            </div>
          </div>
        </div>

        {/* Resolution & Orientation */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <div>
            <label className="text-xs font-medium text-slate-300 block mb-1">
              Risoluzione Schermo Smartphone
            </label>
            <select
              id="select-resolution"
              value={resolution}
              onChange={(e) => setResolution(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 focus:border-emerald-500 rounded-xl px-3.5 py-2 text-sm text-slate-100 transition-colors"
            >
              <option value="1080x2400">1080 x 2400 (FHD+ Standard 20:9)</option>
              <option value="1080x1920">1080 x 1920 (FHD Classico 16:9)</option>
              <option value="720x1600">720 x 1600 (HD+ Standard)</option>
              <option value="1440x3200">1440 x 3200 (QHD+ Alta risoluzione)</option>
              <option value="1080x2340">1080 x 2340 (Xiaomi / Poco / Samsung)</option>
              <option value="800x1280">800 x 1280 (Tablet Android)</option>
            </select>
          </div>

          <div>
            <label className="text-xs font-medium text-slate-300 block mb-1">
              Orientamento di Gioco
            </label>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                id="btn-orientation-portrait"
                onClick={() => setOrientation("portrait")}
                className={`text-xs font-medium py-2 px-3 rounded-xl border flex items-center justify-center gap-1.5 transition-all ${
                  orientation === "portrait"
                    ? "bg-teal-500/15 border-teal-500 text-teal-300"
                    : "bg-slate-950 border-slate-800 text-slate-400 hover:bg-slate-800"
                }`}
              >
                <span>Verticale (Portrait)</span>
              </button>
              <button
                type="button"
                id="btn-orientation-landscape"
                onClick={() => setOrientation("landscape")}
                className={`text-xs font-medium py-2 px-3 rounded-xl border flex items-center justify-center gap-1.5 transition-all ${
                  orientation === "landscape"
                    ? "bg-teal-500/15 border-teal-500 text-teal-300"
                    : "bg-slate-950 border-slate-800 text-slate-400 hover:bg-slate-800"
                }`}
              >
                <span>Orizzontale (Landscape)</span>
              </button>
            </div>
          </div>
        </div>

        {/* AI Prompt Input */}
        <div>
          <label className="text-xs font-medium text-slate-300 block mb-1">
            Cosa deve fare il bot? (Istruzioni dettagliate)
          </label>
          <textarea
            id="textarea-prompt"
            rows={3}
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            placeholder="Descrivi in dettaglio la sequenza di click, attese o pulsanti da premere nel gioco..."
            className="w-full bg-slate-950 border border-slate-800 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500/30 rounded-xl px-3.5 py-2.5 text-sm text-slate-100 placeholder-slate-600 transition-colors resize-none leading-relaxed"
          />
        </div>

        {/* Coordinate pill chips if any were picked */}
        {selectedCoordinates.length > 0 && (
          <div className="p-3 bg-slate-950/80 rounded-xl border border-teal-500/30">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-medium text-teal-300 flex items-center gap-1.5">
                <Layers className="w-3.5 h-3.5" />
                {selectedCoordinates.length} Coordinate salvate dal simulatore:
              </span>
              <span className="text-[10px] text-slate-400">Verranno incluse nel prompt dell'IA</span>
            </div>
            <div className="flex flex-wrap gap-1.5">
              {selectedCoordinates.map((c) => (
                <div
                  key={c.id}
                  className="bg-slate-900 border border-slate-700 px-2 py-1 rounded-lg text-[11px] text-slate-300 flex items-center gap-2"
                >
                  <span className="font-semibold text-teal-400">{c.name}:</span>
                  <span>
                    ({c.x}, {c.y})
                  </span>
                  <span className="text-slate-500 text-[10px]">{c.delaySec}s</span>
                  <button
                    type="button"
                    onClick={() => onRemoveCoordinate(c.id)}
                    className="text-red-400 hover:text-red-300 font-bold ml-1 text-xs"
                  >
                    ×
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Anti-Ban and Kill Switch options */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
          <label className="flex items-center gap-2.5 p-2.5 rounded-xl bg-slate-950/60 border border-slate-800 cursor-pointer hover:border-slate-700">
            <input
              type="checkbox"
              id="check-antiban"
              checked={antiBanJitter}
              onChange={(e) => setAntiBanJitter(e.target.checked)}
              className="w-4 h-4 rounded text-emerald-500 focus:ring-emerald-500/30 bg-slate-900 border-slate-700"
            />
            <div>
              <span className="text-xs font-medium text-slate-200 block">
                Anti-Ban Jitter (Tocco Umano)
              </span>
              <span className="text-[10px] text-slate-500 block">
                Varia casualmente timing e pixel di ±10px
              </span>
            </div>
          </label>

          <label className="flex items-center gap-2.5 p-2.5 rounded-xl bg-slate-950/60 border border-slate-800 cursor-pointer hover:border-slate-700">
            <input
              type="checkbox"
              id="check-killswitch"
              checked={failSafeKillswitch}
              onChange={(e) => setFailSafeKillswitch(e.target.checked)}
              className="w-4 h-4 rounded text-emerald-500 focus:ring-emerald-500/30 bg-slate-900 border-slate-700"
            />
            <div>
              <span className="text-xs font-medium text-slate-200 block">
                Kill-Switch di Emergenza
              </span>
              <span className="text-[10px] text-slate-500 block">
                Arresto immediato con Ctrl+C o file stop
              </span>
            </div>
          </label>
        </div>

        {/* Generate Button */}
        <button
          type="submit"
          id="btn-generate-bot"
          disabled={isLoading || !prompt.trim()}
          className="w-full py-3 px-4 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-400 hover:to-teal-400 text-slate-950 font-semibold text-sm shadow-lg shadow-emerald-500/20 disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center justify-center gap-2"
        >
          {isLoading ? (
            <>
              <div className="w-4 h-4 border-2 border-slate-950 border-t-transparent rounded-full animate-spin" />
              <span>Gemini sta generando il tuo bot per Termux...</span>
            </>
          ) : (
            <>
              <Sparkles className="w-4 h-4 text-slate-950" />
              <span>Genera Bot per Android & Termux</span>
            </>
          )}
        </button>
      </form>
    </div>
  );
};
