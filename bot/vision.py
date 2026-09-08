"""
=============================================================================
👁️ MODULO VISIONE DINAMICA (bot/vision.py)
=============================================================================
Analisi dello schermo Android senza coordinate fisse:
- Cattura screenshot nativo via 'screencap -p'
- Rileva automaticamente l'area di gioco (bounding box della griglia)
- Calcola numero di righe e colonne e determina lo stato di ogni cella:
    0 = MURO (WALL / BLOCKED)
    1 = NON VERNICIATO (UNPAINTED)
    2 = VERNICIATO (PAINTED)
    3 = PALLA / GIOCATORE (PLAYER / BALL)
=============================================================================
"""

import os
import subprocess
import json
import math
try:
    from PIL import Image
    PIL_AVAILABLE = True
except ImportError:
    PIL_AVAILABLE = False
    Image = None

class ScreenAnalyzer:
    def __init__(self, config_path="bot/config.json"):
        self.config = {
            "screencap_path": "/sdcard/screen_temp.png",
            "debug_save": True,
            "crop_top_ratio": 0.20,     # Esclude barra di stato e intestazione livello
            "crop_bottom_ratio": 0.18,  # Esclude pulsanti di fondo / annunci
            "crop_left_ratio": 0.05,
            "crop_right_ratio": 0.05,
        }
        if os.path.exists(config_path):
            try:
                with open(config_path, "r") as f:
                    user_cfg = json.load(f)
                    self.config.update(user_cfg.get("vision", {}))
            except Exception as e:
                print(f"[Vision] Nota: uso configurazione default ({e})")

    def capture_screen(self, output_path=None):
        """
        Cattura lo schermo del dispositivo Android usando il comando nativo 'screencap'.
        Funziona nativamente in Termux tramite ADB o Shizuku.
        """
        if not PIL_AVAILABLE:
            print("[Vision] Errore: libreria Pillow non installata. Esegui 'pkg install python-pillow' o 'pip install Pillow'.")
            return None

        path = output_path or self.config["screencap_path"]
        try:
            # Metodo 1: screencap diretto (se eseguito in shell root o adb shell)
            res = subprocess.run(["screencap", "-p", path], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            if res.returncode != 0:
                # Metodo 2: via adb shell
                subprocess.run(["adb", "shell", "screencap", "-p", path], check=True)
            
            if os.path.exists(path) and os.path.getsize(path) > 1000:
                return Image.open(path).convert("RGB")
        except Exception as e:
            print(f"[Vision] Errore durante la cattura schermo: {e}")
        
        return None

    def sample_color_at(self, img, x, y, radius=5):
        """
        Preleva il colore medio attorno alle coordinate (x, y) per ridurre il rumore.
        """
        r_tot, g_tot, b_tot, count = 0, 0, 0, 0
        w, h = img.size
        for dx in range(-radius, radius + 1):
            for dy in range(-radius, radius + 1):
                px = min(max(0, int(x + dx)), w - 1)
                py = min(max(0, int(y + dy)), h - 1)
                r, g, b = img.getpixel((px, py))
                r_tot += r
                g_tot += g
                b_tot += b
                count += 1
        return (r_tot // count, g_tot // count, b_tot // count)

    @staticmethod
    def color_distance(c1, c2):
        """Calcola la distanza euclidea tra due colori RGB."""
        return math.sqrt((c1[0] - c2[0])**2 + (c1[1] - c2[1])**2 + (c1[2] - c2[2])**2)

    def detect_grid(self, img, rows, cols, grid_box=None):
        """
        Analizza l'immagine e suddivide l'area di gioco in una matrice (righe x colonne).
        Se grid_box non è specificato, lo calcola dinamicamente in base alle percentuali dello schermo.

        Ritorna:
        - matrix: lista di liste con valori (0: muro, 1: non verniciato, 2: verniciato, 3: palla)
        - player_pos: tupla (r, c) con la posizione attuale della palla
        - cell_centers: matrice con le coordinate assolute dello schermo per ogni cella (x, y)
        """
        width, height = img.size
        
        # Bounding box dell'area di gioco
        if grid_box:
            left, top, right, bottom = grid_box
        else:
            left = int(width * self.config.get("crop_left_ratio", 0.08))
            top = int(height * self.config.get("crop_top_ratio", 0.22))
            right = int(width * (1.0 - self.config.get("crop_right_ratio", 0.08)))
            bottom = int(height * (1.0 - self.config.get("crop_bottom_ratio", 0.20)))

        grid_w = right - left
        grid_h = bottom - top
        cell_w = grid_w / cols
        cell_h = grid_h / rows

        matrix = []
        cell_centers = []
        samples = []

        # Campiona il colore centrale di ogni cella
        for r in range(rows):
            row_samples = []
            row_centers = []
            for c in range(cols):
                cx = left + (c + 0.5) * cell_w
                cy = top + (r + 0.5) * cell_h
                col = self.sample_color_at(img, cx, cy, radius=int(min(cell_w, cell_h) * 0.15))
                row_samples.append(col)
                row_centers.append((int(cx), int(cy)))
            samples.append(row_samples)
            cell_centers.append(row_centers)

        # Raggruppamento e classificazione colori
        # Nei giochi come Amaze GO:
        # - Lo sfondo / muri (pareti non percorribili) sono scuri o hanno colore omogeneo di confine.
        # - I percorsi da verniciare sono chiari / bianchi / grigio chiaro.
        # - La vernice è un colore vivo saturo (ad es. arancione, azzurro, verde, viola).
        # - La palla è solitamente del colore della vernice o ha un riflesso brillante/bordo distinto.

        # Calcola luminosità di ogni cella: L = 0.299*R + 0.587*G + 0.114*B
        luminance_map = []
        for r in range(rows):
            row_lum = []
            for c in range(cols):
                rgb = samples[r][c]
                lum = 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]
                row_lum.append(lum)
            luminance_map.append(row_lum)

        # Troviamo la luminosità minima e massima
        all_lums = [lum for row in luminance_map for lum in row]
        min_lum = min(all_lums)
        max_lum = max(all_lums)
        lum_range = max(1.0, max_lum - min_lum)

        # Calcolo varianza cromatica (saturazione)
        saturation_map = []
        for r in range(rows):
            row_sat = []
            for c in range(cols):
                rgb = samples[r][c]
                mx = max(rgb)
                mn = min(rgb)
                sat = (mx - mn) / mx if mx > 0 else 0.0
                row_sat.append(sat)
            saturation_map.append(row_sat)

        player_pos = None
        max_player_score = -1

        for r in range(rows):
            row_vals = []
            for c in range(cols):
                rgb = samples[r][c]
                lum = luminance_map[r][c]
                sat = saturation_map[r][c]

                # Se luminosità molto bassa rispetto allo sfondo -> Muro (0)
                if lum < (min_lum + lum_range * 0.28):
                    row_vals.append(0)  # WALL
                elif sat < 0.20 and lum > (min_lum + lum_range * 0.40):
                    row_vals.append(1)  # UNPAINTED (Pavimento chiaro/bianco)
                else:
                    row_vals.append(2)  # PAINTED (Cella già verniciata)

                # Identificazione posizione giocatore/palla:
                # La palla ha alta saturazione e riflesso centrale distinto
                player_score = sat * 1.5 + (lum / 255.0)
                if row_vals[-1] != 0 and player_score > max_player_score:
                    max_player_score = player_score
                    player_pos = (r, c)

            matrix.append(row_vals)

        # Assegna la palla nella matrice se trovata
        if player_pos:
            pr, pc = player_pos
            matrix[pr][pc] = 3

        return {
            "matrix": matrix,
            "player_pos": player_pos,
            "cell_centers": cell_centers,
            "dimensions": (rows, cols),
            "bounds": (left, top, right, bottom)
        }

    def print_ascii_grid(self, matrix):
        """Stampa la griglia rilevata su terminale Termux con caratteri leggibili."""
        symbols = {
            0: "⬛", # Muro
            1: "⬜", # Da verniciare
            2: "🟧", # Già verniciato
            3: "🔵"  # Palla / Giocatore
        }
        print("\n=== GRIGLIA RILEVATA DALLA VISIONE ===")
        for row in matrix:
            line = "".join(symbols.get(val, "❓") for val in row)
            print(f"  {line}")
        print("Legenda: ⬛=Muro  ⬜=Da colorare  🟧=Colorato  🔵=Palla\n")
