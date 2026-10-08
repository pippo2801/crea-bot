import subprocess
import shutil
import os

def run_fix():
    print("Test diagnostico completo...")
    
    # Test ADB
    if shutil.which('adb'):
        print("✅ ADB trovato nel PATH.")
    
    # Test termux-screenshot
    ts_path = shutil.which('termux-screenshot')
    if ts_path:
        print(f"✅ termux-screenshot trovato in: {ts_path}")
    else:
        print("⚠️ termux-screenshot non è nel PATH di Termux.")
        print("💡 Suggerimento: Puoi usare ADB per fare gli screenshot!")

    # Test screenshot nativo via ADB (molto più affidabile su Termux)
    print("\nTentativo di cattura schermo tramite ADB...")
    try:
        # Cattura sul telefono
        subprocess.run(["adb", "shell", "screencap", "-p", "/sdcard/screenshot.png"], check=True)
        # Scarica sul computer/Termux locale
        subprocess.run(["adb", "pull", "/sdcard/screenshot.png", "screenshot.png"], check=True)
        
        if os.path.exists("screenshot.png") and os.path.getsize("screenshot.png") > 0:
            print(f"✅ Screenshot catturato con successo via ADB! ({os.path.getsize('screenshot.png')} bytes)")
        else:
            print("⚠️ Il file dello screenshot è vuoto.")
    except Exception as e:
        print(f"⚠️ Errore durante la cattura ADB: {e}")
        print("Assicurati di aver abilitato il Debug USB/Wireless sul telefono.")

if __name__ == "__main__":
    run_fix()
