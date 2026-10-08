import React from "react";
import { X, Terminal, Smartphone, ShieldCheck, Play, Edit3, Key, AlertTriangle, CheckCircle2 } from "lucide-react";

interface TermuxGuideModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const TermuxGuideModal: React.FC<TermuxGuideModalProps> = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-in fade-in">
      <div className="bg-slate-900 border border-slate-800 w-full max-w-3xl max-h-[90vh] rounded-2xl shadow-2xl flex flex-col overflow-hidden">
        {/* Modal Header */}
        <div className="p-4 sm:p-5 border-b border-slate-800 flex items-center justify-between bg-slate-950/80">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-amber-500/20 text-amber-400 flex items-center justify-center">
              <Terminal className="w-4 h-4" />
            </div>
            <div>
              <h3 className="font-bold text-slate-100 text-sm sm:text-base">
                Guida Completa: Esecuzione e Modifica su Android con Termux
              </h3>
              <p className="text-xs text-slate-400">
                Come far funzionare i bot direttamente sul tuo smartphone e modificarli in qualsiasi momento
              </p>
            </div>
          </div>

          <button
            id="btn-close-termux-guide"
            onClick={onClose}
            className="w-8 h-8 rounded-lg bg-slate-800 text-slate-400 hover:text-slate-100 flex items-center justify-center transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-4 sm:p-6 overflow-y-auto space-y-6 text-xs sm:text-sm text-slate-300 leading-relaxed">
          {/* Step 1: Install Termux */}
          <div className="bg-slate-950/80 border border-slate-800 rounded-xl p-4 space-y-2">
            <div className="flex items-center gap-2 text-emerald-400 font-semibold text-sm">
              <CheckCircle2 className="w-4 h-4" />
              <span>Passo 1: Installa Termux su Android (da F-Droid)</span>
            </div>
            <p className="text-slate-400 text-xs">
              ⚠️ <strong>Importante:</strong> Non scaricare Termux dal Play Store (è obsoleto e dà errori di repository). Scarica la versione ufficiale aggiornata da{" "}
              <span className="text-emerald-400 font-medium">F-Droid</span> (termux.dev).
            </p>
            <div className="p-2.5 rounded-lg bg-slate-900 font-mono text-xs text-amber-300 border border-slate-800 select-all">
              pkg update && pkg upgrade -y && pkg install -y python android-tools nano termux-api
            </div>
          </div>

          {/* Step 2: Wireless Debugging (No PC required!) */}
          <div className="bg-slate-950/80 border border-slate-800 rounded-xl p-4 space-y-2.5">
            <div className="flex items-center gap-2 text-teal-400 font-semibold text-sm">
              <Smartphone className="w-4 h-4" />
              <span>Passo 2: Abilita il tocco automatico (Debug Wireless senza PC)</span>
            </div>
            <p className="text-slate-400 text-xs">
              Sui dispositivi con Android 11, 12, 13 e 14 puoi usare <strong>Debug Wireless</strong> direttamente dallo schermo diviso del telefono, senza collegare alcun computer:
            </p>
            <ol className="list-decimal list-inside text-xs space-y-1.5 text-slate-300 pl-1">
              <li>Vai in <strong>Impostazioni &gt; Opzioni Sviluppatore</strong> sul tuo smartphone.</li>
              <li>Attiva <strong>Debug Wireless</strong> ed entra nel menu dedicato.</li>
              <li>Premi <em>"Associa dispositivo con codice di accoppiamento"</em> (ti mostrerà porta e codice a 6 cifre).</li>
              <li>
                In Termux (in split screen o finestra mobile) digita:
                <div className="mt-1 p-2 rounded bg-slate-900 font-mono text-[11px] text-teal-300 border border-slate-800 select-all">
                  adb pair localhost:PORTA_ACCOPPIAMENTO
                </div>
                (Inserisci il codice a 6 cifre quando richiesto).
              </li>
              <li>
                Ora connettiti con la porta principale del Debug Wireless:
                <div className="mt-1 p-2 rounded bg-slate-900 font-mono text-[11px] text-teal-300 border border-slate-800 select-all">
                  adb connect localhost:PORTA_PRINCIPALE
                </div>
              </li>
            </ol>
            <p className="text-[11px] text-slate-500 italic">
              * Nota: Se il tuo smartphone ha permessi di <strong>Root</strong> o usi <strong>Shizuku</strong>, i comandi di input funzionano all'istante senza bisogno di ADB wireless!
            </p>
          </div>

          {/* Step 3: Put bot on phone & run */}
          <div className="bg-slate-950/80 border border-slate-800 rounded-xl p-4 space-y-2">
            <div className="flex items-center gap-2 text-amber-400 font-semibold text-sm">
              <Play className="w-4 h-4" />
              <span>Passo 3: Metti il bot su Termux ed avvialo</span>
            </div>
            <p className="text-slate-400 text-xs">
              Puoi scaricare il file <strong>.zip</strong> o il singolo file dal generatore.
              Per accedere ai file scaricati nella cartella Download del telefono:
            </p>
            <div className="p-2.5 rounded-lg bg-slate-900 font-mono text-xs text-slate-200 border border-slate-800 space-y-1 select-all">
              <div>termux-setup-storage</div>
              <div>cd ~/storage/downloads</div>
              <div>python bot.py</div>
            </div>
          </div>

          {/* Step 4: Modify directly on phone with nano */}
          <div className="bg-slate-950/80 border border-slate-800 rounded-xl p-4 space-y-2">
            <div className="flex items-center gap-2 text-emerald-400 font-semibold text-sm">
              <Edit3 className="w-4 h-4" />
              <span>Passo 4: Come modificare il bot con l'editor "nano"</span>
            </div>
            <p className="text-slate-400 text-xs">
              Non serve un PC per cambiare le coordinate o la velocità! Modifica i parametri direttamente dentro Termux:
            </p>
            <div className="p-2.5 rounded-lg bg-slate-900 font-mono text-xs text-slate-200 border border-slate-800 select-all">
              nano config.json &nbsp;&nbsp;&nbsp;# oppure: nano bot.py
            </div>
            <ul className="text-xs space-y-1 text-slate-300 pl-2">
              <li>
                💾 <strong>Per Salvare le modifiche:</strong> Premi <kbd className="px-1.5 py-0.5 rounded bg-slate-800 border border-slate-700 font-mono">Ctrl + O</kbd> e poi premi <kbd className="px-1.5 py-0.5 rounded bg-slate-800 border border-slate-700 font-mono">Invio</kbd>.
              </li>
              <li>
                🚪 <strong>Per Uscire dall'editor:</strong> Premi <kbd className="px-1.5 py-0.5 rounded bg-slate-800 border border-slate-700 font-mono">Ctrl + X</kbd>.
              </li>
            </ul>
          </div>

          {/* Step 5: Stop & Emergency Fail-Safe */}
          <div className="bg-red-950/30 border border-red-900/40 rounded-xl p-4 space-y-2">
            <div className="flex items-center gap-2 text-red-400 font-semibold text-sm">
              <AlertTriangle className="w-4 h-4" />
              <span>Arresto di Emergenza (Kill Switch)</span>
            </div>
            <p className="text-xs text-slate-300">
              Per fermare il bot in qualsiasi istante:
            </p>
            <ul className="list-disc list-inside text-xs text-slate-300 space-y-1 pl-1">
              <li>
                Torna su Termux e premi contemporaneamente <kbd className="px-1.5 py-0.5 rounded bg-slate-800 border border-slate-700 font-mono">Ctrl + C</kbd>.
              </li>
              <li>
                Oppure se hai uno schermo diviso, digita in un'altra scheda: <code className="text-red-300 font-mono">touch /sdcard/stop_bot</code> (il bot si fermerà al ciclo successivo).
              </li>
            </ul>
          </div>
        </div>

        {/* Modal Footer */}
        <div className="p-4 border-t border-slate-800 bg-slate-950/80 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold transition-colors"
          >
            Ho Capito, Chiudi Guida
          </button>
        </div>
      </div>
    </div>
  );
};
