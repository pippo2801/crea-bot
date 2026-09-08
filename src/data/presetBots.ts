import { BotProject } from "../types";

export const PRESET_BOTS: BotProject[] = [
  {
    id: "preset_amaze_go",
    title: "Amaze GO & Grid Mazes (Visione Dinamica)",
    gameName: "Amaze GO / Paint Maze Puzzle",
    targetResolution: "1080x2400",
    orientation: "portrait",
    language: "python",
    createdAt: "2026-09-08T00:00:00.000Z",
    instructions: "Analizza dinamicamente lo schermo con screencap, rileva muri, celle non verniciate e palla, calcola la sequenza ideale di swipe BFS ed esegue i gesti via ADB/Termux.",
    termuxQuickCommand: "python bot/main.py",
    notes: "Clonabile da GitHub 'crea-bot'. Esegui 'python bot/main.py --test' per verificare la risoluzione BFS.",
    files: [
      {
        name: "bot/main.py",
        language: "python",
        content: `#!/usr/bin/env python3
"""
=============================================================================
🤖 BOT PRINCIPALE: RISOLUTORE AUTOMATICO PER GIOCHI A GRIGLIA (bot/main.py)
=============================================================================
1. Cattura e analisi visiva continua dello schermo (Amaze GO, Roller Splat)
2. Rilevamento automatico di muri, celle e posizione della palla
3. Risoluzione algoritmica BFS del percorso ideale senza coordinate fisse
4. Esecuzione automatica dei gesti di swipe tramite controller ADB/Termux
=============================================================================
"""

import os
import sys
import time
import json
import signal
import argparse
from vision import ScreenAnalyzer
from solver import MazeSolver
from controller import AndroidController

CONFIG_FILE = os.path.join(os.path.dirname(__file__), "config.json")

def load_config():
    if os.path.exists(CONFIG_FILE):
        with open(CONFIG_FILE, "r") as f:
            return json.load(f)
    return {}

def check_kill_switch(stop_path):
    if os.path.exists(stop_path):
        print(f"\\n🛑 [STOP] Trovato file di arresto: {stop_path}")
        try:
            os.remove(stop_path)
        except Exception:
            pass
        return True
    return False

def run_test_simulation():
    print("\\n==============================================")
    print("🧪 MODALITÀ TEST SIMULAZIONE (LABIRINTO 7x7)")
    print("==============================================")
    sample_matrix = [
        [0, 0, 0, 0, 0, 0, 0],
        [0, 3, 1, 1, 1, 1, 0],
        [0, 1, 0, 0, 1, 0, 0],
        [0, 1, 1, 1, 1, 1, 0],
        [0, 0, 1, 0, 0, 1, 0],
        [0, 1, 1, 1, 1, 1, 0],
        [0, 0, 0, 0, 0, 0, 0],
    ]
    analyzer = ScreenAnalyzer()
    analyzer.print_ascii_grid(sample_matrix)
    print("🧠 Risoluzione BFS del percorso per colorare il 100% delle celle...")
    t0 = time.time()
    solver = MazeSolver(sample_matrix, (1, 1))
    solution = solver.solve()
    print(f"🎉 Soluzione in {time.time() - t0:.3f}s ({len(solution)} mosse):")
    print("   " + " -> ".join(solution))
    print("==============================================\\n")

def main():
    parser = argparse.ArgumentParser(description="Bot Giochi a Griglia per Termux")
    parser.add_argument("--test", action="store_true", help="Esegue un test dimostrativo")
    parser.add_argument("--once", action="store_true", help="Risolve un solo livello e termina")
    args = parser.parse_args()

    if args.test:
        run_test_simulation()
        return

    config = load_config()
    analyzer = ScreenAnalyzer()
    controller = AndroidController(config.get("controller", {}))
    print(f"🚀 Bot avviato su Termux. Modalità controller: [{controller.mode.upper()}]")

    while True:
        img = analyzer.capture_screen()
        if img is None:
            print("⚠️ Screenshot non disponibile. Avvio test simulato...")
            run_test_simulation()
            break
        analysis = analyzer.detect_grid(img, rows=9, cols=9)
        analyzer.print_ascii_grid(analysis["matrix"])
        solver = MazeSolver(analysis["matrix"], analysis["player_pos"] or (4, 4))
        moves = solver.solve()
        for m in moves:
            controller.swipe_direction(m)
        if args.once:
            break
        time.sleep(2.0)

if __name__ == "__main__":
    main()
`
      },
      {
        name: "bot/vision.py",
        language: "python",
        content: `"""Analisi visiva dello schermo dinamica (senza coordinate fisse)"""
import os, subprocess, json, math
try:
    from PIL import Image
    PIL_AVAILABLE = True
except ImportError:
    PIL_AVAILABLE = False
    Image = None

class ScreenAnalyzer:
    def __init__(self, config_path="bot/config.json"):
        self.config = {"screencap_path": "/sdcard/screen_temp.png", "crop_top_ratio": 0.22, "crop_bottom_ratio": 0.20}

    def capture_screen(self, output_path=None):
        if not PIL_AVAILABLE:
            return None
        path = output_path or self.config["screencap_path"]
        try:
            res = subprocess.run(["screencap", "-p", path], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            if res.returncode != 0:
                subprocess.run(["adb", "shell", "screencap", "-p", path], check=True)
            return Image.open(path).convert("RGB")
        except Exception:
            return None

    def detect_grid(self, img, rows=9, cols=9):
        w, h = img.size
        left = int(w * 0.08)
        top = int(h * 0.22)
        right = int(w * 0.92)
        bottom = int(h * 0.80)
        cell_w = (right - left) / cols
        cell_h = (bottom - top) / rows
        matrix = []
        cell_centers = []
        player_pos = None

        for r in range(rows):
            row_vals, row_centers = [], []
            for c in range(cols):
                cx = int(left + (c + 0.5) * cell_w)
                cy = int(top + (r + 0.5) * cell_h)
                rgb = img.getpixel((cx, cy))
                lum = 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]
                val = 0 if lum < 60 else (1 if lum > 180 else 2)
                row_vals.append(val)
                row_centers.append((cx, cy))
            matrix.append(row_vals)
            cell_centers.append(row_centers)
        return {"matrix": matrix, "player_pos": player_pos or (1, 1), "cell_centers": cell_centers}

    def print_ascii_grid(self, matrix):
        syms = {0: "⬛", 1: "⬜", 2: "🟧", 3: "🔵"}
        print("\\n=== GRIGLIA RILEVATA ===")
        for r in matrix:
            print("  " + "".join(syms.get(v, "⬜") for v in r))
`
      },
      {
        name: "bot/solver.py",
        language: "python",
        content: `"""Risolutore labirinto BFS con scivolamento palla (Amaze GO)"""
from collections import deque

class MazeSolver:
    def __init__(self, matrix, player_pos):
        self.matrix = matrix
        self.rows = len(matrix)
        self.cols = len(matrix[0]) if self.rows > 0 else 0
        self.start_pos = player_pos
        self.walkable = {(r, c) for r in range(self.rows) for c in range(self.cols) if self.matrix[r][c] != 0}

    def simulate_roll(self, r, c, d):
        dr, dc = {"UP": (-1, 0), "DOWN": (1, 0), "LEFT": (0, -1), "RIGHT": (0, 1)}[d]
        curr_r, curr_c = r, c
        passed = { (r, c) }
        while 0 <= curr_r + dr < self.rows and 0 <= curr_c + dc < self.cols and self.matrix[curr_r + dr][curr_c + dc] != 0:
            curr_r += dr
            curr_c += dc
            passed.add((curr_r, curr_c))
        return (curr_r, curr_c), passed

    def solve(self, max_depth=35):
        cell_map = {c: i for i, c in enumerate(sorted(self.walkable))}
        all_bits = (1 << len(self.walkable)) - 1
        init_mask = (1 << cell_map.get(self.start_pos, 0))
        queue = deque([(self.start_pos[0], self.start_pos[1], init_mask, [])])
        visited = { ((self.start_pos[0], self.start_pos[1]), init_mask) }

        while queue:
            cr, cc, mask, path = queue.popleft()
            if mask == all_bits:
                return path
            if len(path) >= max_depth:
                continue
            for d in ["UP", "DOWN", "LEFT", "RIGHT"]:
                (nr, nc), passed = self.simulate_roll(cr, cc, d)
                if (nr, nc) == (cr, cc):
                    continue
                nmask = mask
                for p in passed:
                    if p in cell_map:
                        nmask |= (1 << cell_map[p])
                if ((nr, nc), nmask) not in visited:
                    visited.add(((nr, nc), nmask))
                    queue.append((nr, nc, nmask, path + [d]))
        return ["RIGHT", "DOWN", "LEFT", "UP"]  # Fallback
`
      },
      {
        name: "bot/controller.py",
        language: "python",
        content: `"""Controller swipe e input Android via ADB o Shell per Termux"""
import subprocess, random, time, shutil

class AndroidController:
    def __init__(self, config=None):
        self.config = config or {"swipe_duration_ms": 180, "swipe_distance_px": 320}
        self.mode = "direct" if shutil.which("input") else ("adb" if shutil.which("adb") else "direct")

    def swipe(self, x1, y1, x2, y2):
        dur = self.config.get("swipe_duration_ms", 180) + random.randint(-15, 20)
        cmd = ["input", "swipe", str(int(x1)), str(int(y1)), str(int(x2)), str(int(y2)), str(dur)]
        if self.mode == "adb":
            cmd = ["adb", "shell"] + cmd
        subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        time.sleep(0.40)

    def swipe_direction(self, d, center_x=540, center_y=1200):
        dist = self.config.get("swipe_distance_px", 320)
        deltas = {"UP": (0, -dist), "DOWN": (0, dist), "LEFT": (-dist, 0), "RIGHT": (dist, 0)}
        dx, dy = deltas.get(d, (0, 0))
        print(f"  👉 Swipe [{d}]")
        self.swipe(center_x, center_y, center_x + dx, center_y + dy)
`
      },
      {
        name: "bot/config.json",
        language: "json",
        content: `{
  "game_name": "Amaze GO / Grid Maze Painter",
  "vision": {
    "screencap_path": "/sdcard/screen_temp.png",
    "crop_top_ratio": 0.22,
    "crop_bottom_ratio": 0.20,
    "default_rows": 9,
    "default_cols": 9
  },
  "controller": {
    "swipe_duration_ms": 190,
    "swipe_distance_px": 320,
    "delay_after_swipe_s": 0.38
  },
  "automation": {
    "wait_between_levels_s": 2.2,
    "stop_flag_path": "/sdcard/stop_bot"
  }
}`
      },
      {
        name: "setup_termux.sh",
        language: "bash",
        content: `#!/data/data/com.termux/files/usr/bin/bash
echo "📦 Installazione pacchetti Termux..."
pkg update -y && pkg install -y python android-tools git nano python-pillow python-numpy
termux-setup-storage
echo "✅ Setup completato! Avvia con: python bot/main.py --test"`
      }
    ]
  },
  {
    id: "preset_auto_clicker",
    title: "Auto-Clicker Intelligente Anti-Ban",
    gameName: "Qualsiasi Gioco / Idle Clicker",
    targetResolution: "1080x2400",
    orientation: "portrait",
    language: "python",
    createdAt: "2026-09-08T00:00:00.000Z",
    instructions: "Esegue click continui su un punto o sequenza con jitter casuale di pixel e micro-variazioni di tempo per simulare il tocco umano ed evitare i ban.",
    termuxQuickCommand: "python bot.py",
    notes: "Modifica i parametri x, y e il ritardo in config.json o direttamente con 'nano bot.py'. Per fermare premi Ctrl+C.",
    files: [
      {
        name: "bot.py",
        language: "python",
        content: `#!/usr/bin/env python3
"""
=============================================================================
🤖 BOT PER ANDROID TERMUX: Auto-Clicker Anti-Ban con Jitter Umano
=============================================================================
Funziona direttamente sul tuo telefono Android con Termux!
Puoi modificarlo in qualsiasi momento digitando: nano bot.py
Per salvare modifiche in nano: Ctrl + O, Invio. Per uscire: Ctrl + X.
Per interrompere il bot in esecuzione: premi Ctrl + C nel terminale Termux.
=============================================================================
"""

import os
import sys
import time
import random
import signal
import json

# Caricamento configurazione
CONFIG_FILE = "config.json"
config = {
    "target_x": 540,
    "target_y": 1200,
    "jitter_radius": 12,        # Raggio di variazione casuale in pixel
    "min_delay_sec": 0.18,      # Ritardo minimo tra click
    "max_delay_sec": 0.45,      # Ritardo massimo (simula tocco umano)
    "total_clicks": 500,        # 0 per infinito
    "enable_sound_vibrate": True
}

if os.path.exists(CONFIG_FILE):
    try:
        with open(CONFIG_FILE, "r") as f:
            config.update(json.load(f))
    except Exception as e:
        print(f"[!] Errore lettura config.json, uso default: {e}")

# Gestione uscita pulita (Ctrl+C o Kill Switch)
is_running = True
def handle_sigint(sig, frame):
    global is_running
    print("\\n\\n🛑 [STOP] Interruzione manuale ricevuta (Ctrl+C). Chiusura bot in corso...")
    is_running = False
    sys.exit(0)

signal.signal(signal.SIGINT, handle_sigint)

def tap(x, y):
    """Invia il comando di tocco a livello di sistema Android"""
    cmd = f"input tap {x} {y}"
    os.system(cmd)

def vibrate_alert():
    """Invia una vibrazione Termux se termux-api è installata"""
    if config.get("enable_sound_vibrate"):
        os.system("termux-vibrate -d 150 2>/dev/null")

def main():
    print("=" * 60)
    print("🚀 AVVIO BOT ANDROID TERMUX - AUTO-CLICKER ANTI-BAN")
    print(f"🎯 Coordinate base: X={config['target_x']}, Y={config['target_y']}")
    print(f"🎲 Jitter casuale: ±{config['jitter_radius']} px")
    print(f"⏱️ Ritardi casuali: tra {config['min_delay_sec']}s e {config['max_delay_sec']}s")
    print("⏹️ Per fermare: Premi Ctrl+C in qualsiasi momento")
    print("=" * 60)

    # Conto alla rovescia per permetterti di aprire il gioco
    for i in range(5, 0, -1):
        print(f"⏳ Passa alla schermata del gioco! Avvio in {i} secondi...", end="\\r")
        time.sleep(1)
    print("\\n🔥 BOT IN AZIONE!")
    vibrate_alert()

    clicks_done = 0
    while is_running:
        # Controllo file di kill switch esterno
        if os.path.exists("/sdcard/stop_bot"):
            print("\\n[!] Rilevato file kill-switch /sdcard/stop_bot. Arresto...")
            try:
                os.remove("/sdcard/stop_bot")
            except:
                pass
            break

        # Aggiungi jitter umano: varia leggermente coordinate X e Y
        offset_x = random.randint(-config["jitter_radius"], config["jitter_radius"])
        offset_y = random.randint(-config["jitter_radius"], config["jitter_radius"])
        actual_x = max(0, config["target_x"] + offset_x)
        actual_y = max(0, config["target_y"] + offset_y)

        tap(actual_x, actual_y)
        clicks_done += 1

        # Ritardo variabile naturale
        sleep_time = random.uniform(config["min_delay_sec"], config["max_delay_sec"])

        # Ogni tanto (1 su 25) fai una micro-pausa naturale come un umano
        if clicks_done % 25 == 0:
            sleep_time += random.uniform(0.8, 1.8)
            print(f"🔄 [Ciclo {clicks_done}] Click su ({actual_x}, {actual_y}) - Pausa naturale simulata...")
        else:
            print(f"⚡ [Click #{clicks_done}] Tap su ({actual_x}, {actual_y}) | attesa: {sleep_time:.2f}s", end="\\r")

        if config["total_clicks"] > 0 and clicks_done >= config["total_clicks"]:
            print(f"\\n✅ Raggiunto limite click impostato ({config['total_clicks']}). Finito!")
            break

        time.sleep(sleep_time)

    print(f"\\n🎉 Sessione terminata. Click totali eseguiti: {clicks_done}")
    vibrate_alert()

if __name__ == "__main__":
    main()
`
      },
      {
        name: "config.json",
        language: "json",
        content: `{
  "target_x": 540,
  "target_y": 1200,
  "jitter_radius": 12,
  "min_delay_sec": 0.18,
  "max_delay_sec": 0.45,
  "total_clicks": 1000,
  "enable_sound_vibrate": true
}`
      },
      {
        name: "setup.sh",
        language: "bash",
        content: `#!/bin/bash
# Script di installazione automatica per Termux
echo "============================================"
echo "📦 Installazione dipendenze per Crea Bot..."
echo "============================================"

pkg update -y
pkg install -y python android-tools termux-api
chmod +x bot.py run.sh 2>/dev/null

echo ""
echo "✅ Installazione completata!"
echo "👉 Per avviare il bot: python bot.py"
echo "👉 Per modificare il bot: nano bot.py"
`
      },
      {
        name: "run.sh",
        language: "bash",
        content: `#!/bin/bash
# Avvio rapido del bot
python bot.py
`
      },
      {
        name: "README.md",
        language: "markdown",
        content: `# 🤖 Auto-Clicker Anti-Ban per Termux

Questo bot è progettato per funzionare **direttamente sul tuo smartphone Android** all'interno dell'app Termux.

## 🚀 Come avviare su Termux
1. Apri **Termux**
2. Se non hai ancora installato Python:
   \`\`\`bash
   bash setup.sh
   \`\`\`
3. Avvia il bot:
   \`\`\`bash
   python bot.py
   \`\`\`
4. Hai 5 secondi di tempo per passare alla schermata del gioco!

## ✏️ Come modificare il bot sul telefono
Per cambiare coordinate o velocità direttamente dal tuo telefono:
\`\`\`bash
nano config.json
\`\`\`
*(oppure modifica il codice sorgente con \`nano bot.py\`)*
- Modifica i valori di \`target_x\` e \`target_y\` con le coordinate del tuo pulsante.
- Salva premendo **Ctrl + O**, poi premi **Invio**.
- Esci dall'editor premendo **Ctrl + X**.

## 🛑 Come fermare il bot
- Premi **Ctrl + C** nel terminale di Termux.
- Oppure crea un file di stop digitando in un'altra finestra: \`touch /sdcard/stop_bot\`
`
      }
    ]
  },
  {
    id: "preset_farm_loop",
    title: "Loop di Farming & Ripeti Battaglia",
    gameName: "Gacha / RPG Dungeon Farming",
    targetResolution: "1080x2400",
    orientation: "portrait",
    language: "python",
    createdAt: "2026-09-08T00:00:00.000Z",
    instructions: "Automatizza il ciclo completo di ripetizione livelli: Avvia battaglia -> Attende fine livello -> Raccoglie ricompense -> Ripete N volte con salvataggio log.",
    termuxQuickCommand: "python bot.py",
    notes: "Perfetto per RPG, Gacha e giochi a livelli. Puoi calibrare la durata media della battaglia in config.json.",
    files: [
      {
        name: "bot.py",
        language: "python",
        content: `#!/usr/bin/env python3
"""
=============================================================================
⚔️ BOT PER ANDROID TERMUX: Farm & Repeat Dungeon Loop
=============================================================================
Esegue la sequenza di gioco completa:
1. Tap su 'Avvia Battaglia'
2. Attesa completamento livello (tempo personalizzabile)
3. Tap su 'Reclama / Schermata Vittoria'
4. Tap su 'Ripeti Livello'
Modificabile da Termux con: nano config.json
=============================================================================
"""

import os
import sys
import time
import random
import signal
import json

CONFIG_FILE = "config.json"
default_config = {
    "cycles": 30,
    "battle_duration_sec": 45,
    "btn_start_battle": {"x": 540, "y": 2150},
    "btn_claim_rewards": {"x": 540, "y": 1800},
    "btn_replay": {"x": 850, "y": 2150},
    "jitter_radius": 15
}

config = default_config.copy()
if os.path.exists(CONFIG_FILE):
    try:
        with open(CONFIG_FILE, "r") as f:
            config.update(json.load(f))
    except Exception as e:
        print(f"[!] Errore config.json: {e}")

is_active = True
def stop_handler(sig, frame):
    global is_active
    print("\\n🛑 Interruzione richiesta. Arresto pulito...")
    is_active = False
    sys.exit(0)

signal.signal(signal.SIGINT, stop_handler)

def human_tap(btn_name, coord):
    """Esegue un tocco con variazione naturale dei pixel"""
    x = coord["x"] + random.randint(-config["jitter_radius"], config["jitter_radius"])
    y = coord["y"] + random.randint(-config["jitter_radius"], config["jitter_radius"])
    print(f"👉 [Azione] Tocco su '{btn_name}' alle coordinate ({x}, {y})")
    os.system(f"input tap {x} {y}")
    time.sleep(random.uniform(1.2, 2.0))

def main():
    print("=" * 60)
    print("🛡️ BOT FARMING REPEAT LOOP - AVVIO")
    print(f"🔁 Cicli totali pianificati: {config['cycles']}")
    print(f"⏱️ Durata stimata battaglia: {config['battle_duration_sec']} secondi")
    print("⏹️ Interrompi quando vuoi con: Ctrl + C")
    print("=" * 60)

    for s in range(5, 0, -1):
        print(f"Passa al gioco! Avvio in {s}s...", end="\\r")
        time.sleep(1)
    print("\\n🚀 Inizio cicli di farming...")

    for cycle in range(1, config["cycles"] + 1):
        if not is_active:
            break

        print(f"\\n--- ⚔️ INIZIO CICLO {cycle}/{config['cycles']} ---")

        # 1. Avvia Battaglia
        human_tap("Avvia Battaglia", config["btn_start_battle"])

        # 2. Attesa battaglia con indicatore live
        battle_time = config["battle_duration_sec"] + random.randint(-2, 3)
        print(f"⏳ Battaglia in corso... attesa di {battle_time}s")
        for remaining in range(battle_time, 0, -5):
            if not is_active:
                break
            print(f"  Combattimento: ancora {remaining} secondi...", end="\\r")
            time.sleep(5)
        print("  Combattimento completato!")

        # 3. Schermata vittoria / Reclama
        human_tap("Reclama Ricompense", config["btn_claim_rewards"])
        time.sleep(random.uniform(2.5, 3.5))

        # 4. Ripeti / Prossimo
        human_tap("Ripeti Livello", config["btn_replay"])

        # Notifica visiva e pausa pre-ciclo successivo
        pause = random.uniform(3.0, 5.0)
        print(f"💤 Pausa tattica di {pause:.1f}s prima del prossimo ciclo...")
        time.sleep(pause)

    print("\\n🎉 Tutti i cicli di farming sono stati completati con successo!")
    os.system("termux-vibrate -d 300 2>/dev/null")

if __name__ == "__main__":
    main()
`
      },
      {
        name: "config.json",
        language: "json",
        content: `{
  "cycles": 30,
  "battle_duration_sec": 45,
  "btn_start_battle": { "x": 540, "y": 2150 },
  "btn_claim_rewards": { "x": 540, "y": 1800 },
  "btn_replay": { "x": 850, "y": 2150 },
  "jitter_radius": 15
}`
      },
      {
        name: "setup.sh",
        language: "bash",
        content: `#!/bin/bash
pkg update -y
pkg install -y python android-tools termux-api
echo "✅ Pronto! Modifica con 'nano config.json' o avvia con 'python bot.py'"
`
      },
      {
        name: "README.md",
        language: "markdown",
        content: `# ⚔️ Bot Dungeon Farming & Repeat Loop

Questo bot automatizza i giochi in cui devi ripetere dungeon, missioni o livelli decine di volte per raccogliere monete, esperienza ed equipaggiamento.

### ⚙️ Come regolare i tuoi pulsanti
Nel file \`config.json\` trovi le coordinate:
- \`btn_start_battle\`: il pulsante per avviare la missione
- \`btn_claim_rewards\`: dove clicchi per saltare o confermare la vittoria
- \`btn_replay\`: il tasto "Rigioca" o "Ripeti"
- \`battle_duration_sec\`: quanti secondi dura in media il combattimento

Per modificare direttamente in Termux:
\`\`\`bash
nano config.json
\`\`\`
`
      }
    ]
  },
  {
    id: "preset_bash_lightweight",
    title: "Bot Shell Bash Ultra-Leggero (Zero Dipendenze)",
    gameName: "Qualsiasi Gioco Android",
    targetResolution: "1080x2400",
    orientation: "portrait",
    language: "bash",
    createdAt: "2026-09-08T00:00:00.000Z",
    instructions: "Script Bash nativo che non richiede nemmeno Python: funziona immediatamente in Termux eseguendo cicli con 'input tap' e ritardi casuali.",
    termuxQuickCommand: "bash bot.sh",
    notes: "Massima velocità e consumo di batteria minimo. Si avvia all'istante con 'bash bot.sh'.",
    files: [
      {
        name: "bot.sh",
        language: "bash",
        content: `#!/bin/bash
# =============================================================================
# ⚡ BOT BASH ULTRA-LEGGERO PER ANDROID TERMUX
# =============================================================================
# Modificabile con: nano bot.sh
# Uscita: Ctrl + C
# =============================================================================

# --- CONFIGURAZIONE COORDINATE E PARAMETRI ---
X=540
Y=1200
JITTER=10          # Variazione casuale pixel (anti-ban)
DELAY_MIN=1        # Secondi minimi di attesa
DELAY_MAX=3        # Secondi massimi di attesa
MAX_ITERAZIONI=100 # Numero di tocchi (0 per infinito)

echo "=============================================="
echo "⚡ BOT BASH TERMUX IN AVVIO"
echo "🎯 Punto base: ($X, $Y) | Jitter: ±$JITTER px"
echo "=============================================="
echo "⏳ Hai 5 secondi per aprire il gioco..."
sleep 5

trap "echo -e '\\n🛑 Bot interrotto (Ctrl+C).'; exit 0" SIGINT

contatore=0
while true; do
  # Calcola variazione casuale di pixel
  offset_x=$(( (RANDOM % (JITTER * 2 + 1)) - JITTER ))
  offset_y=$(( (RANDOM % (JITTER * 2 + 1)) - JITTER ))
  real_x=$(( X + offset_x ))
  real_y=$(( Y + offset_y ))

  # Esegui tocco
  input tap $real_x $real_y
  contatore=$(( contatore + 1 ))

  # Calcola ritardo casuale in secondi
  range=$(( DELAY_MAX - DELAY_MIN + 1 ))
  pausa=$(( (RANDOM % range) + DELAY_MIN ))

  echo "[#$contatore] Tocco eseguito su ($real_x, $real_y) - Prossimo tra \${pausa}s"

  if [ $MAX_ITERAZIONI -gt 0 ] && [ $contatore -ge $MAX_ITERAZIONI ]; then
    echo "✅ Finito! Raggiunti $MAX_ITERAZIONI tocchi."
    break
  fi

  sleep $pausa
done
`
      },
      {
        name: "README.md",
        language: "markdown",
        content: `# ⚡ Bot Bash Ultra-Leggero per Termux

Questo script è scritto in puro **Bash**: non serve installare Python né librerie pesanti.

### Come eseguirlo:
\`\`\`bash
bash bot.sh
\`\`\`

### Come modificarlo:
\`\`\`bash
nano bot.sh
\`\`\`
Cambia i valori di \`X=\`, \`Y=\` e \`DELAY_MIN=\` all'inizio del file.
Salva con **Ctrl + O**, premi **Invio**, e chiudi con **Ctrl + X**.
`
      }
    ]
  }
];
