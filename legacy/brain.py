import time
import logging
import subprocess
import os
from PIL import Image

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

class ContinuousSudokuBot:
    def __init__(self):
        self.is_running = False
        self.screenshot_path = "current_screen.png"
        
        # --- CONFIGURAZIONE GEOMETRICA SCHERMO (da tarare in base al tuo telefono) ---
        # Definisci i pixel del rettangolo occupato dalla griglia del Sudoku sul tuo schermo
        self.grid_left = 50     # Coordinata X iniziale
        self.grid_top = 400     # Coordinata Y iniziale
        self.grid_right = 1030  # Coordinata X finale
        self.grid_bottom = 1380 # Coordinata Y finale
        
        logging.info("🤖 Bot Sudoku avanzato (Touch + Visione) inizializzato.")

    def start(self):
        self.is_running = True
        logging.info("🚀 Avvio automazione completa. Premi Ctrl+C per fermare.")
        
        level_count = 1
        try:
            while self.is_running:
                logging.info(f"\n--- Livello {level_count} ---")
                
                # 1. Cattura dello schermo
                if not self.capture_screen():
                    time.sleep(3.0)
                    continue

                # 2. Parsing della griglia dall'immagine
                grid = self.parse_grid_from_image()
                if not grid:
                    logging.warning("Impossibile leggere la griglia. Riprovo...")
                    time.sleep(2.0)
                    continue

                # 3. Risoluzione matematica
                # Creiamo una copia di lavoro lasciando intatti gli zeri iniziali o salvando le celle vuote
                working_grid = [row[:] for row in grid]
                
                if self.solve_sudoku(working_grid):
                    logging.info("🎯 Soluzione trovata. Compilazione celle vuote...")
                    # 4. Esecuzione dei tocchi reali solo dove prima c'era uno 0
                    self.execute_touches(grid, working_grid)
                    
                    logging.info("⏳ Attendendo il livello successivo...")
                    time.sleep(4.0)
                    level_count += 1
                else:
                    logging.error("❌ Impossibile risolvere questo schema.")
                    time.sleep(3.0)

                time.sleep(2.0)
                
        except KeyboardInterrupt:
            self.stop()

    def capture_screen(self):
        try:
            if os.path.exists(self.screenshot_path):
                os.remove(self.screenshot_path)
            subprocess.run(["termux-screenshot", self.screenshot_path], check=True)
            return os.path.exists(self.screenshot_path) and os.path.getsize(self.screenshot_path) > 0
        except Exception as e:
            logging.error(f"Errore screenshot: {e}")
            return False

    def parse_grid_from_image(self):
        """Usa PIL per aprire lo screenshot e tagliare le celle della griglia."""
        try:
            img = Image.open(self.screenshot_path)
            width, height = img.size
            logging.info(f"Immagine caricata. Risoluzione schermo: {width}x{height}")
            
            # Qui potremo inserire il ciclo che ritaglia le 81 sotto-immagini delle celle
            # Per adesso, manteniamo la struttura di test per verificare che il flusso funzioni
            return [
                [5, 3, 0, 0, 7, 0, 0, 0, 0],
                [6, 0, 0, 1, 9, 5, 0, 0, 0],
                [0, 9, 8, 0, 0, 0, 0, 6, 0],
                [8, 0, 0, 0, 6, 0, 0, 0, 3],
                [4, 0, 0, 8, 0, 3, 0, 0, 1],
                [7, 0, 0, 0, 2, 0, 0, 0, 6],
                [0, 6, 0, 0, 0, 0, 2, 8, 0],
                [0, 0, 0, 4, 1, 9, 0, 0, 5],
                [0, 0, 0, 0, 8, 0, 0, 7, 9]
            ]
        except Exception as e:
            logging.error(f"Errore nel parsing dell'immagine: {e}")
            return None

    def solve_sudoku(self, grid):
        return self._backtrack(grid)

    def _backtrack(self, grid):
        empty = self._find_empty(grid)
        if not empty:
            return True
        row, col = empty
        for num in range(1, 10):
            if self._is_valid(grid, num, (row, col)):
                grid[row][col] = num
                if self._backtrack(grid):
                    return True
                grid[row][col] = 0
        return False

    def _find_empty(self, grid):
        for i in range(9):
            for j in range(9):
                if grid[i][j] == 0:
                    return (i, j)
        return None

    def _is_valid(self, grid, num, pos):
        row, col = pos
        if num in grid[row]:
            return False
        if num in [grid[i][col] for i in range(9)]:
            return False
        box_x, box_y = col // 3, row // 3
        for i in range(box_y * 3, box_y * 3 + 3):
            for j in range(box_x * 3, box_x * 3 + 3):
                if grid[i][j] == num and (i, j) != pos:
                    return False
        return True

    def execute_touches(self, original_grid, solved_grid):
        """Calcola il centro geometrico di ogni cella vuota e invia il comando tap."""
        cell_width = (self.grid_right - self.grid_left) / 9.0
        cell_height = (self.grid_bottom - self.grid_top) / 9.0

        for r in range(9):
            for c in range(9):
                # Compiliamo solo le celle che all'inizio erano vuote (0)
                if original_grid[r][c] == 0:
                    val = solved_grid[r][c]
                    
                    # Calcola il punto centrale (X, Y) della cella sul display
                    center_x = int(self.grid_left + (c + 0.5) * cell_width)
                    center_y = int(self.grid_top + (r + 0.5) * cell_height)
                    
                    logging.info(f"Tocchję cella [{r},{c}] -> Inserisci {val} alle coordinate ({center_x}, {center_y})")
                    
                    # Comando ADB / Input locale per simulare il tap
                    subprocess.run(["input", "tap", str(center_x), str(center_y)])
                    time.sleep(0.15BREV) # Piccolo ritardo tra un tocco e l'altro per non sovraccaricare il gioco

    def stop(self):
        self.is_running = False
        logging.info("🛑 Bot arrestato.")

if __name__ == "__main__":
    bot = ContinuousSudokuBot()
    bot.start()
