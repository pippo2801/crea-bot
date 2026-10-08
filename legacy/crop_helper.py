from PIL import Image
import os

# Coordinate attuali (da tarare)
grid_left = 50
grid_top = 400
grid_right = 1030
grid_bottom = 1380

screenshot_path = "current_screen.png"
output_path = "grid_crop.png"

if os.path.exists(screenshot_path):
    img = Image.open(screenshot_path)
    # Taglia l'immagine in base al rettangolo specificato
    cropped_img = img.crop((grid_left, grid_top, grid_right, grid_bottom))
    cropped_img.save(output_path)
    print(f"✅ Taglio completato! Immagine salvata come '{output_path}'.")
    print("Aprila per controllare se la griglia del Sudoku è perfettamente racchiusa nel riquadro.")
else:
    print(f"❌ File '{screenshot_path}' non trovato. Assicurati di aver scattato uno screenshot prima.")
