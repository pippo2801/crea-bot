import subprocess
import time

def execute_moves(moves):
    """
    Esegue le mosse forzando la connessione ADB sulla porta locale di Shizuku (35233).
    """
    print(f"🎮 Esecuzione in corso di {len(moves)} mosse tramite Shizuku ADB...")
    
    # Forza la connessione al server ADB locale di Shizuku
    try:
        subprocess.run(["adb", "connect", "localhost:35233"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, timeout=2)
    except Exception:
        pass

    for move in moves:
        print(f"   ↳ Invio comando: {move}")
        
        if move == 'RIGHT':
            coords = ["500", "1000", "900", "1000"]
        elif move == 'LEFT':
            coords = ["900", "1000", "500", "1000"]
        elif move == 'DOWN':
            coords = ["700", "800", "700", "1200"]
        elif move == 'UP':
            coords = ["700", "1200", "700", "800"]
        else:
            continue
            
        cmd = ["adb", "shell", "input", "swipe"] + coords + ["100"]
        
        try:
            subprocess.run(cmd, check=True)
        except Exception as e:
            print(f"   ⚠️ Errore nell'invio del comando ADB: {e}")
            
        time.sleep(0.15)
        
    print("✅ Sequenza di mosse completata.")
