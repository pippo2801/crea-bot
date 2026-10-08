import time
import sys
import os

# Aggiunge la cartella corrente al path per trovare i moduli locali
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

try:
    from vision import take_screenshot, analyze_grid, print_grid_ascii
except ImportError:
    # Fallback se eseguito da directory diverse
    from bot.vision import take_screenshot, analyze_grid, print_grid_ascii

from solver import solve_maze
from controller import execute_moves

def main():
    print("=======================================================")
    print("🚀 BOT AUTOMAZIONE GIOCHI A GRIGLIA (Termux Edition)")
    print("   Configurazione: 9x9 celle")
    print("   Modalità controller: ADB/Shell nativa")
    print("   Premi Ctrl+C per fermare il bot in qualsiasi momento")
    print("=======================================================")
    
    level = 1
    try:
        while True:
            print(f"\n--- [ LIVELLO {level} ] Analisi schermo in corso... ---")
            screenshot_path = "current_screen.png"
            
            # Tenta la cattura dello schermo con termux-screenshot
            success = take_screenshot(screenshot_path)
            
            if success:
                grid = analyze_grid(screenshot_path)
            else:
                print("⚠️ Impossibile acquisire lo screenshot. Verifica che termux-api sia installato.")
                print("   Tentativo di fallback in modalità simulazione...")
                grid = analyze_grid(None)
            
            if grid is None:
                print("❌ Errore critico: impossibile ottenere la griglia di gioco.")
                break
                
            print_grid_ascii(grid)
            
            print("🧠 Risoluzione BFS del percorso per colorare il 100% delle celle...")
            start_time = time.time()
            moves = solve_maze(grid)
            duration = time.time() - start_time
            
            if moves:
                print(f"🎉 Percorso trovato in {duration:.3f}s ({len(moves)} mosse):")
                print("   " + " -> ".join(moves))
                
                # Esegue i movimenti sul dispositivo
                execute_moves(moves)
            else:
                print("⚠️ Nessuna mossa valida trovata per questo schema.")
                
            # Pausa prima del livello successivo
            time.sleep(3)
            level += 1
            
    except KeyboardInterrupt:
        print("\n\n🛑 Bot interrotto manualmente dall'utente. Uscita in corso...")

if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "--test":
        print("\n==============================================")
        print("🧪 MODALITÀ TEST SIMULAZIONE (LABIRINTO 7x7)")
        print("==============================================")
        grid = analyze_grid(None)
        print_grid_ascii(grid)
        print("🧠 Risoluzione BFS del percorso per colorare il 100% delle celle...")
        start_time = time.time()
        moves = solve_maze(grid)
        duration = time.time() - start_time
        if moves:
            print(f"🎉 Percorso trovato in {duration:.3f}s ({len(moves)} mosse):")
            print("   " + " -> ".join(moves))
        print("==============================================")
    else:
        main()
