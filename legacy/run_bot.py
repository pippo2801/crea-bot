import shutil
import subprocess
import time

def check_environment():
    print("=======================================================")
    print("🚀 BOT AUTOMAZIONE GIOCHI A GRIGLIA (Termux Edition)")
    print("   Configurazione: 9x9 celle")
    print("   Modalità controller: ADB Locale")
    print("=======================================================")
    
    # Verifica dipendenze
    if not shutil.which('termux-screenshot'):
        print("⚠️ Errore: 'termux-screenshot' non trovato. Esegui: pkg install termux-api")
    if not shutil.which('adb'):
        print("⚠️ Errore: 'adb' non trovato. Esegui: pkg install android-tools")

if __name__ == "__main__":
    check_environment()
