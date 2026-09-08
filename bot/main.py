#!/usr/bin/env python3
"""
=============================================================================
🤖 BOT PRINCIPALE: RISOLUTORE AUTOMATICO PER GIOCHI A GRIGLIA (bot/main.py)
=============================================================================
Progettato per funzionare in modo indipendente su Android tramite Termux.
Funzionalità:
1. Cattura e analisi visiva continua dello schermo di gioco (Amaze GO, Roller Splat, ecc.)
2. Rilevamento automatico delle pareti, celle da verniciare e posizione della palla
3. Risoluzione algoritmica BFS del percorso ideale senza blocchi
4. Esecuzione automatica dei gesti di swipe tramite controller ADB/Termux
5. Supporto per modalità test offline (--test) e calibrazione dinamica
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
    """Verifica se l'utente ha richiesto l'arresto d'emergenza via file."""
    if os.path.exists(stop_path):
        print(f"\n🛑 [STOP] Trovato file di arresto di emergenza: {stop_path}")
        try:
            os.remove(stop_path)
        except Exception:
            pass
        return True
    return False

def run_test_simulation():
    """
    Esegue una simulazione di test con un labirinto sintetico tipico di Amaze GO.
    Consente di verificare la logica di calcolo anche senza un dispositivo collegato.
    """
    print("\n==============================================")
    print("🧪 MODALITÀ TEST SIMULAZIONE (LABIRINTO 7x7)")
    print("==============================================")
    # 0 = Muro, 1 = Da colorare, 3 = Palla
    sample_matrix = [
        [0, 0, 0, 0, 0, 0, 0],
        [0, 3, 1, 1, 1, 1, 0],
        [0, 1, 0, 0, 1, 0, 0],
        [0, 1, 1, 1, 1, 1, 0],
        [0, 0, 1, 0, 0, 1, 0],
        [0, 1, 1, 1, 1, 1, 0],
        [0, 0, 0, 0, 0, 0, 0],
    ]
    player_pos = (1, 1)

    analyzer = ScreenAnalyzer()
    analyzer.print_ascii_grid(sample_matrix)

    print("🧠 Risoluzione BFS del percorso per colorare il 100% delle celle...")
    t0 = time.time()
    solver = MazeSolver(sample_matrix, player_pos)
    solution = solver.solve()
    t_elapsed = time.time() - t0

    if solution:
        print(f"🎉 Percorso trovato in {t_elapsed:.3f}s ({len(solution)} mosse):")
        print("   " + " -> ".join(solution))
    else:
        print("❌ Impossibile trovare una soluzione completa.")
    print("==============================================\n")

def main():
    parser = argparse.ArgumentParser(description="Bot di Automazione Giochi a Griglia per Termux")
    parser.add_argument("--test", action="store_true", help="Esegue un test dimostrativo della logica di risoluzione")
    parser.add_argument("--rows", type=int, default=None, help="Numero di righe della griglia (default da config.json)")
    parser.add_argument("--cols", type=int, default=None, help="Numero di colonne della griglia")
    parser.add_argument("--once", action="store_true", help="Risolve solo il livello corrente e termina")
    args = parser.parse_args()

    if args.test:
        run_test_simulation()
        return

    config = load_config()
    vision_cfg = config.get("vision", {})
    ctrl_cfg = config.get("controller", {})
    auto_cfg = config.get("automation", {})

    rows = args.rows or vision_cfg.get("default_rows", 9)
    cols = args.cols or vision_cfg.get("default_cols", 9)
    stop_path = auto_cfg.get("stop_flag_path", "/sdcard/stop_bot")

    # Gestione chiusura pulita tramite Ctrl+C
    def sigint_handler(sig, frame):
        print("\n\n🛑 Arresto del bot richiesto dall'utente (Ctrl+C). Terminato.")
        sys.exit(0)

    signal.signal(signal.SIGINT, sigint_handler)

    print("=======================================================")
    print("🚀 BOT AUTOMAZIONE GIOCHI A GRIGLIA (Termux Edition)")
    print(f"   Configurazione: {rows}x{cols} celle")
    print(f"   Modalità controller: ADB/Shell nativa")
    print("   Premi Ctrl+C per fermare il bot in qualsiasi momento")
    print("=======================================================")

    analyzer = ScreenAnalyzer()
    controller = AndroidController(ctrl_cfg)
    print(f"ℹ️ Modalità controller rilevata: [{controller.mode.upper()}]")

    level_counter = 1

    while True:
        if check_kill_switch(stop_path):
            break

        print(f"\n--- [ LIVELLO {level_counter} ] Analisi schermo in corso... ---")
        img = analyzer.capture_screen()
        if img is None:
            print("⚠️ Impossibile acquisire lo screenshot. Verifica che ADB o Termux abbiano i permessi necessari.")
            print("   Tentativo di fallback in modalità simulazione...")
            run_test_simulation()
            break

        # Analisi visiva della griglia
        analysis = analyzer.detect_grid(img, rows=rows, cols=cols)
        matrix = analysis["matrix"]
        player_pos = analysis["player_pos"]
        cell_centers = analysis["cell_centers"]

        analyzer.print_ascii_grid(matrix)

        if not player_pos:
            print("⚠️ Posizione della palla non identificata con certezza. Uso il centro della griglia...")
            player_pos = (rows // 2, cols // 2)

        # Risoluzione del labirinto
        solver = MazeSolver(matrix, player_pos)
        moves = solver.solve(max_depth=auto_cfg.get("max_moves_per_level", 40))

        if not moves:
            print("⚠️ Nessun percorso valido identificato. Nuovo tentativo tra 2 secondi...")
            time.sleep(2.0)
            continue

        print(f"🎯 Soluzione calcolata ({len(moves)} mosse): {' -> '.join(moves)}")
        print("🎮 Esecuzione gesti sullo schermo...")

        # Esecuzione mosse
        for idx, move in enumerate(moves, start=1):
            if check_kill_switch(stop_path):
                return

            print(f"  [{idx}/{len(moves)}] Esecuzione swipe: {move}")
            # Esegui lo swipe posizionando il punto di partenza
            pr, pc = player_pos
            if 0 <= pr < len(cell_centers) and 0 <= pc < len(cell_centers[0]):
                cx, cy = cell_centers[pr][pc]
            else:
                w, h = img.size
                cx, cy = w // 2, h // 2

            controller.swipe_direction(move, center_x=cx, center_y=cy)

        level_counter += 1
        if args.once:
            print("✅ Opzione --once specificata. Completato con successo!")
            break

        wait_s = auto_cfg.get("wait_between_levels_s", 2.0)
        print(f"⏳ Livello completato. Attesa di {wait_s}s per la transizione alla schermata successiva...")
        time.sleep(wait_s)

if __name__ == "__main__":
    main()
