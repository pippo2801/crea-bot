import React, { useState, useRef } from "react";
import { Crosshair, Plus, Trash2, Smartphone, ArrowRight, Check } from "lucide-react";
import { CoordinatePoint } from "../types";

interface ScreenCoordinateHelperProps {
  resolution: string; // e.g. "1080x2400"
  orientation: "portrait" | "landscape";
  onAddCoordinate: (point: CoordinatePoint) => void;
  coordinates: CoordinatePoint[];
  onRemoveCoordinate: (id: string) => void;
  onClearCoordinates: () => void;
}

export const ScreenCoordinateHelper: React.FC<ScreenCoordinateHelperProps> = ({
  resolution,
  orientation,
  onAddCoordinate,
  coordinates,
  onRemoveCoordinate,
  onClearCoordinates,
}) => {
  // Parse target resolution
  const [resW, resH] = resolution.split("x").map((n) => parseInt(n, 10) || 1080);
  const targetWidth = orientation === "portrait" ? resW : resH;
  const targetHeight = orientation === "portrait" ? resH : resW;

  const [hoverCoord, setHoverCoord] = useState<{ x: number; y: number } | null>(null);
  const [activePointName, setActivePointName] = useState("Pulsante Gioco");
  const [pointDelay, setPointDelay] = useState(1.5);
  const [copiedCoords, setCopiedCoords] = useState(false);

  const screenRef = useRef<HTMLDivElement>(null);

  const handleScreenClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!screenRef.current) return;
    const rect = screenRef.current.getBoundingClientRect();
    const clickX = e.clientX - rect.left;
    const clickY = e.clientY - rect.top;

    // Scale to target device resolution
    const actualX = Math.round((clickX / rect.width) * targetWidth);
    const actualY = Math.round((clickY / rect.height) * targetHeight);

    const newPoint: CoordinatePoint = {
      id: "coord_" + Date.now() + "_" + Math.random().toString(36).substring(2, 5),
      name: `${activePointName} #${coordinates.length + 1}`,
      x: actualX,
      y: actualY,
      delaySec: pointDelay,
      actionType: "tap",
    };

    onAddCoordinate(newPoint);
  };

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!screenRef.current) return;
    const rect = screenRef.current.getBoundingClientRect();
    const mouseX = Math.max(0, Math.min(rect.width, e.clientX - rect.left));
    const mouseY = Math.max(0, Math.min(rect.height, e.clientY - rect.top));

    const scaledX = Math.round((mouseX / rect.width) * targetWidth);
    const scaledY = Math.round((mouseY / rect.height) * targetHeight);

    setHoverCoord({ x: scaledX, y: scaledY });
  };

  const copyAdBCommand = () => {
    if (coordinates.length === 0) return;
    const script = coordinates
      .map((c) => `input tap ${c.x} ${c.y} # ${c.name}\nsleep ${c.delaySec}`)
      .join("\n");
    navigator.clipboard.writeText(script);
    setCopiedCoords(true);
    setTimeout(() => setCopiedCoords(false), 2000);
  };

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 shadow-xl">
      <div className="flex items-center justify-between mb-4 flex-wrap gap-2">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-teal-500/20 flex items-center justify-center text-teal-400">
            <Crosshair className="w-4 h-4" />
          </div>
          <div>
            <h3 className="font-semibold text-slate-100 text-sm">
              Simulatore Schermo & Rilevatore Coordinate (X, Y)
            </h3>
            <p className="text-xs text-slate-400">
              Clicca sullo schermo virtuale per calcolare le coordinate esatte per il tuo bot Android
            </p>
          </div>
        </div>

        <div className="text-xs px-2.5 py-1 rounded-lg bg-slate-950 border border-slate-800 text-slate-300 font-mono">
          Risoluzione target: <span className="text-teal-400">{targetWidth}x{targetHeight}</span> ({orientation})
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* Interactive Virtual Smartphone Display */}
        <div className="lg:col-span-6 flex flex-col items-center justify-center">
          <div className="relative p-3 rounded-[32px] bg-slate-950 border-4 border-slate-800 shadow-2xl shadow-black/60">
            {/* Phone Speaker Notch */}
            <div className="w-16 h-1 bg-slate-800 rounded-full mx-auto mb-2" />

            {/* Screen Canvas Container */}
            <div
              ref={screenRef}
              onClick={handleScreenClick}
              onMouseMove={handleMouseMove}
              onMouseLeave={() => setHoverCoord(null)}
              className={`relative cursor-crosshair overflow-hidden rounded-2xl bg-gradient-to-b from-slate-900 via-slate-950 to-slate-900 border border-slate-700/60 shadow-inner select-none transition-all ${
                orientation === "portrait"
                  ? "w-[240px] h-[480px] sm:w-[280px] sm:h-[560px]"
                  : "w-[360px] h-[200px] sm:w-[460px] sm:h-[260px]"
              }`}
            >
              {/* Subtle grid lines */}
              <div
                className="absolute inset-0 opacity-15 pointer-events-none"
                style={{
                  backgroundImage:
                    "linear-gradient(to right, #38bdf8 1px, transparent 1px), linear-gradient(to bottom, #38bdf8 1px, transparent 1px)",
                  backgroundSize: "30px 30px",
                }}
              />

              {/* Game mockup elements */}
              <div className="absolute inset-0 flex flex-col justify-between p-4 pointer-events-none opacity-40">
                <div className="flex justify-between items-center text-[10px] text-slate-400 font-mono">
                  <span>LIVE GAME DISPLAY</span>
                  <span>FPS: 60</span>
                </div>
                <div className="flex justify-center items-center">
                  <span className="text-xs text-slate-500 font-medium bg-slate-900/80 px-3 py-1.5 rounded-full border border-slate-800">
                    Clicca per posizionare un tocco (Tap)
                  </span>
                </div>
                <div className="flex justify-between items-center text-[10px] text-slate-500">
                  <span>Android ADB Input</span>
                  <span>{resolution}</span>
                </div>
              </div>

              {/* Existing Recorded Coordinate Markers */}
              {coordinates.map((coord, idx) => {
                const leftPercent = (coord.x / targetWidth) * 100;
                const topPercent = (coord.y / targetHeight) * 100;

                return (
                  <div
                    key={coord.id}
                    style={{ left: `${leftPercent}%`, top: `${topPercent}%` }}
                    className="absolute -translate-x-1/2 -translate-y-1/2 z-10 group"
                  >
                    <div className="w-6 h-6 rounded-full bg-teal-500 text-slate-950 font-bold text-[10px] flex items-center justify-center border-2 border-white shadow-lg animate-pulse">
                      {idx + 1}
                    </div>
                    {/* Tooltip on hover */}
                    <div className="absolute left-1/2 -translate-x-1/2 bottom-full mb-1 opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none bg-slate-950 border border-slate-700 px-2 py-0.5 rounded text-[10px] text-slate-200 whitespace-nowrap shadow-xl">
                      {coord.name} ({coord.x}, {coord.y})
                    </div>
                  </div>
                );
              })}

              {/* Live Mouse Coordinates Crosshair Preview */}
              {hoverCoord && (
                <div className="absolute bottom-2 left-2 right-2 bg-slate-950/90 border border-teal-500/40 rounded-lg py-1 px-2.5 text-center text-xs font-mono text-teal-300 pointer-events-none shadow-md">
                  Coordinate live: X: <span className="font-bold">{hoverCoord.x}</span>, Y:{" "}
                  <span className="font-bold">{hoverCoord.y}</span>
                </div>
              )}
            </div>

            {/* Bottom Home Indicator */}
            <div className="w-24 h-1 bg-slate-800 rounded-full mx-auto mt-3" />
          </div>
        </div>

        {/* Coordinate Config & Sequence List */}
        <div className="lg:col-span-6 space-y-4">
          <div className="bg-slate-950/60 border border-slate-800 rounded-xl p-4 space-y-3">
            <h4 className="text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Parametri prossimo tocco
            </h4>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-[11px] text-slate-400 block mb-1">Nome punto / etichetta</label>
                <input
                  type="text"
                  id="input-coord-name"
                  value={activePointName}
                  onChange={(e) => setActivePointName(e.target.value)}
                  placeholder="es. Pulsante Attacco"
                  className="w-full bg-slate-900 border border-slate-800 rounded-lg px-2.5 py-1.5 text-xs text-slate-100"
                />
              </div>

              <div>
                <label className="text-[11px] text-slate-400 block mb-1">Attesa dopo tocco (sec)</label>
                <input
                  type="number"
                  step="0.1"
                  min="0.1"
                  max="60"
                  id="input-coord-delay"
                  value={pointDelay}
                  onChange={(e) => setPointDelay(parseFloat(e.target.value) || 1)}
                  className="w-full bg-slate-900 border border-slate-800 rounded-lg px-2.5 py-1.5 text-xs text-slate-100"
                />
              </div>
            </div>

            <p className="text-[11px] text-slate-400">
              👉 Clicca su un punto dello smartphone a sinistra per salvare automaticamente le coordinate scalate alla risoluzione reale del tuo dispositivo.
            </p>
          </div>

          {/* List of recorded points */}
          <div className="bg-slate-950/60 border border-slate-800 rounded-xl p-4">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-semibold text-slate-300">
                Punti registrati ({coordinates.length})
              </span>
              {coordinates.length > 0 && (
                <div className="flex items-center gap-2">
                  <button
                    onClick={copyAdBCommand}
                    className="text-[11px] text-teal-400 hover:underline flex items-center gap-1"
                  >
                    {copiedCoords ? <Check className="w-3 h-3 text-emerald-400" /> : null}
                    Copia comandi ADB
                  </button>
                  <button
                    onClick={onClearCoordinates}
                    className="text-[11px] text-red-400 hover:underline"
                  >
                    Svuota lista
                  </button>
                </div>
              )}
            </div>

            {coordinates.length === 0 ? (
              <div className="py-6 text-center text-xs text-slate-500">
                Nessun punto registrato. Fai clic sullo schermo per aggiungere il primo tocco!
              </div>
            ) : (
              <div className="space-y-1.5 max-h-48 overflow-y-auto pr-1">
                {coordinates.map((item, idx) => (
                  <div
                    key={item.id}
                    className="flex items-center justify-between p-2 rounded-lg bg-slate-900 border border-slate-800/80 text-xs"
                  >
                    <div className="flex items-center gap-2">
                      <span className="w-5 h-5 rounded-full bg-teal-500/20 text-teal-400 font-bold text-[10px] flex items-center justify-center">
                        {idx + 1}
                      </span>
                      <span className="font-medium text-slate-200">{item.name}</span>
                      <span className="font-mono text-teal-300 text-[11px]">
                        ({item.x}, {item.y})
                      </span>
                    </div>

                    <div className="flex items-center gap-3">
                      <span className="text-[10px] text-slate-400 font-mono">+{item.delaySec}s</span>
                      <button
                        onClick={() => onRemoveCoordinate(item.id)}
                        className="text-slate-500 hover:text-red-400 transition-colors"
                        title="Rimuovi"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
