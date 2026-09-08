import React, { useState } from "react";
import { X, Github, GitBranch, Terminal, ExternalLink, Check, Copy, ArrowRight, ShieldCheck } from "lucide-react";

interface GitHubSyncModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const GitHubSyncModal: React.FC<GitHubSyncModalProps> = ({ isOpen, onClose }) => {
  const [copiedIndex, setCopiedIndex] = useState<number | null>(null);
  const [githubUsername, setGithubUsername] = useState("tuo-username");

  if (!isOpen) return null;

  const repoName = "crea-bot";
  const repoUrl = `https://github.com/${githubUsername}/${repoName}.git`;

  const copyToClipboard = (text: string, index: number) => {
    navigator.clipboard.writeText(text);
    setCopiedIndex(index);
    setTimeout(() => setCopiedIndex(null), 2000);
  };

  const gitPushCommand = `git remote add origin ${repoUrl}\ngit branch -M main\ngit push -u origin main`;

  const termuxCloneCommand = `pkg install git -y\ngit clone ${repoUrl}\ncd ${repoName}\npython bot.py`;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-in fade-in">
      <div className="bg-slate-900 border border-slate-800 w-full max-w-2xl max-h-[90vh] rounded-2xl shadow-2xl flex flex-col overflow-hidden">
        {/* Modal Header */}
        <div className="p-4 sm:p-5 border-b border-slate-800 flex items-center justify-between bg-slate-950/80">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-emerald-500/20 text-emerald-400 flex items-center justify-center">
              <Github className="w-4 h-4" />
            </div>
            <div>
              <h3 className="font-bold text-slate-100 text-sm sm:text-base">
                Sincronizzazione Repository GitHub: '{repoName}'
              </h3>
              <p className="text-xs text-slate-400">
                Collega il progetto e i tuoi bot al tuo account GitHub
              </p>
            </div>
          </div>

          <button
            id="btn-close-github-modal"
            onClick={onClose}
            className="w-8 h-8 rounded-lg bg-slate-800 text-slate-400 hover:text-slate-100 flex items-center justify-center transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-4 sm:p-6 overflow-y-auto space-y-5 text-xs sm:text-sm text-slate-300">
          {/* Status badge */}
          <div className="p-3 bg-emerald-950/40 border border-emerald-500/30 rounded-xl flex items-center gap-3">
            <div className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-ping" />
            <div>
              <div className="font-semibold text-emerald-300 text-xs">
                Repository Git locale inizializzato: branch 'main'
              </div>
              <div className="text-[11px] text-slate-400">
                Tutti i file di base e il codice del generatore sono pronti per essere pubblicati su GitHub.
              </div>
            </div>
          </div>

          {/* Quick config username */}
          <div className="bg-slate-950/80 border border-slate-800 rounded-xl p-4 space-y-2">
            <label className="block text-xs font-semibold text-slate-200">
              Inserisci il tuo Username GitHub:
            </label>
            <div className="flex gap-2">
              <input
                type="text"
                id="input-github-username"
                value={githubUsername}
                onChange={(e) => setGithubUsername(e.target.value.trim() || "tuo-username")}
                placeholder="es. mario-rossi"
                className="bg-slate-900 border border-slate-700 rounded-lg px-3 py-1.5 text-xs text-slate-100 focus:border-emerald-500 flex-1 font-mono"
              />
            </div>
            <p className="text-[11px] text-slate-500">
              I comandi sottostanti verranno aggiornati in tempo reale con l'URL del tuo repository:{" "}
              <code className="text-emerald-400 font-mono">{repoUrl}</code>
            </p>
          </div>

          {/* Method 1: Export via AI Studio Menu */}
          <div className="bg-slate-950/80 border border-slate-800 rounded-xl p-4 space-y-2">
            <div className="flex items-center gap-2 text-teal-300 font-semibold text-xs sm:text-sm">
              <GitBranch className="w-4 h-4 text-teal-400" />
              <span>Metodo 1 (Consigliato): Esportazione diretta con 1 Click da AI Studio</span>
            </div>
            <p className="text-xs text-slate-400 leading-relaxed">
              In Google AI Studio puoi sincronizzare ed esportare il progetto direttamente su GitHub senza dover usare la riga di comando:
            </p>
            <ol className="list-decimal list-inside text-xs space-y-1 text-slate-300 pl-1">
              <li>Clicca sul menu in alto a destra <strong>(Impostazioni / Esporta)</strong>.</li>
              <li>Seleziona <strong>"Export to GitHub"</strong>.</li>
              <li>Inserisci come nome del repository: <code className="text-emerald-400 font-bold bg-slate-900 px-1 py-0.5 rounded">crea-bot</code>.</li>
              <li>Conferma: AI Studio creerà automaticamente il repository sincronizzato sul tuo profilo GitHub!</li>
            </ol>
          </div>

          {/* Method 2: Git Push via Terminal */}
          <div className="bg-slate-950/80 border border-slate-800 rounded-xl p-4 space-y-2">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2 text-slate-200 font-semibold text-xs sm:text-sm">
                <Terminal className="w-4 h-4 text-amber-400" />
                <span>Metodo 2: Comandi Git da Terminale</span>
              </div>
              <button
                onClick={() => copyToClipboard(gitPushCommand, 1)}
                className="text-[11px] font-medium px-2 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 transition-colors flex items-center gap-1"
              >
                {copiedIndex === 1 ? <Check className="w-3 h-3 text-emerald-400" /> : <Copy className="w-3 h-3" />}
                {copiedIndex === 1 ? "Copiato!" : "Copia comandi"}
              </button>
            </div>
            <pre className="p-3 bg-slate-900 rounded-lg font-mono text-xs text-amber-300 border border-slate-800 select-all overflow-x-auto">
              {gitPushCommand}
            </pre>
          </div>

          {/* Method 3: Clone directly in Android Termux */}
          <div className="bg-slate-950/80 border border-slate-800 rounded-xl p-4 space-y-2">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2 text-slate-200 font-semibold text-xs sm:text-sm">
                <Terminal className="w-4 h-4 text-emerald-400" />
                <span>Metodo 3: Scarica e sincronizza direttamente su Termux</span>
              </div>
              <button
                onClick={() => copyToClipboard(termuxCloneCommand, 2)}
                className="text-[11px] font-medium px-2 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 transition-colors flex items-center gap-1"
              >
                {copiedIndex === 2 ? <Check className="w-3 h-3 text-emerald-400" /> : <Copy className="w-3 h-3" />}
                {copiedIndex === 2 ? "Copiato!" : "Copia comando Termux"}
              </button>
            </div>
            <p className="text-xs text-slate-400">
              Una volta creato il repository su GitHub, puoi clonarlo direttamente sul tuo smartphone Android digitando in Termux:
            </p>
            <pre className="p-3 bg-slate-900 rounded-lg font-mono text-xs text-emerald-300 border border-slate-800 select-all overflow-x-auto">
              {termuxCloneCommand}
            </pre>
          </div>
        </div>

        {/* Modal Footer */}
        <div className="p-4 border-t border-slate-800 bg-slate-950/80 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold transition-colors"
          >
            Chiudi
          </button>
        </div>
      </div>
    </div>
  );
};
