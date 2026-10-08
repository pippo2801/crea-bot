import subprocess
import shutil
import time
import os

def send_adb_key(direction):
    # Mappa le direzioni ai keycode di Android (DPAD)
    key_map = {
        "UP": "19",
        "DOWN": "20",
        "LEFT": "21",
        "RIGHT": "22"
    }
    
    keycode = key_map.get(direction)
    if not keycode:
        return

    # Se ADB è disponibile, invia il comando nativo
    if shutil.which('adb'):
        try:
            subprocess.run(["adb", "shell", "input", "keyevent", keycode], check=True)
        except subprocess.CalledProcessError as e:
            print(f"   ⚠️ Errore ADB: {e}")
    else:
        print("   ⚠️ Errore: eseguibile 'adb' non trovato nel PATH.")

if __name__ == "__main__":
    print("Test invio comandi di navigazione...")
    # Esempio di test rapido
    for move in ["RIGHT", "DOWN", "LEFT", "UP"]:
        print(f"   ↳ Invio comando: {move}")
        send_adb_key(move)
        time.sleep(0.3)
    print("Test completato.")
