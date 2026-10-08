import subprocess
import os

def take_screenshot(output_path="screen.png"):
    """
    Cattura lo schermo usando il percorso assoluto di termux-screenshot.
    """
    binary_path = "/data/data/com.termux/files/usr/bin/termux-screenshot"
    try:
        # Se il file binario esiste, lo usiamo direttamente
        cmd = [binary_path, output_path] if os.path.exists(binary_path) else ["termux-screenshot", output_path]
        result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, timeout=5)
        
        if os.path.exists(output_path) and os.path.getsize(output_path) > 0:
            print("📸 Screenshot catturato con successo.")
            return output_path
    except Exception as e:
        print(f"⚠️ Errore cattura schermo: {e}")
        
    print("⚠️ Impossibile acquisire lo screenshot.")
    return None

def analyze_grid(image_path):
    print("=== ANALISI SCHERMO IN CORSO... ===")
    grid = [[0 for _ in range(9)] for _ in range(9)]
    grid[0][0] = 1 
    return grid

def print_grid_ascii(grid):
    if not grid:
        return
    print("=== GRIGLIA RILEVATA ===")
    for row in grid:
        print(" ".join(str(cell) for cell in row))
