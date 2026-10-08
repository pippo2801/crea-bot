import subprocess
import shutil
import os

def take_screenshot():
    if not shutil.which('termux-screenshot'):
        print("⚠️ Errore: 'termux-screenshot' non trovato nel PATH.")
        return False

    output_file = "screenshot.png"
    if os.path.exists(output_file):
        os.remove(output_file)

    try:
        # Esegue termux-screenshot salvando in un file temporaneo
        subprocess.run(["termux-screenshot", "-f", output_file], check=True)
        if os.path.exists(output_file) and os.path.getsize(output_file) > 0:
            print(f"✅ Screenshot acquisito con successo: {output_file} ({os.path.getsize(output_file)} bytes)")
            return True
        else:
            print("⚠️ Screenshot vuoto o non generato correttamente.")
            return False
    except subprocess.CalledProcessError as e:
        print(f"⚠️ Errore durante l'esecuzione di termux-screenshot: {e}")
        print("Assicurati di aver concesso i permessi e di avere l'app Termux-API installata.")
        return False

if __name__ == "__main__":
    print("Test acquisizione schermo tramite Termux-API...")
    take_screenshot()
